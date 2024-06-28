class Foo {
    private:
        std::mutex mtx;
        std::condition_variable cv;
        int count = 0;        
        void printNth(std::function<void()> printFunction, int expectedCount) {
            {
                std::unique_lock<std::mutex> lock(mtx);
                cv.wait(lock, [this, expectedCount]{ return (count == expectedCount) && !isPrinting; });
            }
            printFunction();
            {
                std::unique_lock<std::mutex> lock(mtx);
                count = expectedCount + 1;
                cv.notify_all();
            }
        }

    public: 
        void first(std::function<void()> printFirst) {
            printNth(printFirst, 0);
        }

        void second(std::function<void()> printSecond) {
            printNth(printSecond, 1);
        }

        void third(std::function<void()> printThird) {
            printNth(printThird, 2);
        }
};