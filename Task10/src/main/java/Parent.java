public class Parent extends Thread {

    private final Object mutex;
    private Boolean flag;

    Parent() {
        mutex = new Object();
        flag = true;
    }

    class Child extends Thread {

        private final Object mutex = Parent.this.mutex;

        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {
                synchronized (mutex) {

                    while (flag) {
                        try {
                            mutex.wait();
                        } catch (InterruptedException e) { e.printStackTrace(); }
                    }

                    System.out.println("=================================");

                    flag = true;
                    mutex.notify();
                }
            }
        }
    }

    @Override
    public void run() {
        Child child = new Child();
        child.start();

        for (int i = 0; i < 10; i++) {
            synchronized (mutex) {

                while (!flag) {
                    try {
                        mutex.wait();
                    } catch (InterruptedException e) { e.printStackTrace(); }
                }

                System.out.println("=========");

                flag = false;
                mutex.notify();
            }
        }
    }

}
