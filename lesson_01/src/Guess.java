import java.util.Random;
import java.util.Scanner;

public class Guess {
    public static void main(String[] args) throws Exception {
        int number = new Random().nextInt(100);
        int maxAttempts = 10;
        System.out.println("Я загадал число от 1 до 99. У тебя " + maxAttempts + " попыток угадать.");
        Scanner scanner = new Scanner(System.in);
        for (int i = maxAttempts; i > 0; i--){
            int inputNumber = scanner.nextInt();
            if (inputNumber == number) {
                System.out.println("Ты угадал с "+ (maxAttempts - i + 1) +" попытки!");
                break;
            } else if (number > inputNumber) {
                System.out.println("Мое число больше! У тебя осталось "+ (i - 1) +" попыток");
            } else {
                System.out.println("Мое число меньше! У тебя осталось "+ (i - 1) +" попыток");
            }
            if ((i - 1) == 0) {
                System.out.println("Ты не угадал");
            }
        }
    }
}
