package io.ylab.intensive.lesson05.sqlquerybuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component
public class SQLQueryBuilderImpl implements SQLQueryBuilder{
    private final DataSource dataSource;

    @Autowired
    public SQLQueryBuilderImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private String buildQueryString(List<String> columns, String tableName) {
        String result;
        if (columns.size() == 0) {
            Logger.getAnonymousLogger().log(Level.INFO, "Таблица " + tableName + " не содежит столбцов");
            result = null;
        } else {
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
            result = sb.toString();
        }
        return result;
    }

    @Override
    public String queryForTable(String tableName) throws SQLException {
        List<String> result = new ArrayList<>();
        try (java.sql.Connection connection = this.dataSource.getConnection()){
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet columns = metaData.getColumns(null, null, tableName, null);
            while (columns.next()){
                result.add(columns.getString("COLUMN_NAME"));
            }
        }
        return buildQueryString(result, tableName);
    }

    @Override
    public List<String> getTables() throws SQLException {
        List<String> result = new ArrayList<>();
        try (java.sql.Connection connection = this.dataSource.getConnection()){
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, null, "%", null);
            while (tables.next()){
                result.add(tables.getString("Table_NAME"));
            }
        }
        return result;
    }
}
