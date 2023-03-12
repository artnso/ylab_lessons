package SnilsValidator;

public class SnilsValidatorImpl implements SnilsValidator{
    private int getCheckSum(String snils) {
        int checkSum = 0;
        for (int i = 0; i < 9; i++){
            checkSum += Character.digit(snils.charAt(i), 10) * (9 - i);
        }
        if (checkSum <= 100) {
            checkSum %= 100;
        } else {
            if (checkSum % 101 == 100) {
                checkSum = 0;
            } else {
                checkSum %= 101;
            }
        }
        return checkSum;
    }

    private int getControlSum(String snils){
        return Character.digit(snils.charAt(9), 10) * 10 + Character.digit(snils.charAt(10), 10);
    }

    @Override
    public boolean validate(String snils) {
        if (snils == null) {
            return false;
        }


        if (snils.length() != 11) {
            return false;
        }

        for (int i = 0; i < 11; i++){
            if (!Character.isDigit(snils.charAt(i))){
                return false;
            }
        }

        return getCheckSum(snils) == getControlSum(snils);
    }
}
