package io.ylab.intensive.lesson05.eventsourcing.db;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class DbClient {
    private final DataSource dataSource;

    private final Logger LOG = Logger.getAnonymousLogger();

    private enum Operations {
        FIND,
        INSERT,
        UPDATE,
        DELETE
    }

    @Autowired
    public DbClient(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void deletePerson(Long id) {
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

    public void savePerson(Long personId, String firstName, String lastName, String middleName) {
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
}
