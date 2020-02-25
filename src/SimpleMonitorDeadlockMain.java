import java.util.concurrent.CyclicBarrier;

public class SimpleMonitorDeadlockMain {

    private static class DeadlockRunnable implements Runnable {
        private final Object lockToGrabFirst;
        private final Object lockToGrabSecond;
        private final CyclicBarrier ensureBothThreadsGrabTheirLock;

        public DeadlockRunnable(CyclicBarrier ensureBothThreadsGrabTheirLock,
                                Object lockToGrabFirst,
                                Object lockToGrabSecond) {
            this.ensureBothThreadsGrabTheirLock = ensureBothThreadsGrabTheirLock;
            this.lockToGrabFirst = lockToGrabFirst;
            this.lockToGrabSecond = lockToGrabSecond;
        }

        @Override
        public void run() {
            try {
                synchronized (lockToGrabFirst) {
                    // wait until thread2 reaches barrier before asking for the next lock
                    ensureBothThreadsGrabTheirLock.await();
                    synchronized (lockToGrabSecond) {
                    }
                }
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Object lock1 = new Object();
        Object lock2 = new Object();
        CyclicBarrier ensureBothThreadsGrabTheirLock = new CyclicBarrier(2);

        Thread thread1 = new Thread(new DeadlockRunnable(ensureBothThreadsGrabTheirLock, lock1, lock2));
        // reverse the order in which the locks are grabbed
        Thread thread2 = new Thread(new DeadlockRunnable(ensureBothThreadsGrabTheirLock, lock2, lock1));
        thread1.start();
        thread2.start();

        System.out.print("Waiting for thread1 to complete.");
        thread1.join();
        System.out.print("Waiting for thread2 to complete.");
        thread2.join();
        System.out.print("Done!");
    }
}
