package SnilsValidator;

public class SnilsValidatorTest {
    public static void main(String[] args) {
        System.out.println(new SnilsValidatorImpl().validate("01468870570")); // false
        System.out.println(new SnilsValidatorImpl().validate("90114404441")); // true
    }
}
