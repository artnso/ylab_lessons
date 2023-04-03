package io.ylab.intensive.lesson05.eventsourcing.db;

import io.ylab.intensive.lesson05.eventsourcing.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class MessageProcessor {
    private final RabbitClient rabbitClient;
    private final DbClient dbClient;
    private final Logger LOG = Logger.getAnonymousLogger();

    @Autowired
    public MessageProcessor(RabbitClient rabbitClient, DbClient dbClient) {
        this.rabbitClient = rabbitClient;
        this.dbClient = dbClient;
    }


    public void start() {
        LOG.log(Level.INFO, " [x] Start listening messages...");
        while (!Thread.currentThread().isInterrupted()) {
            processSingleMessage(rabbitClient.getSingleMessage());
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                LOG.log(Level.WARNING, ex.getMessage());
            }

        }
    }

    private void processSingleMessage(Message message) {
        if (message == null) {
            return;
        }
        if (message.getAction().equals("save")){
            dbClient.savePerson(message.getPerson().getId(),
                    message.getPerson().getName(),
                    message.getPerson().getLastName(),
                    message.getPerson().getLastName());

        } else if (message.getAction().equals("delete")) {
            dbClient.deletePerson(message.getPerson().getId());
        }
    }
}
