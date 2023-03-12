package Sequences;

import java.util.Scanner;

public class SequenceGeneratorTest {
    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.print("Введите число N > 0 для вывода элементов последовательности: ");
            int n = scanner.nextInt();
            SequenceGenerator sequenceGenerator = new SequenceGeneratorImpl();

            System.out.print("А. ");
            sequenceGenerator.a(n);

            System.out.print("B. ");
            sequenceGenerator.b(n);

            System.out.print("C. ");
            sequenceGenerator.c(n);

            System.out.print("D. ");
            sequenceGenerator.d(n);

            System.out.print("E. ");
            sequenceGenerator.e(n);

            System.out.print("F. ");
            sequenceGenerator.f(n);

            System.out.print("G. ");
            sequenceGenerator.g(n);

            System.out.print("H. ");
            sequenceGenerator.h(n);

            System.out.print("I. ");
            sequenceGenerator.i(n);

            System.out.print("J. ");
            sequenceGenerator.j(n);

            scanner.close();
        }
    }
}
