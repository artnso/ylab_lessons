package PasswordValidator;

public class PasswordValidatorTest {
    public static void main(String[] args) {
        System.out.println(PasswordValidator.validate("   ", "", "")); // WrongLoginException - false
        System.out.println(PasswordValidator.validate("", "", "")); // WrongLoginException - false
        System.out.println(PasswordValidator.validate("alex123абв", "", "")); //WrongLoginException - false
        System.out.println(PasswordValidator.validate("123456789012345678901", "", "")); // WrongLoginException - false
        System.out.println(PasswordValidator.validate("alex123", "123", "")); //WrongPasswordException - false
        System.out.println(PasswordValidator.validate("alex123", "123456789012345678901", "")); //WrongPasswordException - false
        System.out.println(PasswordValidator.validate("alex123", "", "")); //WrongPasswordException - false
        System.out.println(PasswordValidator.validate("alex123", "123654", "123654")); // correct - true
    }
}
