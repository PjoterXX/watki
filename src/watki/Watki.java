package watki;

import java.util.*;
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

        
        
        ExecutorService zarzadca = Executors.newCachedThreadPool();
        CountDownLatch blokadaStartu = new CountDownLatch(1);
        CountDownLatch zakonczenie = new CountDownLatch(3);
        
        zarzadca.execute(new zadanie("Lech", blokadaStartu, zakonczenie));
        zarzadca.execute(new zadanie("Czech", blokadaStartu, zakonczenie));
        zarzadca.execute(new zadanie("Rus", blokadaStartu, zakonczenie));
        
        System.out.println("Poczekajmy 2s");
        TimeUnit.SECONDS.sleep(2);
        blokadaStartu.countDown();
        
        System.out.println("Czekamy na zakonczenie wszystkich zadan");
        zakonczenie.await();
        System.out.println("Wszystkie zadania zakonczone");

        zarzadca.shutdownNow();
        
    }
}

// zadanie oblicza coś bardzo waznego bardzo dlugo
class zadanie implements Runnable {

    private final Random r = new Random();
    CountDownLatch blokadaStartu, zakonczenie;
    String nazwa;
    
    public zadanie(String nazwa, CountDownLatch blokadaStartu, CountDownLatch zakonczenie)
    {
        this.nazwa = nazwa;
        this.blokadaStartu = blokadaStartu;
        this.zakonczenie = zakonczenie;
    }
    
    @Override
    public void run() {
        try {
            System.out.println("Zadanie " + nazwa + " uruchomione");
            blokadaStartu.await();
            System.out.println("Zadanie " + nazwa + " dostalo pozwolenie na start");
            TimeUnit.SECONDS.sleep(r.nextInt(3));
            System.out.println("Zadanie " + nazwa + " informuje, ze zkonczylo");
            zakonczenie.countDown();
        } catch (InterruptedException ex) {
            System.out.println("Zadanie " + nazwa + " przerwane - timeout");
        }
        
    }
    

}
