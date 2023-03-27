package io.ylab.intensive.lesson04.persistentmap;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;

/**
 * Класс, методы которого надо реализовать 
 */
public class PersistentMapImpl implements PersistentMap {
  
  private DataSource dataSource;
  private String name;

  public PersistentMapImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void init(String name) {
    this.name = name;
  }

  @Override
  public boolean containsKey(String key) throws SQLException {
    String query = "select value from persistent_map where map_name = ? and KEY = ?;";
    List<String> queryResult;

    queryResult = executeQuery(query, this.name, key);
    return (queryResult.size() > 0);
  }

  @Override
  public List<String> getKeys() throws SQLException {
    String query = "select KEY from persistent_map where map_name = ?;";
    return executeQuery(query, this.name);
  }

  @Override
  public String get(String key) throws SQLException {
    String query = "select value from persistent_map where map_name = ? and KEY = ?;";
    List<String> queryResult = executeQuery(query, this.name, key);

    String result = queryResult.size() > 0 ? queryResult.get(0) : ""; // or null??
    return result;
  }

  @Override
  public void remove(String key) throws SQLException {
    String query = "delete from persistent_map where map_name = ? and KEY = ?;";
    executeQuery(query, this.name, key);
  }

  @Override
  public void put(String key, String value) throws SQLException {
    String query = "insert into persistent_map (map_name, KEY, value) values (?, ?, ?);";

    if (containsKey(key)) {
      remove(key);
    } else {
      executeQuery(query, this.name, key, value);
    }
  }

//  Реализация метода посредством обновления значения value для ключа key
//
//  @Override
//  public void put(String key, String value) throws SQLException {
//    String sql_insert = "insert into persistent_map (map_name, KEY, value) values (?, ?, ?);";
//    String sql_update = "update persistent_map set value = ? where map_name = ? and KEY = ?;";
//
//    if (containsKey(key)) {
//      executeQuery(sql_update, value, this.name, key);
//    } else {
//      executeQuery(sql_insert, this.name, key, value);
//    }
//  }

  @Override
  public void clear() throws SQLException {
    String query = "delete from persistent_map where map_name = ?;";
    executeQuery(query, this.name);
  }

  private List<String> executeQuery(String sql, String... args) throws SQLException{
    List<String> result = new ArrayList<>();

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)){
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
}
