package eps.scp;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class Statics {
    private int nItemsCount = 0;
    private int maxValue = 0;
    public int nColumnsCount;
    public int totalColumns = 0;

    protected Lock staticsLock;

    protected Condition isPrintable;
    protected Condition isNotPrintable;

    public int lastPrint = 0;

    public Statics(int columns, Lock staticsLock, Condition isPrintable, Condition isNotPrintable){
        this.totalColumns = columns;
        this.staticsLock = staticsLock;
        this.isPrintable = isPrintable;
        this.isNotPrintable = isNotPrintable;
    }

    public void addMaxValue(int value){
        if(value > this.maxValue){
            this.maxValue = value;
        }
    }

    public void incnItemsCount(){
        ++this.nItemsCount;
    }

    public void incColumnsCount(){
        ++this.nColumnsCount;
    }

    public float getProgress(){ return (float)((nColumnsCount*100)/totalColumns); }

    public int getItemsCount(){ return nItemsCount; }

    public int getMaxValue(){ return maxValue; }

    public void printStatics(){
        System.out.println("");
        System.out.println("--------------------------------------------");
        System.out.println("               printing statics");
        System.out.println("--------------------------------------------");
        System.out.println("\t- Items completamente procesados: " + this.nItemsCount);
        System.out.println("\t- Máximo valor logrado hasta el momento: " + this.getMaxValue());
        System.out.println("\t- Número de columnas evaluadas: " + this.nColumnsCount);
        System.out.println("\t- Porcentaje de Progreso: " + this.getProgress());
        System.out.println("--------------------------------------------\n");
        System.out.println("");


    }
}
