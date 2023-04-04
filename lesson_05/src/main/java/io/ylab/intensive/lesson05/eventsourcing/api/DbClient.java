package io.ylab.intensive.lesson05.eventsourcing.api;

import io.ylab.intensive.lesson05.eventsourcing.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
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

@Component
public class DbClient {
    private final DataSource dataSource;

    private final Logger LOG = Logger.getAnonymousLogger();

    @Autowired
    public DbClient(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public Person findPerson(Long personId) {
        String query = "select person_id, first_name, last_name, middle_name from person where person_id = ?;";
        List<Map<String, String>> persons;
        try {
            persons = executeQuery(query, personId.toString());
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
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

            List<Person> result = new ArrayList<>();
            for (Map<String, String> person: persons){
                result.add(new Person(Long.parseLong(person.get("person_id")),
                        person.get("first_name"),
                        person.get("last_name"),
                        person.get("middle_name")));
            }
            return result;
        }
    }

    private List<Map<String, String>> executeQuery(String sql, String... args) throws SQLException {
        List<Map<String, String>> result = Collections.emptyList();

        try (Connection connection = this.dataSource.getConnection();
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
