package io.ylab.intensive.lesson04.eventsourcing.db;

import java.sql.*;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import io.ylab.intensive.lesson04.DbUtil;
import io.ylab.intensive.lesson04.RabbitMQUtil;
import io.ylab.intensive.lesson04.eventsourcing.Message;

public class DbApp {
  public static void main(String[] args) throws Exception {
    DataSource dataSource = initDb();
    ConnectionFactory connectionFactory = initMQ();

    // тут пишем создание и запуск приложения работы с БД
    String EXCHANGE_NAME = "exc";
    String QUEUE_NAME = "queue";
    String DIRECT_KEY = "direct";

    try (com.rabbitmq.client.Connection connection = connectionFactory.newConnection();
         Channel channel = connection.createChannel()) {

      channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, DIRECT_KEY);

      // Далее код приема сообщений

      while (!Thread.currentThread().isInterrupted()) {
        GetResponse response = channel.basicGet(QUEUE_NAME, true);
        if (response == null) {
          // no message
          Thread.sleep(500);
        } else {
          String json = new String(response.getBody());
          Message message = new ObjectMapper().readValue(json, Message.class);
          if (message.getAction().equals("save")){
            savePerson(dataSource, message.getPerson().getId(),
                    message.getPerson().getName(),
                    message.getPerson().getLastName(),
                    message.getPerson().getLastName());

          } else if (message.getAction().equals("delete")) {
            deletePerson(dataSource, message.getPerson().getId());
          }
          Thread.sleep(1000); //
        }
      }
    }
  }
  
  private static ConnectionFactory initMQ() throws Exception {
    return RabbitMQUtil.buildConnectionFactory();
  }
  
  private static DataSource initDb() throws SQLException {
    String ddl = "" 
                     + "drop table if exists person;" 
                     + "create table if not exists person (\n"
                     + "person_id bigint primary key,\n"
                     + "first_name varchar,\n"
                     + "last_name varchar,\n"
                     + "middle_name varchar\n"
                     + ")";
    DataSource dataSource = DbUtil.buildDataSource();
    DbUtil.applyDdl(ddl, dataSource);
    return dataSource;
  }

  private enum Operations {
    FIND,
    INSERT,
    UPDATE,
    DELETE
  }

  private static void deletePerson(DataSource dataSource, Long Id){
    String query = "delete from person where person_id = ?;";
    try {
      if (!containsPerson(dataSource, Id)) {
        Logger.getAnonymousLogger().log(Level.INFO, "Try to delete person. Person with id = " + Id + " not found");
        return;
      }
      executeQuery(dataSource, Operations.DELETE, query, Id.toString());
      Logger.getAnonymousLogger().log(Level.INFO, "Person with id = " + Id + " deleted");
    } catch (SQLException ex) {
      Logger.getAnonymousLogger().log(Level.WARNING, ex.getMessage());
    }
  }

  private static void savePerson(DataSource dataSource, Long personId, String firstName,
                                 String lastName, String middleName) throws SQLException {
    String query_insert = "insert into person (first_name, last_name, middle_name, person_id) values (?, ?, ?, ?);";
    String query_update = "update person set first_name = ?," +
            " last_name = ?, middle_name = ? where person_id = ?;";

    if (containsPerson(dataSource, personId)) {
      executeQuery(dataSource, Operations.UPDATE, query_update,
              firstName, lastName, middleName, personId.toString());
    } else {
      executeQuery(dataSource, Operations.INSERT, query_insert,
              firstName, lastName, middleName, personId.toString());
    }
    Logger.getAnonymousLogger().log(Level.INFO, "Person " + firstName + " "
            + middleName + " " + lastName + " saved");
  }

  private static boolean containsPerson(DataSource dataSource, Long personId) throws SQLException{
    String query = "select person_id from person where person_id = ?;";
    List<String> queryResult;

    queryResult = executeQuery(dataSource, Operations.FIND, query, personId.toString());
    return (queryResult.size() > 0);
  }

  private static List<String> executeQuery(DataSource dataSource, Operations operation, String query, String... args) throws SQLException{
    List<String> result = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(query)){

      if (operation == Operations.FIND || operation == Operations.DELETE){
        for (int i = 0; i < args.length; i++) {
          if (i == 0) {
            preparedStatement.setLong(i+1, Long.parseLong(args[i]));
          } else {
            preparedStatement.setString(i+1, args[i]);
          }
        }
      } else if (operation == Operations.UPDATE || operation == Operations.INSERT) {
        for (int i = 0; i < args.length; i++) {
          if (i == args.length - 1) {
            preparedStatement.setLong(i+1, Long.parseLong(args[i]));
          } else {
            if (args[i] == null) {
              preparedStatement.setNull(i+1, Types.VARCHAR);
            } else {
              preparedStatement.setString(i+1, args[i]);
            }
          }
        }
      }

      preparedStatement.execute();
      ResultSet rs = preparedStatement.getResultSet();
      if (rs != null){
        while (rs.next()){
          result.add(rs.getString(1));
        }
        rs.close();
      }
    }
    return result;
  }
}
