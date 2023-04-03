package io.ylab.intensive.lesson05.eventsourcing.db;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.*;
import io.ylab.intensive.lesson05.eventsourcing.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RabbitClient {
    private final ConnectionFactory connectionFactory;
    private final DbClient dbClient;
    private final String EXCHANGE_NAME = "exc";
    private final String QUEUE_NAME = "queue";
    private final String DIRECT_KEY = "direct";
    private final Logger LOG = Logger.getAnonymousLogger();

    @Autowired
    public RabbitClient(ConnectionFactory connectionFactory, DbClient dbClient) {
        this.connectionFactory = connectionFactory;
        this.dbClient = dbClient;
    }

    public void processSingleMessage(){
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()){

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, DIRECT_KEY);

            GetResponse response = channel.basicGet(QUEUE_NAME, true);
            if (response != null) {
                String json = new String(response.getBody());
                Message message = new ObjectMapper().readValue(json, Message.class);
                if (message.getAction().equals("save")){
                    dbClient.savePerson(message.getPerson().getId(),
                            message.getPerson().getName(),
                            message.getPerson().getLastName(),
                            message.getPerson().getLastName());

                } else if (message.getAction().equals("delete")) {
                    dbClient.deletePerson(message.getPerson().getId());
                }
            }
        } catch (IOException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        } catch (TimeoutException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        }
    }
}
