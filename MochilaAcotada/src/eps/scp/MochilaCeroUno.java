package eps.scp;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.Condition;

public class MochilaCeroUno {
    protected List<Item> itemList  = new ArrayList<Item>();
    protected int maxWeight        = 0;
    protected int solutionWeight   = 0;
    //protected int profit           = 0;
    protected boolean calculated   = false;
    protected int nThread = 1;
    protected int M = 1;
    private List<ThreadParams> args = new ArrayList<ThreadParams>();
    protected Object profitLock = new Object();
    private final Lock staticsLock = new ReentrantLock();
    private final Condition isPrint = staticsLock.newCondition();
    private final Condition isNotPrint = staticsLock.newCondition();

    protected Semaphore barrier = new Semaphore(0);

    protected Statics statics;

    public MochilaCeroUno() {}

    public MochilaCeroUno(int _maxWeight) {
        setMaxWeight(_maxWeight);
    }

    public MochilaCeroUno(List<Item> _itemList) {
        setItemList(_itemList);
    }

    public MochilaCeroUno(List<Item> _itemList, int _maxWeight) {
        setItemList(_itemList);
        setMaxWeight(_maxWeight);
    }

    // calculte the solution of 0-1 knapsack problem with dynamic method:
    public List<Item> calcSolution() {
        int n = itemList.size();
        List<Thread> threads = new ArrayList<Thread>();


        setInitialStateForCalculation();

        List<List<Integer>> c = new ArrayList<List<Integer>>();
        for(int i = 0; i<=n; ++i) {
            List<Integer> tmp = new ArrayList<Integer>();
            for (int j = 0; (j <= maxWeight); j++) {
                if(j==0){
                    tmp.add(0);
                }else{
                    tmp.add(-1);
                }
            }
            c.add(tmp);
        }

        statics = new Statics(n, this.staticsLock, this.isPrint, this.isNotPrint);

        for(int i = 0; i<nThread ; ++i){
            ThreadParams tmpargs = new ThreadParams();
            args.add(tmpargs);

        }

        if (n > 0  &&  maxWeight > 0) {

            for(int j = 0; j <=maxWeight; j++)
                c.get(0).set(j, 0);

            int threadN = 0;

            for(int i = 1; i <= n; i++) {
                List<Integer> curr;

                if(i<=nThread){
                    if(i==1){
                        args.get(threadN).semaforoItem =  new Semaphore(maxWeight);
                        if(nThread == 1){
                            args.get(threadN).semaforoItemNext =  args.get(threadN).semaforoItem;
                        }
                    }else if(i==nThread) {
                        args.get(threadN).semaforoItem =  new Semaphore(0);
                        args.get(threadN-1).semaforoItemNext =  args.get(threadN).semaforoItem;
                        args.get(threadN).semaforoItemNext = args.get(0).semaforoItem;
                    }else{
                        args.get(threadN).semaforoItem =  new Semaphore(0);
                        args.get(threadN-1).semaforoItemNext =  args.get(threadN).semaforoItem;
                    }
                    args.get(threadN).barrier = barrier;
                    args.get(threadN).statics = statics;
                }

                args.get(threadN).solutionspart = c;
                args.get(threadN).pos.add(i);

                args.get(threadN).maxWeight = maxWeight;
                args.get(threadN).itemListpart = itemList;

                args.get(threadN).profitLock = this.profitLock;

                if(threadN < nThread-1){
                    ++threadN;
                }else{
                    threadN = 0;
                }
            }

            // for (i...)
            Thread tstatics = new Thread(new StaticsPrinter(statics));
            tstatics.start();
            threads.add(tstatics);

            for(int i = 0; i<nThread ; ++i){
                Thread t = new Thread(new MyThread(args.get(i)));
                t.start();
                threads.add(t);
            }

            try {
                barrier.acquire();
            }catch (Exception ex){
                System.out.println("Error: " + ex.getMessage());
            }



            for(int i = n, j = maxWeight; i > 0 &&  j >= 0; i--) {
                int tempI   = c.get(i).get(j);
                int tempI_1 = c.get(i-1).get(j);
                if((i == 0 &&  tempI > 0) || (i > 0 &&  tempI != tempI_1)){
                    Item iH = itemList.get(i-1);
                    int wH = iH.getWeight();
                    iH.setInKnapsack(1);
                    j-= wH;
                    solutionWeight += wH;
                }
            }

            for (Thread t : threads) {
                if(t.isAlive())
                    t.stop();
            }
// for()
            calculated=true;
            this.statics.printStatics();

        } // if()
        return itemList;
    }


    private int CalculateMaxWeigth(int start, int end, List<Item> items){
        int tmpj=maxWeight;
        List<Item> tmpSubList;

        if((end - start) == items.size()) {
            return maxWeight;
        }else {
            tmpSubList = items.subList(end+1, items.size());
        }

        for (Item i : tmpSubList) {
            tmpj -= i.weight;
            //System.out.println(this.args.id + " :: " + i.toString() + " / " + i.weight);
        }
        //System.out.println("("+this.args.id +") " + "tmpj :: "+tmpj +"");

        return tmpj;
    }


    // add an item to the item list
    public void add(String name, int weight, int value) {
        if (name.equals(""))
            name = "" + (itemList.size() + 1);
        itemList.add(new Item(name, weight, value));
        setInitialStateForCalculation();
    }

    // add an item to the item list
    public void add(int weight, int value) {
        add("", weight, value); // the name will be "itemList.size() + 1"!
    }

    // remove an item from the item list
    public void remove(String name) {
        for (Iterator<Item> it = itemList.iterator(); it.hasNext(); ) {
            if (name.equals(it.next().getName())) {
                it.remove();
            }
        }
        setInitialStateForCalculation();
    }

    // remove all items from the item list
    public void removeAllItems() {
        itemList.clear();
        setInitialStateForCalculation();
    }

    int calcMaxProfit(){
        int tmpProfit = 0;


        for(int i=0; i<nThread; ++i){
            if(tmpProfit < args.get(i).profit){
                synchronized(this.profitLock){
                    tmpProfit = args.get(i).profit;
                }

            }
        }
        return tmpProfit;
    }

    public int getProfit() {
        if (!calculated)
            calcSolution();
        return calcMaxProfit();
    }

    public int getSolutionWeight() {return solutionWeight;}
    public boolean isCalculated() {return calculated;}
    public int getMaxWeight() {return maxWeight;}

    public void setMaxWeight(int _maxWeight) {
        maxWeight = Math.max(_maxWeight, 0);
    }

    public void setItemList(List<Item> _itemList) {
        if (_itemList != null) {
            itemList = _itemList;
            for (Item item : _itemList) {
                item.checkMembers();
            }
        }
    }

    // set the member with name "inKnapsack" by all items:
    private void setInKnapsackByAll(int inKnapsack) {
        for (Item item : itemList)
            if (inKnapsack > 0)
                item.setInKnapsack(1);
            else
                item.setInKnapsack(0);
    }

    // set the data members of class in the state of starting the calculation:
    protected void setInitialStateForCalculation() {
        setInKnapsackByAll(0);
        calculated     = false;
        //profit         = 0;
        solutionWeight = 0;
    }
} // class
