#include <mutex>
#include <condition_variable>
#include <functional>
class FooBar {
private:
    int n;
    std::mutex m;
    std::condition_variable cv;
    bool printingBar;

public:
    FooBar(int n) {
        this->n = n;
        printingBar = false;
    }

    void oneStep(std::function<void()> printFunc, bool isBar) {
        {
            std::unique_lock lk(m);
            cv.wait(lk, [this, isBar](){return isBar == printingBar;});
        } 
        printFunc();
        {
            std::unique_lock lk(m);
            printingBar = !isBar;
        }
        cv.notify_one();
    }

    void foo(std::function<void()> printFoo) {      
        for (int i = 0; i < n; i++) {
            oneStep(printFoo, false);
        }
    }

    void bar(std::function<void()> printBar) {
        for (int i = 0; i < n; i++) {
            oneStep(printBar, true);
        }
    }
};