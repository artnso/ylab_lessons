package io.ylab.intensive.lesson05.eventsourcing.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;
import io.ylab.intensive.lesson05.eventsourcing.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class DBExecutorImpl implements DBExecutor {
    private final DataSource dataSource;
    private final ConnectionFactory connectionFactory;

    private final String EXCHANGE_NAME = "exc";
    private final String QUEUE_NAME = "queue";
    private final String DIRECT_KEY = "direct";

    private final Logger LOG = Logger.getAnonymousLogger();

    private enum Operations {
        FIND,
        INSERT,
        UPDATE,
        DELETE
    }

    @Autowired
    public DBExecutorImpl(DataSource dataSource, ConnectionFactory connectionFactory) {
        this.dataSource = dataSource;
        this.connectionFactory = connectionFactory;
    }

    private void deletePerson(Long id) {
        String query = "delete from person where person_id = ?;";
        try {
            if (!containsPerson(id)) {
                LOG.log(Level.INFO, "Try to delete person. Person with id = " + id + " not found");
                return;
            }
            executeQuery(Operations.DELETE, query, id.toString());
            LOG.log(Level.INFO, "Person with id = " + id + " deleted");
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        }
    }

    private void savePerson(Long personId, String firstName, String lastName, String middleName) {
        String query_insert = "insert into person (first_name, last_name, middle_name, person_id) values (?, ?, ?, ?);";
        String query_update = "update person set first_name = ?," +
                " last_name = ?, middle_name = ? where person_id = ?;";

        try {
            if (containsPerson(personId)) {
                executeQuery(Operations.UPDATE, query_update,
                        firstName, lastName, middleName, personId.toString());
            } else {
                executeQuery(Operations.INSERT, query_insert,
                        firstName, lastName, middleName, personId.toString());
            }
            Logger.getAnonymousLogger().log(Level.INFO, "Person " + firstName + " "
                    + middleName + " " + lastName + " saved");
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        }
    }

    private boolean containsPerson(Long personId) throws SQLException {
        String query = "select person_id from person where person_id = ?;";
        List<String> queryResult;

        queryResult = executeQuery(Operations.FIND, query, personId.toString());
        return (queryResult.size() > 0);
    }

    private List<String> executeQuery(Operations operation, String query, String... args) throws SQLException{
        List<String> result = new ArrayList<>();

        try (Connection connection = this.dataSource.getConnection();
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

    @Override
    public void doExecute(){
        try {
            com.rabbitmq.client.Connection connection = this.connectionFactory.newConnection();
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, DIRECT_KEY);

            boolean autoAck = true;
            LOG.log(Level.INFO, " [x] Start listening queue...");
            channel.basicConsume(QUEUE_NAME, autoAck, this.deliverCallback(), consumerTag -> {});

        } catch (TimeoutException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        } catch (IOException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        }
    }

    private DeliverCallback deliverCallback() {
        return (consumerTag, delivery) -> {
            String json = new String(delivery.getBody());
            Message message = new ObjectMapper().readValue(json, Message.class);
            if (message.getAction().equals("save")){
                savePerson(message.getPerson().getId(),
                        message.getPerson().getName(),
                        message.getPerson().getLastName(),
                        message.getPerson().getLastName());

            } else if (message.getAction().equals("delete")) {
                deletePerson(message.getPerson().getId());
            }
        };
    }
}
