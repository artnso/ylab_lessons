package filterconvertor;

import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws IOException {
        File filterFile = new File("filter.dat");
        StringBuilder sb = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filterFile))) {
            String next;
            while ((next = br.readLine()) != null){
                sb.append(next);
            }
        }

        String outString = sb.toString();
        String[] outData = outString.split(",");
        try (PrintWriter pw = new PrintWriter(filterFile)){
            for (String str: outData) {
                pw.println(str.trim());
            }
        }
    }
}
