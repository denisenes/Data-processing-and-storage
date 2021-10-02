import java.util.Date;
import java.util.Random;

public class RightSimulation {

    static class Fork {
        private final int id;
        public int getId() { return id; }
        Fork(int id) { this.id = id; }
    }

    class Philosopher extends Thread {
        private final Fork left;
        private final Fork right;

        Random random;

        // FOR LOGGING
        private final int id;

        Philosopher(Fork left, Fork right, int id, Random random) {
            this.left = left;
            this.right = right;
            this.id = id;
            this.random = random;
        }

        @Override
        public void run() {
            for (int i = 0; i < 100; i++) {
                think();

                Fork minFork;
                Fork maxFork;

                if (left.getId() < right.getId()) {
                    minFork = left;
                    maxFork = right;
                } else {
                    minFork = right;
                    maxFork = left;
                }

                synchronized (minFork) {
                    synchronized (maxFork) {
                        System.out.println("Philosopher " + id + " is eating");
                        eat();
                    }
                }

            }
        }

        private void think() {
            int randomNum = random.nextInt(100);
            try {
                sleep(randomNum);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        private void eat() {
            int randomNum = random.nextInt(100);
            try {
                sleep(randomNum);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void simulate() {
        Random rand = new Random(new Date().getTime());

        // init forks
        Fork[] forks = new Fork[5];
        for (int i = 0; i < 5; i++) {
            forks[i] = new Fork(i);
        }

        // init philosophers
        Philosopher[] philosophers = new Philosopher[5];
        for (int i = 0; i < 5; i++) {
            philosophers[i] = new Philosopher(forks[i % 5], forks[(i + 1) % 5], i, rand);
        }

        // start philosophers
        for (int i = 0; i < 5; i++) {
            philosophers[i].start();
        }

        // wait philosophers
        for (Philosopher philosopher : philosophers) {
            try {
                philosopher.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("All philosophers are happy :3");
    }
}
