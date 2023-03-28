package io.ylab.intensive.lesson04.eventsourcing.api;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import io.ylab.intensive.lesson04.eventsourcing.Message;
import io.ylab.intensive.lesson04.eventsourcing.Person;

import javax.sql.DataSource;

/**
 * Тут пишем реализацию
 */
public class PersonApiImpl implements PersonApi {
  private final String EXCHANGE_NAME = "exc";
  private final String QUEUE_NAME = "queue";
  private final String DIRECT_KEY = "direct";
  private final String ACTION_DELETE = "delete";

  private final String ACTION_SAVE = "save";

  private final ConnectionFactory connectionFactory;
  private final DataSource dataSource;

  public PersonApiImpl(DataSource dataSource, ConnectionFactory connectionFactory){
    this.dataSource = dataSource;
    this.connectionFactory = connectionFactory;
  }

  @Override
  public void deletePerson(Long personId) {
    Message message = new Message(ACTION_DELETE, new Person());
    message.getPerson().setId(personId);
    sendMessage(Message.getJsonString(message));
  }

  @Override
  public void savePerson(Long personId, String firstName, String lastName, String middleName) {
    Message message = new Message(ACTION_SAVE, new Person(personId, firstName, lastName, middleName));
    sendMessage(Message.getJsonString(message));
  }

  @Override
  public Person findPerson(Long personId) {
    String query = "select person_id, first_name, last_name, middle_name from person where person_id = ?;";
    List<Map<String, String>> persons;
    try {
       persons = executeQuery(query, personId.toString());
    } catch (SQLException ex) {
      Logger.getAnonymousLogger().log(Level.WARNING, ex.getMessage());
      return null;
    }

    if (persons.size() == 0) {
      return null;
    } else {

      Map<String, String> person = persons.get(0);
      return new Person(Long.parseLong(person.get("person_id")),
              person.get("first_name"),
              person.get("last_name"),
              person.get("middle_name"));
    }
  }

  @Override
  public List<Person> findAll() {
    String query = "select person_id, first_name, last_name, middle_name from person";

    List<Map<String, String>> persons = Collections.emptyList();
    try {
      persons = executeQuery(query);
    } catch (SQLException ex) {
      Logger.getAnonymousLogger().log(Level.WARNING, ex.getMessage());
    }

    if (persons.size() == 0) {
      return Collections.emptyList();
    } else {

      List<Person> result =new ArrayList<>();
      for (Map<String, String> person: persons){
        result.add(new Person(Long.parseLong(person.get("person_id")),
                person.get("first_name"),
                person.get("last_name"),
                person.get("middle_name")));
      }
      return result;
    }
  }

  private void sendMessage(String message) {
    try (com.rabbitmq.client.Connection connection = connectionFactory.newConnection();
         Channel channel = connection.createChannel()) {

      channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
      channel.queueDeclare(QUEUE_NAME, true, false, false, null);
      channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, DIRECT_KEY);

      // Далее код публикации сообщений
      channel.basicPublish(EXCHANGE_NAME, DIRECT_KEY, null, message.getBytes());
      Logger.getAnonymousLogger().log(Level.INFO, " [x] Sent '" + message + "'");

    } catch (Exception ex) {
      Logger.getAnonymousLogger().log(Level.WARNING, ex.getMessage());
    }
  }

  private List<Map<String, String>> executeQuery(String sql, String... args) throws SQLException {
    List<Map<String, String>> result = Collections.emptyList();

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)){
      for (int i = 0; i < args.length; i++) {
        if (i == args.length - 1) {
          preparedStatement.setLong(i+1, Long.parseLong(args[i]));
        } else{
          preparedStatement.setString(i+1, args[i]);
        }
      }

      preparedStatement.execute();
      ResultSet rs = preparedStatement.getResultSet();
      if (rs != null){
        result = new ArrayList<>();
        while (rs.next()){
          Map<String, String> record = Map.ofEntries(
                  Map.entry("person_id", rs.getString(1)),
                  Map.entry("first_name", rs.getString(2)),
                  Map.entry("last_name", rs.getString(3)),
                  Map.entry("middle_name", rs.getString(4)));
          result.add(record);
        }
        rs.close();
      }
    }

    return result;
  }
}
