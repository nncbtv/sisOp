package sisOp.Tracce.Funivia.Codici;

import java.util.concurrent.Semaphore;

public class FuniviaSempahore extends Funivia {
	
	private Semaphore salitaPersone;
	private Semaphore salitaCiclisti;
	private Semaphore partenzaFunivia;
	private Semaphore fineCorsa;
	private Semaphore ritornoFunivia;
	private Boolean corsaPedoni = true; //Inizio la prima corsa con i pedoni
	
	public FuniviaSempahore() {
		salitaPersone = new Semaphore(0, true); //possono salire 6 persone
		salitaCiclisti = new Semaphore(0, true); //possono salire al massimo 3 ciclisti
		partenzaFunivia = new Semaphore(0); //Parto appena sono pieni i posti della funivia
		ritornoFunivia = new Semaphore(0); //Scendo appena sono scesi tutti i thread
		fineCorsa = new Semaphore(0); //Mi serve per svegliare poi i thread che hanno fatto il giro sulla funivia
	}//FuniviaSemaphore

	@Override
	void pilotaStart() {
		//Questo metodo fa salire i passeggeri sulla funivia, una volta salito un tipo di passeggeri continuo con quello
		try {
			System.out.println("Attendo che i passeggeri salgano");
			if ( corsaPedoni ) { //Informo che si sono liberati posti sulla funivia, faccio direttamente così perché tanto non ho un altra corsa o altro thread pilota
				salitaPersone.release(6);	
			}else {
				salitaCiclisti.release(3);	
			}
			//Devo attendere che tutti salgano sulla funivia e appena è piena blocco la salita e parto
			partenzaFunivia.acquire((corsaPedoni)?6:3); //Sono i posti presenti sulla funivia, in base al tipo di corsa
			System.out.println("Tutti i passeggeri sono saliti, partiamo");
		}catch (Exception e) {
			System.out.println("Errore nella salita passeggeri a valle");
		}
	}//pilotaStart

	@Override
	void pilotaEnd() {
		//Questo metodo fa scendere i passeggeri e imposta il nuovo tipo di corsa
		try {
			fineCorsa.release((corsaPedoni)?6:3); //Sveglio i thread sui sedili della funivia
			ritornoFunivia.acquire((corsaPedoni)?6:3); //Aspetto che i posti sulla funivia siano liberi
			System.out.print("Sono scesi tutti, ho trasportato: ");
			
			if (corsaPedoni) {
				System.out.println("pedoni");
			}else {
				System.out.println("Ciclisti");
			}
			super.personeValle = super.personeValle - ((corsaPedoni)?6:3); //Aggiorno il contatore per le persone a valle altrimenti giro inutilmente
			corsaPedoni = !corsaPedoni; //Cambio tipo di corsa
			
		}catch (Exception e) {
			System.out.println("Errore nella discesa passeggeri a monte");
		}
	}//pilotaEnd

	@Override
	void turistaSali(int tipoTurista) {
		try {
			if ( tipoTurista == 0 ) { //Se sono un pedone e la corsa è per me salgo
				salitaPersone.acquire(); //Questo per forza mi darà 6 thread qui dentro
				System.out.println("Sono: "+Thread.currentThread().getName()+ ", pedone e sto salendo");
				partenzaFunivia.release();
			}
			if ( tipoTurista == 1 ) { //Se sono un ciclista e la corsa è per me salgo
				salitaCiclisti.acquire();
				System.out.println("Sono: "+Thread.currentThread().getName()+ ", ciclista e sto salendo");
				partenzaFunivia.release(); //Ne rilascio due perché uno lo occopa il tipo e l'altro la bici
			}
		}catch (Exception e) {
			System.out.println("Errore salita posto funivia");
		}
	}//TuristaSali

	@Override
	void turistaScendi(int tipoTurista) {
		if ( tipoTurista == 0 ) { //Se sono un pedone libero un posto
			try {
				fineCorsa.acquire(); //Attede che la corsa finisca prima di andare avanti
				System.out.println("Sono: "+Thread.currentThread().getName()+ ", pedone e sto scendendo");
				ritornoFunivia.release();
			}catch (Exception e) {
				System.out.println("Errrore nella discesa del posto pedone a monte");
			}
		}
		if ( tipoTurista == 1 ) { //Se sono un ciclista libero due posti
			try {
				fineCorsa.acquire(); //Resto seduto finchè non mi da il via per scendere il pilota
				System.out.println("Sono: "+Thread.currentThread().getName()+ ", ciclista e sto scendendo");
				ritornoFunivia.release();
			}catch (Exception e) {
				System.out.println("Errore nella discesa del posto ciclista a monte");
			}
		}
	}//turistaScendi

}//FuniviaSemaphore