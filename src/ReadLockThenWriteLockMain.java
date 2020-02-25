import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class ReadLockThenWriteLockMain {
    public static void main(String[] args) {
        System.out.println("Starting!");
        ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
        System.out.println("Grabbing the read lock.");
        readWriteLock.readLock().lock();
        System.out.println("Grabbing the write lock.");
        readWriteLock.writeLock().lock();
        System.out.println("Releasing the write lock.");
        readWriteLock.writeLock().unlock();
        System.out.println("Releasing the read lock.");
        readWriteLock.readLock().unlock();
        System.out.println("Done!");
    }
}
