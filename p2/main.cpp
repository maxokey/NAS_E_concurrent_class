class FooBar {
private:
    int n;
    mutex m;
    condition_variable cv;
    bool printingBar;

public:
    FooBar(int n) {
        this->n = n;
        printingBar = false;
    }

    void oneStep(function<void()> printFunc, bool isBar) {
        {
            unique_lock lk(m);
            cv.wait(lk, [this, isBar](){return isBar == printingBar;});
        } 
        printFunc();
        {
            unique_lock lk(m);
            printingBar = !isBar;
        }
        cv.notify_one();
    }

    void foo(function<void()> printFoo) {      
        for (int i = 0; i < n; i++) {
            oneStep(printFoo, false);
        }
    }

    void bar(function<void()> printBar) {
        for (int i = 0; i < n; i++) {
            oneStep(printBar, true);
        }
    }
};