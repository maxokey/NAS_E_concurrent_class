package p2;

import java.util.concurrent.ThreadLocalRandom;

class FooBar {
    private int n;
    private boolean printingBar;

    public FooBar(int n) {
        this.n = n;
        this.printingBar = false;
    }

    private void oneStep(Runnable printFunc, boolean isBar) throws InterruptedException {
        chaos();
        synchronized(this){
            chaos();
            while(isBar != printingBar){
                chaos();
                wait();
                chaos();
            }
            chaos();
        } 
        chaos();
        printFunc.run();
        chaos();
        synchronized(this){
            chaos();
            printingBar = !isBar;
            chaos();
            notify();
            chaos();
        }
    }

    public void foo(Runnable printFoo) throws InterruptedException { 
        for (int i = 0; i < n; i++) {       
            oneStep(printFoo, false);
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {       
            oneStep(printBar, true);
        }
    }

    private static void chaos() {
        try {
            Thread.sleep(ThreadLocalRandom.current().nextInt(6));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void printFoo() {
        String s = "foo";
        for (char c : s.toCharArray()) {
            System.out.print(c);
            chaos();
        }
    }

    public static void printBar() {
        String s = "bar";
        for (char c : s.toCharArray()) {
            System.out.print(c);
            chaos();
        }
    }

    public static void main(String[] args) {
        final int repeat = 10000;
        FooBar foobar = new FooBar(repeat);

        Thread t1 = new Thread(() -> {
            try {
                foobar.foo(FooBar::printFoo);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                foobar.bar(FooBar::printBar);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}