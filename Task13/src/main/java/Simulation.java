import java.util.Date;
import java.util.Random;
import java.util.concurrent.locks.ReentrantLock;

public class Simulation {

    private final Object forks = new Object();
    private Boolean flag = true;

    class Philosopher extends Thread {
        private final ReentrantLock left;
        private final ReentrantLock right;

        Random random;

        // FOR LOGGING
        private final int id;

        Philosopher(ReentrantLock left, ReentrantLock right, int id, Random random) {
            this.left = left;
            this.right = right;
            this.id = id;
            this.random = random;
        }

        @Override
        public void run() {
            for (int i = 0; i < 1000; i++) {

                think();

                while (true) {
                    synchronized (forks) {
                        if (left.tryLock()) {
                            if (right.tryLock()) {
                                flag = true;
                                forks.notifyAll();
                                break;
                            } else {
                                left.unlock();
                            }
                        }
                        while (!flag) {
                            try {
                                forks.wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    flag = false;
                }

                eat(i);
                left.unlock();
                right.unlock();
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

        private void eat(int i) {
            int randomNum = random.nextInt(100);
            try {
                System.out.println("Philosopher " + id + " eats, i = " + i);
                sleep(randomNum);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void simulate() {
        Random rand = new Random(new Date().getTime());

        // init forks
        ReentrantLock [] forks = new ReentrantLock[5];
        for (int i = 0; i < 5; i++) {
            forks[i] = new ReentrantLock();
        }

        // init philosophers
        Philosopher [] philosophers = new Philosopher[5];
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