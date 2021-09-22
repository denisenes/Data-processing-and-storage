import java.util.ArrayList;

class PrinterThread extends Thread {
    private final String [] strs;

    PrinterThread(String [] strs) { this.strs = strs; }

    @Override
    public void run() { printer(strs); }

    private void printer(String [] strs) {
        for (String str : strs) {
            System.out.println(str);
        }
    }
}

public class Main {

    public static void main(String [] args) {
        ArrayList<String[]> strs = new ArrayList<>();
        strs.add(new String[] {"1", "2", "3"});
        strs.add(new String[] {"один", "два", "три"});
        strs.add(new String[] {"one", "two", "three"});
        strs.add(new String[] {"eins", "zwei", "drei"});

        // init threads
        PrinterThread [] threads = new PrinterThread[4];
        for (int i = 0; i < 4; i++) {
            threads[i] = new PrinterThread(strs.get(i));
        }

        // start threads
        for (int i = 0; i < 4; i++) {
            threads[i].start();
        }
    }

}
