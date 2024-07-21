class DiningPhilosophers {
    Object[] forkLocks = new Object[5];
// AtomicBoolean[] isPhil = new AtomicBoolean[5];
    AtomicBoolean[] isTaken = new AtomicBoolean[5];

    void pickFork(int n, Runnable pickFunc, StringBuilder logBuilder) throws InterruptedException {
        synchronized(this){ // forkLocks[n]
            while(isTaken[n].get()){
                this.wait();
            }
            isTaken[n].set(true);
// logBuilder.append(n);
// System.out.printf("picked up fork #%d\n", n);
            pickFunc.run();
        }
    }

    void putFork(int n, Runnable putFunc) throws InterruptedException {
        synchronized(this){ // forkLocks[n]
// System.out.printf("put down fork #%d\n", n);
            putFunc.run();
            isTaken[n].set(false);
            this.notify();
        }
    }

    public DiningPhilosophers() {
        for(int i = 0; i < 5; i++) {
            forkLocks[i] = new Object();
            isTaken[i] = new AtomicBoolean(false);
// isPhil[i] = new AtomicBoolean(false);
        }
    }

    void wantsToEat(int philosopher,
                    Runnable pickLeftFork,
                    Runnable pickRightFork,
                    Runnable eat,
                    Runnable putLeftFork,
                    Runnable putRightFork){
try{
// if(isPhil[philosopher].get() == true) System.out.println("ALARM");
// isPhil[philosopher].set(true);
StringBuilder logBuilder = new StringBuilder();
// logBuilder.append(philosopher);
        if(philosopher % 2 == 0) {
            pickFork((philosopher + 4) % 5, pickRightFork, logBuilder);
            pickFork(philosopher, pickLeftFork, logBuilder);
        } else {
            pickFork(philosopher, pickLeftFork, logBuilder);
            pickFork((philosopher + 4) % 5, pickRightFork, logBuilder);
        }
// System.out.println(logBuilder.toString());
        eat.run();
        putFork((philosopher + 4) % 5, putRightFork);
        putFork(philosopher, putLeftFork);
// isPhil[philosopher].set(false);
// } catch(Throwable e) {
//     System.out.println(e.toString());
// }
    }
};