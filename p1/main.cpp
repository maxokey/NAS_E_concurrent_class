class Foo {
    public:
        Foo() : count(0) {}

        void first(std::function<void()> printFirst) {
            std::unique_lock<std::mutex> lock(mtx);
            printFirst();
            count = 1;
            cv.notify_all();
        }

        void second(std::function<void()> printSecond) {
            std::unique_lock<std::mutex> lock(mtx);
            while (count != 1) {
                cv.wait(lock);
            }
            printSecond();
            count = 2;
            cv.notify_all();
        }

        void third(std::function<void()> printThird) {
            std::unique_lock<std::mutex> lock(mtx);
            while (count != 2) {
                cv.wait(lock);
            }
            printThird();
        }

    private:
        std::mutex mtx;
        std::condition_variable cv;
        int count;
};

/*

*/