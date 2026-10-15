package sisOp.Tracce.BarMod.Codice;

import java.util.concurrent.Semaphore;

public class BarModSemaphore extends BarMod {

	//Appena arrivo e non ce nessuno in cassa prima passo in cassa
	//Se il bancone è libero vado prima a bere il caffé e poi in cassa
	//Se nessuno è libero mi accodo nella più corta preferendo la cassa se sono di uguale dimensione

	private Semaphore codaCassa; //Uso i semafori per gestire le code dei thread, il semaforo stesso si occupera di garantire mutua esclusione
	private Semaphore codaBancone;

	public BarModSemaphore() {
		codaCassa = new Semaphore(1, true); //Alla cassa si paga uno alla volta e in ordine FIFO
		codaBancone = new Semaphore(4, true); //Ovviamente anche qui siccome siamo in una società utopica stiamo in coda FIFO
	}//Costruttore

	@Override
	public int scegli() {
		//Con 1 paghi con 2 consumi al bancone
		if ( codaCassa.availablePermits() > 0 ) {
			return 1; //Se la cassa è libera prima pago
		}
		if ( (codaBancone.availablePermits() > 0) || (codaCassa.getQueueLength() > codaBancone.getQueueLength()) ) {
			return 2; //La coda del bancone è più corta quindi mi accodo li
		}
		return 1; //Se le code sono uguali oppure la coda cassa è più corta vado in cassa
		/*
		 * Metodo che fa un piccola stima, ovviemente qui c'é race condition poiche un Thread può aggiungersi in coda mentre controllo,
		 * non è però un problema, sto simulando una stima di due code ad occhio quindi ci sta
		 * */
	}//scegli

	@Override
	public void inizia(int i) {
		try {
			if ( i == 1 ) { //Il cliente deve pagare
				codaCassa.acquire(); //Cerco di acquisire la cassa e se non riesco mi accodo
				System.out.println("Il cliente: "+Thread.currentThread().getName()+" sta pagando");
			}else { //Il cliente deve consumare
				codaBancone.acquire(); //Vado al bancone e consumo se posso oppure mi metto in coda
				System.out.println("Il cliente: "+Thread.currentThread().getName()+" sta consumando al bancone");
			}
		}catch (Exception e) {
			System.out.println("Errore nell'operazione del cliente");
		}
	}//inizia

	@Override
	public void finisci(int i) {
		//In questo metodo rilascio i permessi dopo che i thread hanno svolto la propria operazione
		if ( i == 1 ) { //Il cliente ha pagato quindi deve spostarsi sulla coda bancone
			System.out.println("Il cliente: "+Thread.currentThread().getName()+" ha pagato");
			codaCassa.release(); //Lascio la cassa al prossimo in coda
		}else { //Il cliente ha consumato quindi si sposta sulla coda della cassa
			System.out.println("Il cliente: "+Thread.currentThread().getName()+" ha consumato");
			codaBancone.release();
		}
	}//finisci

}//BarModSemaphore