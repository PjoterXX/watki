package watki;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author jfk
 */
public class Watki {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        Thread w1 = new zadanie();
        Thread w2 = new Thread(new inneZadanie());
        
        w1.start();
        w2.start();
        
        
        
        System.out.println("Koniec Main!");
        
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
