package eps.scp;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;

public class ThreadParams {
    protected List<Item> itemListpart  = new ArrayList<Item>();
    protected List<List<Integer>> solutionspart  = new ArrayList<List<Integer>>();
    protected List<Integer> pos = new ArrayList<Integer>();

    protected Semaphore semaforoItem;
    protected Semaphore semaforoItemNext;
    protected Semaphore barrier;

    protected Object profitLock;

    protected Statics statics;

    protected int profit = 0;
    protected int maxWeight = 0;
}
