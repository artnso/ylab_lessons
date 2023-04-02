package io.ylab.intensive.lesson05.sqlquerybuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Component
public class SQLQueryBuilderImpl implements SQLQueryBuilder{
    private final String SEARCH_TABLE = "select tablename from pg_tables where tablename = ?;";

    //"select tablename from pg_tables where schemaname = 'public';";
    private final String LIST_TABLES = "select tablename from pg_tables where schemaname <> 'information_schema';";

    private final DataSource dataSource;

    @Autowired
    public SQLQueryBuilderImpl(DataSource dataSource) {
        this.dataSource = dataSource;
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

    private List<String> getColumnsNames(String query) throws SQLException {
        List<String> result = new ArrayList<>();
        try (java.sql.Connection connection = this.dataSource.getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)){

            preparedStatement.execute();

            ResultSet rs = preparedStatement.getResultSet();
            int columnCount = rs.getMetaData().getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                result.add(rs.getMetaData().getColumnName(i));
            }
        }
        return result;
    }

    private String buildQueryString(List<String> columns, String tableName) {
        StringBuilder sb = new StringBuilder("select ");
        for (int i = 0; i < columns.size(); i++) {
            if (i < columns.size() - 1) {
                sb.append(columns.get(i));
                sb.append(", ");
            } else {
                sb.append(columns.get(i));
            }
        }
        sb.append(" from ");
        sb.append(tableName);
        sb.append(";");
        return sb.toString();
    }

    @Override
    public String queryForTable(String tableName) throws SQLException {
        List<String> tables = executeQuery(SEARCH_TABLE, tableName);

        if (tables.size() > 0) {
            String getFieldsQuery = "select * from tableName LIMIT 1;".replaceAll("tableName", tableName);
            List<String> columns = getColumnsNames(getFieldsQuery);
            return buildQueryString(columns, tableName);
        } else {
            return null;
        }
    }

    @Override
    public List<String> getTables() throws SQLException {
        return executeQuery(LIST_TABLES);
    }
}
