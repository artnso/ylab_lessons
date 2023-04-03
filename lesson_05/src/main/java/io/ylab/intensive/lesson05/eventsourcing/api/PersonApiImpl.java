package io.ylab.intensive.lesson05.eventsourcing.api;

import io.ylab.intensive.lesson05.eventsourcing.Message;
import io.ylab.intensive.lesson05.eventsourcing.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonApiImpl implements PersonApi {
    private final String ACTION_DELETE = "delete";

    private final String ACTION_SAVE = "save";

    private final RabbitClient rabbitClient;
    private final DbClient dbClient;

    @Autowired
    public PersonApiImpl(RabbitClient rabbitClient, DbClient dbClient) {
        this.rabbitClient = rabbitClient;
        this.dbClient = dbClient;
    }

    @Override
    public void deletePerson(Long personId) {
        Message message = new Message(ACTION_DELETE, new Person());
        message.getPerson().setId(personId);
        rabbitClient.sendSingleMessage(Message.getJsonString(message));
    }

    @Override
    public void savePerson(Long personId, String firstName, String lastName, String middleName) {
        Message message = new Message(ACTION_SAVE, new Person(personId, firstName, lastName, middleName));
        rabbitClient.sendSingleMessage(Message.getJsonString(message));
    }

    @Override
    public Person findPerson(Long personId) {
        return dbClient.findPerson(personId);
    }

    @Override
    public List<Person> findAll() {
        return dbClient.findAll();
    }
}
