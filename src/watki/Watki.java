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
        
        
        licznik l = new licznikParzysty();
        
        testerParzystosci.zatrzymajWszystkie = false;
        for (int i = 0; i < liczbaWatkow; i++)
            zarzadzca.execute(new testerParzystosci(l));
        
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
                
            }
            //System.err.println("koniec!");
        }
    }
    
    // licznik, który nie implementuje współbieżności w dostępie 
    class licznikParzysty implements licznik
    {
        int licznik = 2;
        
        // ta metoda jest problematyczna, bo watek, ktory ja wywoluje moze
        // byc wywlaszczony w "polowie"
        public void zwieksz()
        {
            licznik++;
            licznik++;
        }

        public int dajLicznik()
        {
            return licznik;
        }
        
        
    }
    

