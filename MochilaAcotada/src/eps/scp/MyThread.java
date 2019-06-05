package eps.scp;

import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.Semaphore;

public class MyThread extends Thread {
    private ThreadParams args;

    public MyThread(ThreadParams args){
        this.args = args;
    }

    public void run() {
        //System.out.println("("+this.args.id +")");

        for (int pos = 0; pos < args.pos.size(); ++pos) {
            //System.out.println("pos-("+ pos +")");

            int item = args.pos.get(pos);
            //System.out.println("item-("+ item + ")");

            for (int j = 0; (j <= args.maxWeight); j++) {
                //System.out.println("itemJ-1("+ args.get(pos).solutionspart.get(item-1).get(j) + ") - "+ item +"// "+j);

                if (j > 0) {
                    int wH = args.itemListpart.get(item - 1).getWeight();
                    if (wH > j) {
                        boolean bconticue = false;
                        //System.out.println("itemJ-1wH("+ args.get(pos).solutionspart.get(item-1).get(j-wH) + ") - "+ (item-1) +", "+Integer.toString(j-wH) + " | "+item+" - " +j+" ][  || "+wH );

                        try {
                            args.semaforoItem.acquire(1);
                        } catch (Exception e) {
                            System.err.println("Join Exception: " + e.getMessage());
                            e.printStackTrace();
                        }
                        args.solutionspart.get(item).set(j, args.solutionspart.get(item - 1).get(j));

                        if (item == 1) {
                            //System.out.println("\n "+ item +" -1- RELEASE\n");
                        }

                        args.semaforoItemNext.release(1);

                        if (item == 1) {
                            //System.out.println("\n \"+ item +\" -1- END\n");
                        }
                    } else {
                        boolean bconticue = false;
                        //System.out.println("itemJ-1wH("+ args.get(pos).solutionspart.get(item-1).get(j-wH) + ") - "+ (item-1) +", "+Integer.toString(j-wH) + " | "+item+" - " +j+" ][  || "+wH );

                        //System.out.println(">>itemJ-1wH("+ args.get(pos).solutionspart.get(item-1).get(j-wH) + ") - "+ (item-1) +"// "+Integer.toString(j-wH));
                        if (item == 1) {
                            //System.out.println("\n "+ item + " -1- START\n");
                        }

                        try {
                            args.semaforoItem.acquire(1);
                        } catch (Exception e) {
                            System.err.println("Join Exception: " + e.getMessage());
                            e.printStackTrace();
                        }

                        args.solutionspart.get(item).set(j, Math.max(args.solutionspart.get(item - 1).get(j), args.itemListpart.get(item - 1).getValue() + args.solutionspart.get(item - 1).get(j - wH)));

                        if (item == 1) {
                            //System.out.println("\n "+ item +" _-1- RELEASE\n");
                        }

                        args.semaforoItemNext.release(1);
                    }
                } else {
                    args.solutionspart.get(item).set(j, 0);
                    //System.out.println("<<B>>itemJ-("+ args.get(pos).solutionspart.get(item).get(j) + ") - "+ item +"// "+j);
                }


                //System.out.println("itemJ-("+ args.get(pos).solutionspart.get(item).get(j) + ") - "+ item +"// "+j);

                // for (j...)
            }
            // for()


            this.args.statics.staticsLock.lock();

            synchronized (args.profitLock) {
                args.profit = args.solutionspart.get(item).get(args.maxWeight);
            }

            this.args.statics.addMaxValue(args.profit);
            this.args.statics.incColumnsCount();
            this.args.statics.incnItemsCount();

            if(this.args.statics.getProgress() % 5 == 0 && (int)this.args.statics.getProgress() != this.args.statics.lastPrint) {
                //System.out.println("getProgress: " + args.statics.getProgress() + " - "+this.args.statics.nColumnsCount+" / "+this.args.statics.totalColumns);
                this.args.statics.lastPrint = (int)this.args.statics.getProgress();
                this.args.statics.isNotPrintable.signal();
            }

            this.args.statics.staticsLock.unlock();

            if(item == args.itemListpart.size()){
                args.barrier.release();
            }
            //System.out.println("\n ["+ item +"] Item   E N D\n");
        }


        super.run();
    }
}
