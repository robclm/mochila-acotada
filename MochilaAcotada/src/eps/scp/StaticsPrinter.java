package eps.scp;

import java.util.EmptyStackException;

public class StaticsPrinter extends Thread {
    private Statics statics;

    public StaticsPrinter(Statics statics){
        this.statics = statics;
    }

    public void run() {

        while (true){
            this.statics.staticsLock.lock();
            if ((statics.getProgress() % 5 != 0) || statics.getProgress()==0 || statics.getProgress() == statics.lastPrint){
                try {
                    this.statics.isNotPrintable.await();
                } catch (Exception ex) {
                    System.out.println("Error: " + ex.getMessage());
                }
            }
            this.statics.printStatics();

            this.statics.staticsLock.unlock();

            if(this.statics.getProgress() == 100){
                this.stop();
            }
        }
    }

}
