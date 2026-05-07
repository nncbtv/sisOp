package sisOp.Esercitazione_5_BarbiereAddormentato;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class SalaSem extends Sala {
	private Semaphore mutex = new Semaphore(1);
	private Semaphore clientiSeduti = new Semaphore(0, true);;
	private Semaphore poltrona = new Semaphore(0, true);
	
	public SalaSem(int posti) {
		super(posti);
	}//Costruttore per la  sala

	@Override
	public void tagliaCapelli() throws InterruptedException {
		clientiSeduti.acquire(); //Chiamo un cliente seduto, se non ci sono dormo
		TimeUnit.SECONDS.sleep(5); //Simulo il taglio di capelli con un attesa
		poltrona.release(); //Appena finito il taglio libero la poltrona
	}//tagliaCapelli

	@Override
	public boolean attendiTaglio() throws InterruptedException {
		//Controllo se posso accettare un altro cliente
		mutex.acquire(); //Thread n deve esntrare in sezione critica per controlli e modifiche variabili
		if ( sedieLibere == 0) {//Controllo se ci sono posti in sala
			mutex.release(); //rilascio il semaforo per non andare in deadlock
			return false; //Cliente non accettato
		}
		sedieLibere--; //Mi siedo in sala d'aspetto
		mutex.release(); //Lascio il semaforo per la verifica dati al prossimo thread
		clientiSeduti.release(); //Avverto il barbiere che sono in attesa
		poltrona.acquire(); //Cerco di sedermi sulla poltrona e farmi fare il taglio
		mutex.acquire(); //Mentre mi sta tagliando i capelli libero la sedia su cui ero seduto
		sedieLibere++; //Informo gli altri che c'è una sedia libera
		mutex.release(); //Lascio la sezione critica al prossimo thread
		return true; //Ho lasciato un posto al prossimo
	}//attendiTaglio

}//Sala