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
    private final String EXCHANGE_NAME = "exc";
    private final String QUEUE_NAME = "queue";
    private final String DIRECT_KEY = "direct";
    private final Logger LOG = Logger.getAnonymousLogger();

    @Autowired
    public RabbitClient(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public Message getSingleMessage() {
        Message message = null;
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()){

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, DIRECT_KEY);

            GetResponse response = channel.basicGet(QUEUE_NAME, true);
            if (response != null) {
                String json = new String(response.getBody());
                message = new ObjectMapper().readValue(json, Message.class);
            }
        } catch (IOException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
            return message;
        } catch (TimeoutException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
            return message;
        }
        return message;
    }
}
