#include <condition_variable>
class FizzBuzz {
private:
    int n, i;
    std::mutex m;
    std::condition_variable cv;

    void oneStep(function<void(int)> printFunc, function<bool(int)> condition){
        while(true){
            {
                std::unique_lock lk(m);
                if(i > n){
                    cv.notify_one();
                    return;
                }
                if(!condition(i)){
                    cv.wait(lk);
                    continue; // this is probably less performant than adding a second i > n check here
                }
            }
            printFunc(i);
            {
                std::unique_lock lk(m);
                i++;
            }
            cv.notify_all();
        }
    }

public:
    FizzBuzz(int n) {
        this->n = n;
        this->i = 1;
    }

    void fizz(function<void()> printFizz) {
        oneStep([printFizz](int x){return printFizz();}, [](int x){return x % 3 == 0 && x % 5 != 0;});
    }

    void buzz(function<void()> printBuzz) {
        oneStep([printBuzz](int x){return printBuzz();}, [](int x){return x % 5 == 0 && x % 3 != 0;});
    }

	void fizzbuzz(function<void()> printFizzBuzz) {
        oneStep([printFizzBuzz](int x){return printFizzBuzz();}, [](int x){return x % 15 == 0;});
    }

    void number(function<void(int)> printNumber) {
        oneStep(printNumber, [](int x){return x % 3 != 0 && x % 5 != 0;});
    }
};