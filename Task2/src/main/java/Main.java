public class Main {

    static class PrinterThread extends Thread {
        @Override
        public void run() {
            printLoop("Child");
        }
    }

    public static void printLoop(String str) {
        for (int i = 0; i < 10; i++) {
            System.out.println("I'm " + str);
        }
    }

    public static void main(String [] args) {
        PrinterThread printer = new PrinterThread();
        printer.start();

        try {
            printer.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        printLoop("Parent");
    }
}