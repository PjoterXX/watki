package watki;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
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
        
        zarzadzca = Executors.newCachedThreadPool();
        
        // zadanie ze sleep
        zarzadzca.execute(new zadanieZawieszoneSleep());
        
        // zadania obslugujace wejscie wyjscie roznego rodzaju
        zarzadzca.execute(new zadanieBlokowaneIO(System.in));
        
        Socket gniazdoPolaczenia = new Socket("157.158.62.96", 10103);
        InputStream netIS = gniazdoPolaczenia.getInputStream();
        zarzadzca.execute(new zadanieBlokowaneIO(netIS));
        
        // zadanie, ktore sprawdza, czy powinno sie skonczyc
        zadanieSwiadome zs = new zadanieSwiadome();
        zarzadzca.execute(zs);
        
        // najpierw przerwij zadanie "swiadome"
        TimeUnit.MILLISECONDS.sleep(1);
        zs.czyPrzerwac = true;
        
        // przerwanie watkow zawieszonych
        TimeUnit.MILLISECONDS.sleep(1000);
        zarzadzca.shutdownNow();

        // proba zamkniecia watkow zawieszonych na operacjach IO na System.IN
        TimeUnit.MILLISECONDS.sleep(2000);
        System.out.println("zamykam strumien in");
        System.in.close();
        
        // proba zamkniecia watkow zawieszonych na operacjach IO na gniezdzie 
        TimeUnit.MILLISECONDS.sleep(1000);
        gniazdoPolaczenia.close();
        
        System.out.println("Koniec Main!");        
        
    }
    
    
    // klasa wewnetrzna z zadaniem, ktore sprawdza, czy powinno zostac wylaczone
    private static class zadanieSwiadome implements Runnable
    {
        public volatile boolean czyPrzerwac = false;
        
        @Override
        public void run() {
            System.out.println("rozpoczynam zadanie SWIADOME");
            while (czyPrzerwac == false)
            {
                System.out.println("*");
            }
            
            System.out.println("zakonczylem dzialanie swiadomie");
            
        }
        
    }
    
    
    // klasa wewnetrzna z zadaniem blokowanym na IO
    private static class zadanieBlokowaneIO implements Runnable
    {
        private InputStream ins;
        public zadanieBlokowaneIO(InputStream ins)
        {
            this.ins = ins;
        }
        
        @Override
        public void run() {
            System.out.println("Zadanie z blokowanym IO na " + ins.getClass().getName());
            try {
                while (true)
                    ins.read();
            } catch (IOException ex) {
                if (Thread.currentThread().isInterrupted())
                    System.out.println("bylo zgloszone przerwanie ale i tak czekalismy!");
                System.out.println("wyjatek IO zwiazany z !" + ins.getClass().getName());
            } 
            
        }
        
    }
    // klasa wewnetrzna z zadaniem przysypiajacym
    private static class zadanieZawieszoneSleep implements Runnable
    {

        @Override
        public void run() {
            System.out.println("Zadanie z przysypianiem");
            try {
                TimeUnit.MILLISECONDS.sleep(5000);
                System.out.println("Zadanie z przypisaniem wypisalo komunikat");
            } catch (InterruptedException ex) {
                System.out.println("Nie minelo 5s, ale ktos nam przerwal...");
            }
            
            
        }
        
    }
    
}
