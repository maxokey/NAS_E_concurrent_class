class Foo {
    volatile int cnt;
    Object o;

    public Foo() {
        cnt = 1;
        o = new Object();
    }

    public void first(Runnable printFirst) throws InterruptedException {
        synchronized (o) {
            printFirst.run();
            cnt++;
            o.notifyAll();
        }
    }

    public void second(Runnable printSecond) throws InterruptedException {
        synchronized (o) {
            while (cnt < 2) {
                o.wait();
            }
            printSecond.run();
            cnt++;
            o.notify();
        }
    }

    public void third(Runnable printThird) throws InterruptedException {
        synchronized (o) {
            while (cnt < 3) {
                o.wait();
            }
            printThird.run();
        }
    }
}

/*
class Foo {
    volatile int cnt;

    public Foo() {
        cnt = 1;
    }

    public synchronized void first(Runnable printFirst) throws InterruptedException {
        printFirst.run();
        cnt++;
        this.notifyAll();
    }

    public synchronized void second(Runnable printSecond) throws InterruptedException {
        while (cnt < 2) {
            this.wait();
        }
        printSecond.run();
        cnt++;
        this.notify();
    }

    public synchronized void third(Runnable printThird) throws InterruptedException {
        while (cnt < 3) {
            this.wait();
        }
        printThird.run();
    }
}
*/
