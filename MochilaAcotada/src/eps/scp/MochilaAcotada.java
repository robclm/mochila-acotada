package eps.scp;

import java.util.List;

public class MochilaAcotada extends MochilaCeroUno {

    public MochilaAcotada() {}

    public MochilaAcotada(int _maxWeight, int nThread, int M) {
        setMaxWeight(_maxWeight);
        super.nThread = nThread;
        super.M = M;
    }

    public MochilaAcotada(List<Item> _itemList) {
        setItemList(_itemList);
    }

    public MochilaAcotada(List<Item> _itemList, int _maxWeight) {
        setItemList(_itemList);
        setMaxWeight(_maxWeight);
    }

    @Override
    public List<Item> calcSolution() {
        int n = itemList.size();

        // add items to the list, if bounding > 1
        for (int i = 0; i < n; i++) {
            Item item = itemList.get(i);
            if (item.getBounding() > 1) {
                for (int j = 1; j < item.getBounding(); j++) {
                    add(item.getName(), item.getWeight(), item.getValue());
                }
            }
        }

        super.calcSolution();

        // delete the added items, and increase the original items
        while (itemList.size() > n) {
            Item lastItem = itemList.get(itemList.size() - 1);
            if (lastItem.getInKnapsack() == 1) {
                for (int i = 0; i < n; i++) {
                    Item iH = itemList.get(i);
                    if (lastItem.getName().equals(iH.getName())) {
                        iH.setInKnapsack(1 + iH.getInKnapsack());
                        break;
                    }
                }
            }
            itemList.remove(itemList.size() - 1);
        }

        return itemList;
    }

    // add an item to the item list
    public void add(String name, int weight, int value, int bounding) {
        if (name.equals(""))
            name = "" + (itemList.size() + 1);
        itemList.add(new Item(name, weight, value, bounding));
        setInitialStateForCalculation();
    }
} // class
