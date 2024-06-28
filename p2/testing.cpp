#include <mutex>
#include <condition_variable>
#include <functional>
#include <thread>
#include <iostream>
#include <random>

void chaos();

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
            chaos();
            std::unique_lock<std::mutex> lk(m);
            chaos();
            cv.wait(lk, [this, isBar](){return isBar == printingBar;});
            chaos();
        } 
        chaos();
        printFunc();
        chaos();
        {
            chaos();
            std::unique_lock<std::mutex> lk(m);
            chaos();
            printingBar = !isBar;
            chaos();
        }
        chaos();
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

void chaos() {
    static std::mt19937 rng(std::random_device{}());
    std::uniform_int_distribution<std::chrono::milliseconds::rep> dist(0, 5);
    std::this_thread::sleep_for(std::chrono::milliseconds(dist(rng)));
}

void printFoo() {
    std::string s = "foo";
    for (char c : s) {
        std::cout << c;
        std::cout.flush();
        chaos();
    }
}

void printBar() {
    std::string s = "bar";
    for (char c : s) {
        std::cout << c;
        std::cout.flush();
        chaos();
    }
}

int main() {
    const int repeat = 10000;
    FooBar foobar(repeat);

    std::thread t1(&FooBar::foo, &foobar, printFoo);
    std::thread t2(&FooBar::bar, &foobar, printBar);

    t1.join();
    t2.join();

    return 0;
}