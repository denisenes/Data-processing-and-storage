import sun.misc.Signal;

public class Main {

    public static void main(String[] args) {
        // init pi computer
        PiSolver piSolver = new PiSolver(Integer.parseInt(args[0]));

        // add SIGINT handler
        Signal.handle(new Signal("INT"), signal -> {
            System.out.println("SIGINT handling...");
            piSolver.printResult();
        });

        new Thread(() -> {
            System.out.println("Signal thread: Go to sleep");
            try {
                Thread.sleep(3141);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("Signal thread: WAKED UP!");
            Signal signal = new Signal("INT");
            Signal.raise(signal);
        }).start();

        System.out.println(piSolver.compute());
    }

}
