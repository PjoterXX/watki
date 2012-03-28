package watki;

import java.io.IOException;
import java.io.InputStream;
import java.net.*;
import java.util.concurrent.*;
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
    public static void main(String[] args) throws Exception {

        ExecutorService zarzadzca;
        final int liczbaWatkow = 100;
        
        zarzadzca = Executors.newFixedThreadPool(liczbaWatkow);
        
        
        //licznik l = new licznikParzysty();
        licznik l = new licznikBezpieczny(new licznikParzysty());
        
        testerParzystosci.zatrzymajWszystkie = false;
        for (int i = 0; i < liczbaWatkow; i++)
            zarzadzca.execute(new testerParzystosci(l));
        
        // tym razem sztucznie przerwiemy
        TimeUnit.SECONDS.sleep(5);
        System.out.println("Przerywam watki");
        zarzadzca.shutdownNow();
        
        System.out.println("Koniec Main!");        
        
    }
}
 
    // na podstawie "Thinking in Java

    // drobne zwiększenie przejrzystości
    interface licznik
    {
        public int dajLicznik();
        public void zwieksz();
    }
    
    //testery parzystosci tworzone są dla testowania równoległego liczników
    // które po wywołaniu metody "zwiększ" dają zawsze wartość parzystą.

    class testerParzystosci implements Runnable
    {
        public static volatile boolean zatrzymajWszystkie = false;
        
        licznik testowanyLicznik;
        int liczbaWywolan = 1;
        
        public testerParzystosci(licznik l)
        {
            testowanyLicznik = l;
        }

        @Override
        public void run() {
            int wartosc;
            
            while (testerParzystosci.zatrzymajWszystkie != true)
            {
                wartosc = testowanyLicznik.dajLicznik();
                
                if (wartosc % 2 != 0)
                {
                    testerParzystosci.zatrzymajWszystkie = true;
                    System.out.format("W wywołaniu: %d wykrylem blad: %s dal wartosc %d\n", 
                            liczbaWywolan, testowanyLicznik.getClass().getName(), wartosc);
                    
                    return;
                }
                
                
                testowanyLicznik.zwieksz();
                liczbaWywolan++;
                
                // miejsce na przejęcie przerwania
                try {
                    TimeUnit.MICROSECONDS.sleep(10);
                } catch (InterruptedException ex) {
                    System.out.format("przerywam: timeout po %d wywolaniach\n", 
                            liczbaWywolan);
                    break;
                }
                
            }
            //System.err.println("koniec!");
        }
    }



    // licznik, który nie jest bezpieczny dla współbieżności
    class licznikBezpieczny implements licznik
    {
        private licznik l;
        public licznikBezpieczny(licznik l)
        {
            this.l = l;
        }

        // słowo synchronized NIE jest częścią API...
        @Override
        public synchronized int dajLicznik() {
            return l.dajLicznik();
        }

        @Override
        public synchronized void zwieksz() {
            l.zwieksz();
        }
        

    }


    // licznik, który nie jest bezpieczny dla współbieżności
    class licznikParzysty implements licznik
    {
        int licznik = 2;
        
        // ta metoda już nie jest problematyczna, bo blokuje obiekt
        // w czasie zmiany
        public void zwieksz()
        {
            try {
                TimeUnit.MICROSECONDS.sleep(2);
            } catch (InterruptedException ex) {
                 
            }
            licznik++;
            licznik++;

        }

        // ta metoda też musi być synchronizowana, żeby respektować blokadę
        // chroniącą zwieksz
        public int dajLicznik()
        {
            return licznik;
        }
        
        
    }
    

