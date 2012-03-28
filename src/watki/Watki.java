package watki;

import java.io.IOException;
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

        
        final int liczbaWatkow = 100;
        
        ExecutorService zarzadzcaKonsumentow = Executors.newFixedThreadPool(liczbaWatkow);
        ExecutorService zarzadzcaProducentow = Executors.newFixedThreadPool(2);
        
        generatorLosowy generator = new generatorLosowy();
        
        // uruchom producentow
        for (int i = 0 ; i < 2; i++)
        {
            zarzadzcaProducentow.execute(new producent(generator));
        }
        
        // uruchom konsumentow
        for (int i = 0 ; i < liczbaWatkow; i++)
        {
            zarzadzcaKonsumentow.execute(new konsument(generator, ((Integer)i).toString()) );
        }
        
        
        TimeUnit.SECONDS.sleep(5);
        zarzadzcaKonsumentow.shutdownNow();
        zarzadzcaProducentow.shutdownNow();
        
        
        System.out.println("Koniec Main!");        
        
    }
}



class generatorLosowy
{
    private final Random r = new Random();
    private Queue<Integer> dostepne;
    public generatorLosowy()
    {
        dostepne = new LinkedList<Integer>();

    }
    
    public synchronized void generuj()
    {
        int ile = r.nextInt(5);
        for (int i = 0; i < ile; i++)
        {
            dostepne.add(r.nextInt());
            try {
                TimeUnit.MICROSECONDS.sleep(ile);
            } catch (InterruptedException ignore) {
                
            }
        }
        // poinformuj wszystkie wątki uśpione na blokadzie tego obiektu
        // że sytuacja się zmieniła
        System.out.println("Wygenerowano " + ile + " nowych liczb");
        this.notifyAll();
    }
    
    public synchronized  Integer daj()
    {
        // ta metoda blokuje obiekt, ale
        // nie ma szansy kontynuować, dopóki
        // lista nie będzie pełna, dlatego dobrowolnie 
        // oddaje blokadę, usypia wątek i czeka na spełnienie warunku
        // warunek powinien być zgłoszony skądinąd 
        
        try {
            while (dostepne.isEmpty())
                wait(); // w momencie wyjścia z wait() wiadomo, że 
                        // coś zgłosiło zmianę (notifyAll) oraz że ta metoda
                        // odzyskała blokadę. W związku z tym TRZEBA jeszcze
                        // raz sprawdzić warunek i jeśli jest spełniony iść
                        // dalej
        } catch (InterruptedException ex) {
            throw new RuntimeException(ex);
        }
        
        return dostepne.remove();

    }
    
}

   
class producent implements Runnable
{
    private generatorLosowy gen;

    public producent(generatorLosowy g)
    {
        gen = g;
    }
    
    @Override
    public void run() {
        try {
            while (true)
            {
                gen.generuj();
                TimeUnit.MICROSECONDS.sleep(10);
            }
        } catch (InterruptedException ex) {
            System.out.println("producent konczy prace");
        }
            
    }
    
}



   
class konsument implements Runnable
{
    private generatorLosowy gen;
    String nazwa;
    
    public konsument(generatorLosowy g, String n)
    {
        gen = g;
        nazwa = n;
    }
    
    @Override
    public void run() {
        Integer liczba;
        try
        {
            while (true)
            {
                liczba = gen.daj();
                System.out.format("Konsument %s dostal %d\n", nazwa, liczba);
                TimeUnit.MICROSECONDS.sleep(1000);
            }
        } catch (InterruptedException ex)
        {
            System.out.println("Konsument " + nazwa + " konczy prace");
        }
    }
    
}
