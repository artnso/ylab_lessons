package PasswordValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {
    private static final String loginIncorrectSymbols = "Логин содержит недопустимые символы";
    private static final String loginExceededLength = "Логин слишком длинный";
    private static final String passwordIncorrectSymbols = "Пароль содержит недопустимые символы";
    private static final String passwordExceededLength = "Пароль слишком длинный";
    private static final String passwordDoesNotMatch = "Пароль и подтверждение не совпадают";
    public static boolean validate(String login, String password, String confirmPassword) {
        // регулярное выражение для поиска недопустимых символов или пустого значения
        // насчет пустого значения в условиях задачи не сказано, посчитал пустое значение как недопустимый символ
        String regex = "[^A-Za-z0-9_]|^$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(login);
        try {
            if (matcher.find()) {
                throw new WrongLoginException(loginIncorrectSymbols);
            }
            if (login.length() > 20) {
                throw new WrongLoginException(loginExceededLength);
            }
            matcher = pattern.matcher(password);
            if (matcher.find()) {
                throw new WrongLoginException(passwordIncorrectSymbols);
            }
            if (password.length() > 20) {
                throw new WrongPasswordException(passwordExceededLength);
            }
            if (!password.equals(confirmPassword)){
                throw new WrongPasswordException(passwordDoesNotMatch);
            }
        } catch (WrongLoginException ex) {
            System.out.println(ex.getMessage());
//          Другие варианты вывода сообщения об ошибке на консоль:
//          ex.printStackTrace();
//          System.out.println(ex.toString());
            return false;
        } catch (WrongPasswordException ex) {
            System.out.println(ex.getMessage());
//          Другие варианты вывода сообщения об ошибке на консоль:
//          ex.printStackTrace();
//          System.out.println(ex.toString());
            return false;
        } catch (NullPointerException ex) {
            // Обработка NPE (в случае, если на вход поступили пустые указатели)
            ex.printStackTrace();
            return false;
        }
        return true;
    }
}
