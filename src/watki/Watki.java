package watki;

import java.util.concurrent.*;


/**
 *
 * @author jfk
 */
public class Watki {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {

        ExecutorService zarzadzca;
        //zarzadzca = Executors.newSingleThreadExecutor();
        //zarzadzca.execute(new inneZadanie());
        
        zarzadzca = Executors.newCachedThreadPool();
        zarzadzca.execute(new inneZadanie());
        zarzadzca.execute(new zadanie());
        
        
        System.out.println("Koniec Main!");
        
        // przerwanie watkow zawieszonych
        TimeUnit.MILLISECONDS.sleep(1000);
        zarzadzca.shutdownNow();
        
    }
    
    
    
    // klasa wewnetrzna z zadaniem 1
    private static class zadanie extends Thread
    {

        @Override
        public void run() {
            try {
                sleep(5000);
            } catch (InterruptedException ex) {
                System.out.println("nie minelo 5s...");
            }
            System.out.println("Dzialam!");
        }
        
    }
    
    private static class inneZadanie implements Runnable
    {

        @Override
        public void run() {
            try {
                Thread.sleep(5000);
            } catch (InterruptedException ex) {
                System.out.println("Nie minelo 5s (2)...");
            }
            
            System.out.println("Ja też działam!");
        }
        
    }
    
}
