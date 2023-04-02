package io.ylab.intensive.lesson05.eventsourcing.api;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ApiApp {
  public static void main(String[] args) throws Exception {
    // Тут пишем создание PersonApi, запуск и демонстрацию работы
    AnnotationConfigApplicationContext applicationContext = new AnnotationConfigApplicationContext(Config.class);
    applicationContext.start();
    PersonApi personApi = applicationContext.getBean(PersonApi.class);
    // пишем взаимодействие с PersonApi

    // Добавляем запись о человеке
    personApi.savePerson(1L, "Alexey", "Ivanov", "Ivanovich");
    personApi.savePerson(2L, "Alexey2", "Ivanov2", "Ivanovich2");
    personApi.savePerson(3L, "Alexey3", "Ivanov3", "Ivanovich3");

    // Удаляем запись о человеке
    personApi.deletePerson(1L);

    // Т.к. в DbApp реализовано добавление записей с задержкой, то следующий код
    // может вывести пустые значения
    System.out.println(personApi.findAll());
    System.out.println(personApi.findPerson(2L));
    Thread.sleep(5000);

    // После небольшой задержки можно убедиться, что все записи в БД внесены
    System.out.println(personApi.findAll());
    System.out.println(personApi.findPerson(2L));

    // Удаляем запись о человеке
    personApi.deletePerson(1L);
  }
}
