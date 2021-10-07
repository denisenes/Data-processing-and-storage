import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {

        StrList list = new StrList();

        // sorting demon
        new Thread(() -> {
            while (true) {
                try {
                    Thread.sleep(5000);
                    list.sort();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        while (true) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            if (input.equals("")) {
                list.printList();
            } else {
                list.add(input);
            }
        }
    }

}
