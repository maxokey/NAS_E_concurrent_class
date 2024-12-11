import java.util.concurrent.atomic.AtomicInteger;

public class DiningPhilosophers {
    AtomicInteger q;
    AtomicInteger cur;

    public DiningPhilosophers() {
        q = new AtomicInteger();
        cur = new AtomicInteger(1);
    }

    void wantsToEat(int philosopher,
                    Runnable pickLeftFork,
                    Runnable pickRightFork,
                    Runnable eat,
                    Runnable putLeftFork,
                    Runnable putRightFork) throws InterruptedException {
        int qSpot = q.incrementAndGet();
        while(true) {
            if(cur.get() == qSpot) break;
            Thread.sleep(1);
        }
        pickLeftFork.run();
        pickRightFork.run();
        eat.run();
        putLeftFork.run();
        putRightFork.run();
        cur.incrementAndGet();
    }
}