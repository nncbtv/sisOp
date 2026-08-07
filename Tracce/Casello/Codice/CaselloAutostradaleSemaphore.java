package sisOp.Tracce.Casello.Codice;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CaselloAutostradaleSemaphore extends CaselloAutostradale {
	
	private Semaphore [] porteCasello = new Semaphore[super.nPorte]; //Più porte ma solo un automobilista per ognuna di esse
	private Semaphore mutex = new Semaphore(1); //Semaforo per la mutua esclusione sulla variabile incasso

	public CaselloAutostradaleSemaphore() {
		for ( int i = 0; i<super.nPorte; i++ ) {
			porteCasello[i] = new Semaphore(1, true); //Ogni porta deve gestire la sua coda col FIFO, 1 automobile per volta
		}
	}//Costruttore
	
	@Override
	public void accodamento(int p) {
		//Provo ad accodarmi in fila sulla porta p
		try {
			porteCasello[p].acquire();
		}catch (InterruptedException e) {
			System.out.println("Non sono riuscito ad accodare un automobilista");
		}
	}//accodamento

	@Override
	public void pagaPedaggio(double km, int p) {
		try {
			//Il tempo di prendere i picciuli
			Random r = new Random();
			int tempo = r.nextInt(3,7);
			TimeUnit.SECONDS.sleep(tempo);
			mutex.acquire(); //Necessito del mutex per pagare altrimenti qualcun altro su una altra porta del casello modifica la variubile incasso e vado in race condition
			super.incasso = super.incasso + (km*super.tariffa); //Pago
			mutex.release(); //Rilascio la cassa
			porteCasello[p].release(); //Rilascio il posto al casello
		}catch (Exception e) {
			System.out.println("Non sono riuscito a pagare");
		}
	}//pagapedaggio


}//CaselloAutostradaleSemaphore