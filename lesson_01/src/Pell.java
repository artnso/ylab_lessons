import java.util.Scanner;

public class Pell {
    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            int n = scanner.nextInt();
            int[] pellNumbers = new int[n+1];
            pellNumbers[0] = 0;
            pellNumbers[1] = 1;
            if (n > 1){
                for (int i = 2; i <= n; i++){
                    pellNumbers[i] = 2 * pellNumbers[i-1] +pellNumbers[i-2];
                }
            }
            System.out.println(pellNumbers[n]);
        }
    }
}
