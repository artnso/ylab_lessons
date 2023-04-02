package io.ylab.intensive.lesson05.messagefilter;

import io.ylab.intensive.lesson05.DbUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class DBWordFinderImpl implements WordFinder{
    private final Logger LOG = Logger.getAnonymousLogger();
    private final DataSource dataSource;
    private final int BATCH_SIZE = 10_000;
    private final String BLACK_LIST = "filter.dat";

    @Autowired
    public DBWordFinderImpl(DataSource dataSource) {
        this.dataSource = dataSource;
        initDB();
    }

    @Override
    public boolean containsWord(String word) {
        String query = "select word from words where word = ?;";
        List<String> queryResult;

        try {
            queryResult = executeQuery(query, word);
            return (queryResult.size() > 0);
        } catch (SQLException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
            return false;
        }
    }

    private List<String> executeQuery(String query, String... args) throws SQLException {
        List<String> result = new ArrayList<>();
        try (java.sql.Connection connection = this.dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)){

            for (int i = 0; i < args.length; i++) {
                preparedStatement.setString(i+1, args[i]);
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

    private void initDB(){
        String createWordsTable = "drop table if exists words;"
                + "CREATE TABLE if not exists words (\n"
                + "\tword varchar\n"
                + ");";
        try {
            DbUtil.applyDdl(createWordsTable, this.dataSource);
        } catch (SQLException ex){
            LOG.log(Level.WARNING, ex.getMessage());
        }

        try {
            insertDataIntoDB();
        } catch (SQLException ex){
            LOG.log(Level.WARNING, ex.getMessage());
        } catch (IOException ex) {
            LOG.log(Level.WARNING, ex.getMessage());
        }
    }

    private void insertDataIntoDB() throws SQLException, IOException {
        String query = "insert into words (word) values (?);";
        try (BufferedReader br = new BufferedReader(new FileReader(BLACK_LIST));
             java.sql.Connection connection = dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            int batchCounter = 0;
            String line;
            while ((line = br.readLine()) != null){
                preparedStatement.setString(1, line.toLowerCase());
                preparedStatement.addBatch();
                batchCounter += 1;

                if (batchCounter == BATCH_SIZE - 1){
                    preparedStatement.executeBatch();
                    batchCounter = 0;
                }
            }
            preparedStatement.executeBatch();
        }
    }
}
