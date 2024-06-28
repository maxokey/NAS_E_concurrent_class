class FooBar {
    private int n;
    private boolean printingBar;

    public FooBar(int n) {
        this.n = n;
        this.printingBar = false;
    }

    private void oneStep(Runnable printFunc, boolean isBar) throws InterruptedException {
        synchronized(this){
            while(isBar != printingBar){
                wait();
            }
        } 
        printFunc.run();
        synchronized(this){
            printingBar = !isBar;
            notify();
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
}