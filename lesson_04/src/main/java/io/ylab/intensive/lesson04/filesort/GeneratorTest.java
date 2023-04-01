package io.ylab.intensive.lesson04.filesort;

import java.io.IOException;

/**
 * Вспомогательный класс для создания файла с числами
 */
public class GeneratorTest {
    public static void main(String[] args) throws IOException {
        new Generator().generate("data.txt", 1_000_000);
    }
}
