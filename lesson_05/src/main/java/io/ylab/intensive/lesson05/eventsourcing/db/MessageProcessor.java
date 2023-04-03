package io.ylab.intensive.lesson05.eventsourcing.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MessageProcessor {
    private final RabbitClient rabbitClient;
    private final Logger LOG = Logger.getAnonymousLogger();

    @Autowired
    public MessageProcessor(RabbitClient rabbitClient) {
        this.rabbitClient = rabbitClient;
    }

    public void start() {
        LOG.log(Level.INFO, " [x] Start listening messages...");
        while (!Thread.currentThread().isInterrupted()) {
            rabbitClient.processSingleMessage();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                LOG.log(Level.WARNING, ex.getMessage());
            }

        }
    }
}
