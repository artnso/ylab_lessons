package Transliterator;

public class TransliteratorTest {
    public static void main(String[] args) {
        Transliterator transliterator = new TransliteratorImpl_2();
        String res = transliterator
                .tansliterate("HELLO! ПРИВЕТ! Go, boy!"); // с транслитерацией
        System.out.println(res);
        res = transliterator
                .tansliterate("HELLO! привет! Go, boy!"); // без транслитерации
        System.out.println(res);
    }
}
