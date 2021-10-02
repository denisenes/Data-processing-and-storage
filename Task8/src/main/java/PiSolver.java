import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public class PiSolver {
    int thread_n;
    Worker [] threads;

    CyclicBarrier barrier;

    private class Worker extends Thread {
        private double res;
        private final long t_n;
        private final int id;

        private long i_max;
        public void setI_max(long m) { this.i_max = m; }

        private long i;

        private boolean status;
        public synchronized void upStatus() { status = true; }
        public synchronized boolean getStatus() { return status; }

        Worker(int id, int t_n) {
            res = 0;
            this.id = id;
            this.t_n = t_n;
            this.i_max = -1;
            status = false;
        }

        private double calc(long k) {
            if (k % 2 == 0 ) {
                return 1 / (2f*k + 1f);
            } else {
                return -(1 / (2f*k + 1f));
            }
        }

        @Override
        public void run() {
            System.out.println("Thread " + id + " started computations");
            i = id;
            while (!getStatus()) {
                res += calc(i);
                i += t_n;
            }

            System.out.println("Thread " + id + " was interrupted");

            // printer says we have to end our work
            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }

            //System.out.println("Thread " + id + " i_max = " + i_max);

            // now we can end our work
            while (i < i_max) {
                res += calc(i);
                i += t_n;
            }

            //System.out.println("Thread " + id + " i_max = " + i_max);

            System.out.println("Thread " + id + " last iteration: " + i);
        }

        public double getRes() { return res; }
        public long getI() { return i; }
    }

    PiSolver (int thread_n) {
        this.thread_n = thread_n;
        barrier = new CyclicBarrier(thread_n + 1);
    }

    public double compute() {

        // initialize
        threads = new Worker[thread_n];
        for (int i = 0; i < thread_n; i++) {
            threads[i] = new Worker(i, thread_n);
        }

        // start and join threads
        for (Worker thread : threads) {
            thread.start();
        }

        for (Worker thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // get result
        double pi = 0;
        for (Worker thread : threads) {
            pi += thread.getRes();
        }

        return pi * 4;
    }

    void printResult() {
        // stop all the threads
        for (Worker thread : threads) {
            thread.upStatus();
        }

        // find thread with max iterations
        long max = 0;
        for (Worker thread : threads) {
            if (thread.getI() > max) {
                max = thread.getI();
            }
        }

        System.out.println("MAXIMAL ITERATION: " + max);

        // set max iters
        for (int i = 0; i < threads.length; i++) {
            threads[i].setI_max(max);
        }

        // then go to the barrier to open it for all threads
        try {
            barrier.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}