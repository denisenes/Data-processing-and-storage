public class Main {

    public static void main(String[] args) {
        PiSolver piSolver = new PiSolver(Long.parseLong(args[0]), Integer.parseInt(args[1]));
        System.out.println(piSolver.compute());

    }
}
