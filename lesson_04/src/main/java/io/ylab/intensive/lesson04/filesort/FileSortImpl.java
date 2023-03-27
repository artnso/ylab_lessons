package io.ylab.intensive.lesson04.filesort;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class FileSortImpl implements FileSorter {
  private final String QUERY_SORT = "select val from numbers order by val desc";
  private final String QUERY_INSERT = "insert into numbers (val) values (?);";
  private final int BATCH_SIZE = 10_000;

  private DataSource dataSource;

  public FileSortImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public File sort(File data) {
    // ТУТ ПИШЕМ РЕАЛИЗАЦИЮ
    File outData = null;
    try {

      Logger.getAnonymousLogger().log(Level.INFO, "Старт записи данных в БД");
      insertDataIntoDB(data);
//      insertDataIntoDBWithoutBatch(data); Функция без batch-processing
      Logger.getAnonymousLogger().log(Level.INFO, "Запись данных в БД окончена. Старт сортировки данных");
      outData =  getSortedFile(data);
      Logger.getAnonymousLogger().log(Level.INFO, "Данные отсортированы");

    } catch (IOException ex) {
      System.out.println(ex.getMessage());

    } catch (SQLException ex) {
      System.out.println(ex.getMessage());
    }
    return outData;
  }

  private void insertDataIntoDB(File data) throws SQLException, IOException {
    try (Scanner scanner = new Scanner(data);
         Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(this.QUERY_INSERT)) {

      int batchCounter = 0;

      while (scanner.hasNextLong()){
        long number = scanner.nextLong();
        preparedStatement.setLong(1, number);
        preparedStatement.addBatch();
        batchCounter += 1;

        if (batchCounter == BATCH_SIZE - 1){
          preparedStatement.executeBatch();
          batchCounter = 0;
        }
      }
    }
  }

  private void insertDataIntoDBWithoutBatch(File data) throws SQLException, IOException {
    try (Scanner scanner = new Scanner(data);
         Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(this.QUERY_INSERT)) {

      while (scanner.hasNextLong()){
        long number = scanner.nextLong();
        preparedStatement.setLong(1, number);
        preparedStatement.executeUpdate();
      }
    }
  }

  private File getSortedFile(File data) throws SQLException, IOException {
    try (PrintWriter pw = new PrintWriter(data);
         Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(this.QUERY_SORT);
         ResultSet rs = preparedStatement.executeQuery()) {

      while (rs.next()){
        pw.println(rs.getLong(1));
      }

      pw.flush();
      return data;
    }
  }
}
