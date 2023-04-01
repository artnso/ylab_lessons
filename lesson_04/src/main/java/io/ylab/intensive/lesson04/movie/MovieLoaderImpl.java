package io.ylab.intensive.lesson04.movie;

import java.io.File;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sql.DataSource;

public class MovieLoaderImpl implements MovieLoader {
  private DataSource dataSource;

  public MovieLoaderImpl(DataSource dataSource) {
    this.dataSource = dataSource;
  }

  @Override
  public void loadData(File file) {
    // РЕАЛИЗАЦИЮ ПИШЕМ ТУТ
    String sql="insert into movie " +
            "(year, length, title, subject, actors, actress, director, popularity, awards) " +
            "values(?,?,?,?,?,?,?,?,?);";

    List<Movie> movies = getMoviesFromCsv(file);

    try (Connection connection = dataSource.getConnection();
         PreparedStatement preparedStatement = connection.prepareStatement(sql)) {

        for (Movie movie: movies) {
            setupPreparedStatement(movie, preparedStatement);
            preparedStatement.executeUpdate();
        }

    } catch (SQLException ex) {
        System.out.println(ex.getMessage());
    }
  }

  private List<Movie> getMoviesFromCsv(File file) {
    try (Scanner scanner = new Scanner(file, "cp1251")){
      List<Movie> movies = new ArrayList<>();
      int lineToSkip = 2;

      while (scanner.hasNext()) {
        String line = scanner.nextLine();
        if (lineToSkip > 0) {
          lineToSkip -= 1;
          continue;
        }
        movies.add(buildMovie(line.split(";")));
      }
      return movies;
    } catch (IOException ex){
      System.out.println(ex.getMessage());
      return Collections.emptyList();
    }
  }

  private Movie buildMovie(String... record){
    Movie movie = new Movie();
    try {
      movie.setYear(record[0].isEmpty() ? null : Integer.parseInt(record[0]));
      movie.setLength(record[1].isEmpty() ? null : Integer.parseInt(record[1]));
      movie.setTitle(record[2]);
      movie.setSubject(record[3]);
      movie.setActors(record[4]);
      movie.setActress(record[5]);
      movie.setDirector(record[6]);
      movie.setPopularity(record[7].isEmpty() ? null : Integer.parseInt(record[7]));
      movie.setAwards(record[8].equals("Yes"));
    } catch (IndexOutOfBoundsException ex) {
      Logger.getAnonymousLogger().log(Level.WARNING, ex.getMessage());
      return movie;
    }
    return movie;
  }

  private void setupPreparedStatement(Movie movie, PreparedStatement preparedStatement) throws SQLException {
    if (movie.getYear() == null) {
      preparedStatement.setNull(1, Types.INTEGER);
    } else {
      preparedStatement.setInt(1, movie.getYear());
    }

    if (movie.getLength() == null) {
      preparedStatement.setNull(2, Types.INTEGER);
    } else {
      preparedStatement.setInt(2, movie.getLength());
    }

    preparedStatement.setString(3, movie.getTitle());
    preparedStatement.setString(4, movie.getSubject());
    preparedStatement.setString(5, movie.getActors());
    preparedStatement.setString(6, movie.getActress());
    preparedStatement.setString(7, movie.getDirector());

    if (movie.getPopularity() == null) {
      preparedStatement.setNull(8,Types.INTEGER);
    } else {
      preparedStatement.setInt(8, movie.getPopularity());
    }

    preparedStatement.setBoolean(9, movie.getAwards());
  }
}
