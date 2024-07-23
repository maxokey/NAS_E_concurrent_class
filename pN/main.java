class DiningPhilosophers {
    Object[] forkLocks = new Object[5];
    boolean[] isTaken = new boolean[5];

    void pickFork(int n, Runnable pickFunc, StringBuilder logBuilder) throws InterruptedException {
        synchronized(forkLocks[n]){
            while(isTaken[n]){
                forkLocks[n].wait();
            }
            isTaken[n]= true;
        }
        synchronized(this){ // necessary for leetcode tester to not lose calls
            pickFunc.run();
        }
    }

    void putFork(int n, Runnable putFunc) throws InterruptedException {
        synchronized(this){ // necessary for leetcode tester to not lose calls
            putFunc.run();
        }
        synchronized(forkLocks[n]){
            isTaken[n] = false;
            forkLocks[n].notify();
        }
    }

    public DiningPhilosophers() {
        for(int i = 0; i < 5; i++) {
            forkLocks[i] = new Object();
        }
    }

    void wantsToEat(int philosopher,
                    Runnable pickLeftFork,
                    Runnable pickRightFork,
                    Runnable eat,
                    Runnable putLeftFork,
                    Runnable putRightFork) throws InterruptedException {
StringBuilder logBuilder = new StringBuilder();
        if(philosopher % 2 == 0) {
            pickFork((philosopher + 4) % 5, pickRightFork, logBuilder);
            pickFork(philosopher, pickLeftFork, logBuilder);
        } else {
            pickFork(philosopher, pickLeftFork, logBuilder);
            pickFork((philosopher + 4) % 5, pickRightFork, logBuilder);
        }
        synchronized(this){ // necessary for leetcode tester to not lose calls
            eat.run();
        }
        putFork((philosopher + 4) % 5, putRightFork);
        putFork(philosopher, putLeftFork);
    }
};