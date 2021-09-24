import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

public final class Founder {

    CyclicBarrier cyclicBarrier;

    static class Worker implements Runnable {
        CyclicBarrier barrier;
        Department department;

        Worker(CyclicBarrier barrier, Department department) {
            this.barrier = barrier;
            this.department = department;
        }

        @Override
        public void run() {
            department.performCalculations();

            try {
                barrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }

    }

    private final List<Worker> workers;

    public Founder(final Company company) {
        this.workers = new ArrayList<>(company.getDepartmentsCount());

        Runnable barrierAction = company::showCollaborativeResult;
        cyclicBarrier = new CyclicBarrier(company.getDepartmentsCount(), barrierAction);

        for (int i = 0; i < company.getDepartmentsCount(); i++) {
            workers.add(new Worker(cyclicBarrier, company.getFreeDepartment(i)));
        }
    }

    public void start() {
        // threads started
        for (final Worker worker : workers) {
            new Thread(worker).start();
        }
    }
}