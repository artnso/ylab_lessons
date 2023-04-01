package io.ylab.intensive.lesson04.persistentmap;

import java.sql.SQLException;
import javax.sql.DataSource;

import io.ylab.intensive.lesson04.DbUtil;

public class PersistenceMapTest {
  public static void main(String[] args) throws SQLException {
    DataSource dataSource = initDb();
    PersistentMap persistentMap = new PersistentMapImpl(dataSource);
    // Написать код демонстрации работы
    persistentMap.init("map_1");
    persistentMap.put("k1", "v1");
    System.out.println(persistentMap.get("k1"));
    persistentMap.put("k2", "v2");
    System.out.println(persistentMap.get("k2"));
    persistentMap.put("k3", "v4");
    System.out.println(persistentMap.getKeys());
    persistentMap.put("k1", "v5");
    persistentMap.remove("k2");
    System.out.println(persistentMap.getKeys());
    persistentMap.clear();
    System.out.println(persistentMap.getKeys());
    persistentMap.init("map_2");
    persistentMap.remove("k2");

  }
  
  public static DataSource initDb() throws SQLException {
    String createMapTable = "" 
                                + "drop table if exists persistent_map; " 
                                + "CREATE TABLE if not exists persistent_map (\n"
                                + "   map_name varchar,\n"
                                + "   KEY varchar,\n"
                                + "   value varchar\n"
                                + ");";
    DataSource dataSource = DbUtil.buildDataSource();
    DbUtil.applyDdl(createMapTable, dataSource);
    return dataSource;
  }
}
