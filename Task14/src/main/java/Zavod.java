import javax.sound.midi.ShortMessage;
import java.util.concurrent.Semaphore;

public class Zavod {

    abstract class Producer extends Thread {
        private final Semaphore output;
        Producer(Semaphore pipeline) {
            output = pipeline;
        }
        abstract void produce();

        @Override
        public void run() {
            while (true) {
                produce();
                output.release();
                System.out.println("A SEM: " + output.availablePermits());
            }
        }
    }

    class A_Producer extends Producer {
        A_Producer(Semaphore pipeline) {
            super(pipeline);
        }
        @Override
        void produce() {
            try {
                sleep(1000);
            } catch (InterruptedException e) { e.printStackTrace(); }
            System.out.println("A detail was created");
        }
    }

    class B_Producer extends Producer {
        B_Producer(Semaphore pipeline) {
            super(pipeline);
        }
        @Override
        void produce() {
            try {
                sleep(2000);
            } catch (InterruptedException e) { e.printStackTrace(); }
            System.out.println("B detail was created");
        }
    }

    class C_Producer extends Producer {
        C_Producer(Semaphore pipeline) {
            super(pipeline);
        }
        @Override
        void produce() {
            try {
                sleep(3000);
            } catch (InterruptedException e) { e.printStackTrace(); }
            System.out.println("C detail was created");
        }
    }

    class ModuleAssembler extends Thread {
        private final Semaphore inA, inB;
        private final Semaphore output;
        ModuleAssembler(Semaphore inA, Semaphore inB, Semaphore output) {
            this.inA = inA;
            this.inB = inB;
            this.output = output;
        }

        @Override
        public void run() {
            while (true) {
                String a = null;
                try {
                    inA.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    inB.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // получили 2 детали А и В, можем передать модуль дальше
                output.release();
            }
        }
    }

    class WidgetAssembler extends Thread {
        private final Semaphore inC, inModule;
        WidgetAssembler(Semaphore inC, Semaphore inModule) {
            this.inC = inC;
            this.inModule = inModule;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    inC.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                try {
                    inModule.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Widget was created");
            }
        }
    }

    public void simulate() {
        // init pipelines
        Semaphore A_to_module = new Semaphore(0);
        Semaphore B_to_module = new Semaphore(0);
        Semaphore module_to_widget = new Semaphore(0);
        Semaphore C_to_widget = new Semaphore(0);

        // init producers
        A_Producer a_producer = new A_Producer(A_to_module);
        B_Producer b_producer = new B_Producer(B_to_module);
        C_Producer c_producer = new C_Producer(C_to_widget);

        // init assemblers
        ModuleAssembler moduleAssembler = new ModuleAssembler(A_to_module, B_to_module, module_to_widget);
        WidgetAssembler widgetAssembler = new WidgetAssembler(C_to_widget, module_to_widget);

        // start all threads
        a_producer.start();
        b_producer.start();
        c_producer.start();
        moduleAssembler.start();
        widgetAssembler.start();
    }
}
