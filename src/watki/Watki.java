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
        final int liczbaWatkow = 2;
        
        zarzadzca = Executors.newFixedThreadPool(liczbaWatkow);
        
        
        zarzadzca.execute(new licznik(false));
        zarzadzca.execute(new licznik(true));
        
        System.out.println("Koniec Main!");        
        
    }
}
 

    class licznik implements Runnable
    {
        public static int a = 0;
        private boolean tryb;
        
        public licznik(boolean tryb)
        {
            this.tryb = tryb;
        }
                
        @Override
        public void run() {
            if (tryb)
                zwiekszaj();
            else
                wyswietlaj();
            //System.err.println("koniec!");
        }
        
        private void zwiekszaj()
        {
            while (true)
                a++;
        }
        private void wyswietlaj()
        {
            while (true)
                System.out.println(a);
        }
        
    }

