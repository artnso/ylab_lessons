package io.ylab.intensive.lesson05.messagefilter;

import com.rabbitmq.client.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MessageFilterImpl implements MessageFilter{
    private final Logger LOG = Logger.getAnonymousLogger();

    private final String INPUT_QUEUE = "input";
    private final String OUTPUT_QUEUE = "output";

    private final ConnectionFactory connectionFactory;
    private final WordFinder wordFinder;

    @Autowired
    public MessageFilterImpl(ConnectionFactory connectionFactory, WordFinder wordFinder) {
        this.connectionFactory = connectionFactory;
        this.wordFinder = wordFinder;
    }

    private DeliverCallback deliverCallback() {
        return (consumerTag, delivery) -> {
            String message = new String(delivery.getBody());
            System.out.println(" [x] Received '" + message + "'");
            sendFilteredMessage(findWordsInMessageAndMask(message));
        };
    }

    private void sendFilteredMessage(String message){
        try (Connection connection = connectionFactory.newConnection();
             Channel channel = connection.createChannel()) {

            channel.queueDeclare(OUTPUT_QUEUE, true, false, false, null);
            channel.basicPublish("", OUTPUT_QUEUE, null, message.getBytes());
            System.out.println(" [x] Send '" + message + "'");

        } catch (TimeoutException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        } catch (IOException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        }
    }

    private String findWordsInMessageAndMask(String message) {
        String result = message;
        List<String> blackList = new ArrayList<>();
        String[] wordsInMessage = message.split(" ");

        for (int i = 0; i < wordsInMessage.length; i++) {
            wordsInMessage[i] = wordsInMessage[i].trim().replaceAll("[.,;!?]", "");
            if (wordFinder.containsWord(wordsInMessage[i].toLowerCase())){
                blackList.add(wordsInMessage[i]);
            }
        }

        for (String word: blackList) {
            result = result.replaceAll(word, maskWord(word));
        }

        return result;
    }

    private String maskWord (String word){
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (i != 0 && i != word.length() - 1) {
                sb.append("*");
            } else {
                sb.append(word.charAt(i));
            }
        }
        return sb.toString();
    }

    private void connectToInputQueue(){
        try {
            Connection connection = this.connectionFactory.newConnection();
            Channel inputChannel = connection.createChannel();
            inputChannel.queueDeclare(INPUT_QUEUE, true, false, false, null);
            boolean autoAck = true;
            inputChannel.basicConsume(INPUT_QUEUE, autoAck, this.deliverCallback(), consumerTag -> {});
        } catch (TimeoutException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        } catch (IOException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        }
    }

    @Override
    public void doFilter() {
        connectToInputQueue();
        LOG.log(Level.INFO, "Filtering started...");
    }
}
