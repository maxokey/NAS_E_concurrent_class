class DiningPhilosophers {
private:
    mutex globalLock;
    condition_variable philCvs[5];
    queue<int> q;
    set<int> eating;

public:
    DiningPhilosophers() {
        
    }

    void wantsToEat(int philosopher,
                    function<void()> pickLeftFork,
                    function<void()> pickRightFork,
                    function<void()> eat,
                    function<void()> putLeftFork,
                    function<void()> putRightFork) {
		{
            unique_lock lk(globalLock);
            q.push(philosopher);

            philCvs[philosopher].wait(lk, [this, philosopher](){
                return q.front() == philosopher && // q should never be empty as only the thread can remove its number from q
                !eating.contains((philosopher + 4) % 5) &&
                !eating.contains((philosopher + 1) % 5) &&
                !eating.contains(philosopher);});   // necessary, but I don't really understand why.
            q.pop();                                // Seems like there can be 2 threads pretending to be the same philosopher.
            eating.insert(philosopher);
            if(!q.empty()) philCvs[q.front()].notify_one();
        }
            pickLeftFork();
            pickRightFork();
            eat();
            putLeftFork();
            putRightFork();
        {
            unique_lock lk(globalLock);
            eating.erase(philosopher);
            if(!q.empty()) philCvs[q.front()].notify_one();
        }
    }
};