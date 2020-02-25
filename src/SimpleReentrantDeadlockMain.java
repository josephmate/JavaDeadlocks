import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleReentrantDeadlockMain {
    private static class DeadlockRunnable implements Runnable {
        private final Lock lockToGrabFirst;
        private final Lock lockToGrabSecond;
        private final CyclicBarrier ensureBothThreadsGrabTheirLock;

        public DeadlockRunnable(CyclicBarrier ensureBothThreadsGrabTheirLock,
                                Lock lockToGrabFirst,
                                Lock lockToGrabSecond) {
            this.ensureBothThreadsGrabTheirLock = ensureBothThreadsGrabTheirLock;
            this.lockToGrabFirst = lockToGrabFirst;
            this.lockToGrabSecond = lockToGrabSecond;
        }

        @Override
        public void run() {
            try {
                lockToGrabFirst.lock();

                // wait until thread2 reaches barrier before asking for the next lock
                ensureBothThreadsGrabTheirLock.await();

                lockToGrabSecond.lock();
            } catch(Exception e) {
                e.printStackTrace();
            } finally {
                lockToGrabSecond.unlock();
                lockToGrabFirst.unlock();
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        Lock lock1 = new ReentrantLock();
        Lock lock2 = new ReentrantLock();
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
