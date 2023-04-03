package io.ylab.intensive.lesson05.eventsourcing.api;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class RabbitClient {
    private final ConnectionFactory connectionFactory;
    private final String EXCHANGE_NAME = "exc";
    private final String QUEUE_NAME = "queue";
    private final String DIRECT_KEY = "direct";

    @Autowired
    public RabbitClient(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void sendSingleMessage(String message){
        try (com.rabbitmq.client.Connection connection = this.connectionFactory.newConnection();
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

}
