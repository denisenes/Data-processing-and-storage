class InfernalThread extends Thread {

    @Override
    public void run() {
        System.out.println("Thread started");
        while (!isInterrupted()) {
            System.out.println("Looooooooooooooop!!!!");
        }
        System.out.println("Thread was interrupted");
    }

}

public class Main {

    public static void main(String [] args) {
        InfernalThread inf_thread = new InfernalThread();
        inf_thread.start();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        inf_thread.interrupt();
    }

}