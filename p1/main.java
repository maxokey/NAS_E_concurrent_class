class Foo {
    volatile int count;
    Object o;

    public Foo() {
        count = 1;
        o = new Object();
    }

    void printNth(Runnable printFunction, int expectedCount) throws InterruptedException {
        synchronized(this){
            while(count != expectedCount){
                wait();
            }
        }
        printFunction.run();
        synchronized(this){
            count = expectedCount + 1;
            notifyAll();
        }
    }

    public void first(Runnable printFirst) throws InterruptedException {
        printNth(printFirst, 1);
    }

    public void second(Runnable printSecond) throws InterruptedException {
        printNth(printSecond, 2);
    }

    public void third(Runnable printThird) throws InterruptedException {
        printNth(printThird, 3);
    }
}
