class DiningPhilosophers {
private:
    mutex forkLocks[5];
    condition_variable forkCvs[5];
    bool isTaken[5];

    void pickFork(int n, function<void()> pickFunc) {
        {
            unique_lock lk(forkLocks[n]);
            forkCvs[n].wait(lk, [this, n](){return !isTaken[n];});
            isTaken[n] = true;
        }
        pickFunc();
    }

    void putFork(int n, function<void()> putFunc) {
        putFunc();
        {
            unique_lock lk(forkLocks[n]);
            isTaken[n] = false;
            forkCvs[n].notify_one();
        }
    }

public:
    DiningPhilosophers() {
        
    }

    void wantsToEat(int philosopher,
                    function<void()> pickLeftFork,
                    function<void()> pickRightFork,
                    function<void()> eat,
                    function<void()> putLeftFork,
                    function<void()> putRightFork) {
		if(philosopher % 2 == 0) {
            pickFork(philosopher, pickRightFork);
            pickFork((philosopher + 1) % 5, pickLeftFork);
        } else {
            pickFork((philosopher + 1) % 5, pickLeftFork);
            pickFork(philosopher, pickRightFork);
        }
        eat();
        putFork(philosopher, putRightFork);
        putFork((philosopher + 1) % 5, putLeftFork);
    }
};