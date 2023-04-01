package io.ylab.intensive.lesson04.filesort;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Random;

/**
 * Вспомогательный класс для создания файла со случайными числами
 */
public class Generator {
    public File generate(String name, int count) throws IOException {
        Random random = new Random();
        File file = new File(name);
        try (PrintWriter pw = new PrintWriter(file)) {
            for (int i = 0; i < count; i++) {
                pw.println(random.nextLong());
            }
            pw.flush();
        }
        return file;
    }
}
