package FileSort;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

public class Test {
    public static void main(String[] args) throws IOException {
        System.out.println("Программа запущена: " + LocalDateTime.now());
        File dataFile = new Generator().generate("data.txt", 50_000_000);
        System.out.println("Файл сгенерирован: " + LocalDateTime.now());
//        File dataFile = new Generator().generate("data.txt", 375_000_000);
        boolean fileIsSorted = new Validator(dataFile).isSorted(); // false
        System.out.println("Файл проверен на сортировку: " + LocalDateTime.now() + " результат: " + fileIsSorted);
        File sortedFile = new Sorter().sortFile(dataFile);
        System.out.println("Файл отсортирован: " + LocalDateTime.now());
        fileIsSorted = new Validator(sortedFile).isSorted(); // true
        System.out.println("Файл проверен на сортировку: " + LocalDateTime.now() + " результат: " + fileIsSorted);
        System.out.println("Программа завершена");

    }
}
