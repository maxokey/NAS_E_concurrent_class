// fairness criteria: if philosopher X ate x times, and currently has fork f and waits on another fork,
// the philosopher Y that ate y times has the right to wake up X and take f from him, assuming that y < x.
// However, X can deny the request if by the time he wakes up, his second for is available.
class DiningPhilosophers {
    boolean[] isTaken = new boolean[5];
    int[] ateCnt = new int[5];
    Object[] philLocks = new Object[5];
    boolean[] isStealing = new boolean[5];

    void demandFork(int philosopher, int fork, int neighbor){ // demand the fork you want from the the neighbor if he's fatter
        boolean willSteal = false;
        synchronized(philLocks[neighbor]){
            if(isTaken[fork] && ateCnt[neighbor] > ateCnt[philosopher]) {
                willSteal = true;
                philLocks[neighbor].notifyAll();
            }
        }
        if(willSteal){
            synchronized(philLocks[philosopher]){ isStealing[philosopher] = true; }
        }
    }

    void putFork(int neighbor, int fork) throws InterruptedException {
        synchronized(philLocks[neighbor]){
            isTaken[fork] = false;
            philLocks[neighbor].notifyAll();
        }
    }

    public DiningPhilosophers() {
        for(int i = 0; i < 5; i++) {
            philLocks[i] = new Object();
        }
    }

    void wantsToEat(int philosopher,
                    Runnable pickLeftFork,
                    Runnable pickRightFork,
                    Runnable eat,
                    Runnable putLeftFork,
                    Runnable putRightFork) throws InterruptedException {
        while (true) {
            int fork1 = (philosopher % 2 == 0) ? (philosopher + 4) % 5 : philosopher; // fork you grab first
            int fork2 = (philosopher % 2 == 0) ? philosopher : (philosopher + 4) % 5; // fork you grab second
            int neighbor1 = (philosopher % 2 == 0) ? (philosopher + 4) % 5 : (philosopher + 1) % 5;; // one you compete for your first fork with
            int neighbor2 = (philosopher % 2 == 0) ? (philosopher + 1) % 5 : (philosopher + 4) % 5;; // one you compete for your second fork with
            int thief = neighbor1; // who would be trying to take your first fork
            
            demandFork(philosopher, fork1, neighbor1);
            synchronized(philLocks[neighbor1]){
                while(isTaken[fork1]){
                    philLocks[neighbor1].wait();
                }
                System.out.printf("ph %d got his first fork\n", philosopher);
                isTaken[fork1] = true;
            }

            demandFork(philosopher, fork2, neighbor2);
            synchronized(philLocks[neighbor2]){
                boolean restart = false;
                while(isTaken[fork2]){
                    synchronized(philLocks[thief]){
                        if(isStealing[thief]){
                            restart = true;
                            isTaken[fork1] = false;
                            System.out.printf("ph %d had his first fork stolen\n", philosopher);
                            break;
                        }
                    }
                    philLocks[neighbor2].wait();
                }
                if(restart) {
                    philLocks[neighbor2].wait();
                    continue;
                }
                System.out.printf("ph %d got his second fork\n", philosopher);
                isTaken[fork2] = true;
            }

            synchronized(this){
                pickLeftFork.run();
                pickRightFork.run();
                eat.run();
                putLeftFork.run();
                putRightFork.run();
            }
            System.out.printf("ph %d called all funcs\n", philosopher);

            synchronized(philLocks[philosopher]){ 
                isStealing[philosopher] = false; 
                ateCnt[philosopher]++;
            }
            putFork(neighbor1, fork1);
            putFork(neighbor2, fork2);
            return;
        }
    }
};