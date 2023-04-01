package io.ylab.intensive.lesson04.eventsourcing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Message {
    private String action;
    private Person person;

    public Message(){

    }

    public Message(String action, Person person) {
        this.action = action;
        this.person = person;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public static String getJsonString(Message message){
        String json = "";
        try {
            json = new ObjectMapper().writeValueAsString(message);
        } catch (JsonProcessingException ex) {
            Logger.getAnonymousLogger().log(Level.WARNING, ex.getMessage());
        }
        return json;
    }
}
