import java.util.Scanner;

public class Stars {
    public static void main(String[] args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)){
            int n = scanner.nextInt();
            int m = scanner.nextInt();
            String template = scanner.next();
            for (int i = 1; i <= n; i++){
                for (int j = 1; j <= m; j++){
                    System.out.print(template + " ");
                }
                System.out.println();
            }
        }

    }
}
