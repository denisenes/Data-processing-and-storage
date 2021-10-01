public class PiSolver {

    long iter_n;
    int thread_n;

    private static class Worker extends Thread {
        private double res;
        private final long i_n;
        private final int t_n;
        private final int id;

        Worker(int id, long n, int t_n) {
            res = 0;
            this.i_n = n;
            this.id = id;
            this.t_n = t_n;
        }

        @Override
        public void run() {
            long i = id;
            while (i < i_n) {
                if (i % 2 == 0 ) {
                    res += 1 / (2f*i + 1f);
                } else {
                    res -= 1 / (2f*i + 1f);
                }
                i += t_n;
            }
        }

        public double getRes() {
            return res;
        }
    }

    PiSolver (long iter_n, int thread_n) {
        this.iter_n = iter_n;
        this.thread_n = thread_n;
    }

    public double compute() {

        // initialize
        Worker [] threads = new Worker[thread_n];
        for (int i = 0; i < thread_n; i++) {
            threads[i] = new Worker(i, iter_n, thread_n);
        }

        long startTime = System.nanoTime();

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

        long estimatedTime = System.nanoTime() - startTime;
        System.out.println("TIME: " + String.valueOf(estimatedTime / 1000000));

        return pi * 4;
    }


}
