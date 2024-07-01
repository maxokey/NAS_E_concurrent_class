package p3;

import java.util.function.Consumer;
import java.util.function.IntConsumer;

enum Command {
    FIZZ, BUZZ, FIZZBUZZ, NUMBER
}

class FizzBuzz {
    private int n, i;

    public FizzBuzz(int n) {
        this.n = n;
        this.i = 1;
    }

    private void oneStep(Consumer<Object> action, Command command) throws InterruptedException {
        while(true) {
            synchronized(this){
                if(i > n) {
                    notify();
                    return;
                }
                while((command == Command.FIZZBUZZ && i % 15 != 0) ||
                        (command == Command.FIZZ && (i % 3 != 0 || i % 5 == 0)) ||
                        (command == Command.BUZZ && (i % 5 != 0 || i % 3 == 0)) ||
                        (command == Command.NUMBER && (i % 3 == 0 || i % 5 == 0))){
                    wait();
                    if(i > n) {
                        notify();
                        return;
                    }
                }
            } 
            action.accept(i);
            synchronized(this){
                i++;
                notifyAll();
            }
        }
    }

    public void fizz(Runnable printFizz) throws InterruptedException {
            oneStep(obj -> printFizz.run(), Command.FIZZ);
    }

    public void buzz(Runnable printBuzz) throws InterruptedException {
        oneStep(obj -> printBuzz.run(), Command.BUZZ);
    }

    public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
        oneStep(obj -> printFizzBuzz.run(), Command.FIZZBUZZ);
    }

    public void number(IntConsumer printNumber) throws InterruptedException {
        oneStep(obj -> printNumber.accept((Integer)obj), Command.NUMBER);
    }
}