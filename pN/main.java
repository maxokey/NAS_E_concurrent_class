class DiningPhilosophers {

    Object[] forkLocks = new Object[5];
    boolean[] isTaken = new boolean[5];

    public DiningPhilosophers() {
        for(int i = 0; i < 5; i++) forkLocks[i] = new Object();
    }

    // call the run() method of any runnable to execute its code
    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) throws InterruptedException {
        if(philosopher % 2 == 0) {
            // System.out.printf("right for ph #%d\n", philosopher);
            pickFork(philosopher, pickRightFork);
            // System.out.printf("left for ph #%d\n", philosopher);
            pickFork((philosopher + 1) % 5, pickLeftFork);
        } else {
            // System.out.printf("left for ph #%d\n", philosopher);
            pickFork((philosopher + 1) % 5, pickLeftFork);
            // System.out.printf("right for ph #%d\n", philosopher);
            pickFork(philosopher, pickRightFork);
        }
        eat.run();
        putFork(philosopher, putRightFork);
        putFork((philosopher + 1) % 5, putLeftFork);
    }

    private void pickFork(int n, Runnable pickFunc) throws InterruptedException {
        synchronized(forkLocks[n]){
            while(isTaken[n]){
                forkLocks[n].wait();
            }
            isTaken[n] = true;
        }
        pickFunc.run();
    }

    private void putFork(int n, Runnable putFunc) {
        putFunc.run();
        synchronized(forkLocks[n]){
            isTaken[n] = false;
            forkLocks[n].notify();
        }
    }
}