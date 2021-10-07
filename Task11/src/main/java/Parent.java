import java.util.concurrent.Semaphore;

public class Parent extends Thread {

    Semaphore sem1;
    Semaphore sem2;

    Parent() {
        sem1 = new Semaphore(1);
        sem2 = new Semaphore(0);
    }

    class Child extends Thread {

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {

                try {
                    sem2.acquire(1);
                } catch (InterruptedException e) { e.printStackTrace(); }

                System.out.println("=================================");

                sem1.release(1);
            }
        }
    }

    @Override
    public void run() {
        Child child = new Child();
        child.start();

        for (int i = 0; i < 10; i++) {

            try {
                sem1.acquire(1);
            } catch (InterruptedException e) { e.printStackTrace(); }

            System.out.println("=========");

            sem2.release(1);

        }
    }
}

//             /\__/\    _________________
//            | 0   0|  |                 \
//           /\'= 3 =/  <      MEOW!!!!!  |
//          /       |    \________________/
//         /    \   |
//      __|     | | |
//    / __   ___| | |
//    \ (  \____\_)_)
//     ---
