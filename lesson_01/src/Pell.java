import java.util.Scanner;

public class Pell {
    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            int n = scanner.nextInt();
            int pell_2 = 0;
            int pell_1 = 1;
            int currentPell;

            if (n > 1){
                for (int i = 2; i <= n; i++){
                    currentPell = 2 * pell_1 + pell_2;
                    pell_2 = pell_1;
                    pell_1 = currentPell;
                }
            }

            if (n == 0) {
                System.out.println(pell_2);
            } else {
                System.out.println(pell_1);
            }
        }
    }
}
