package sisOp.Tracce.Funivia.Codici;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;

public class FuniviaSempahore extends Funivia {
	
	private Semaphore salitaPersone;
	private Semaphore salitaCiclisti;
	private Semaphore mutex;
	private Queue<String> nominativi;
	private Semaphore partenzaFunivia;
	private Semaphore fineCorsa;
	private Semaphore ritornoFunivia;
	private boolean corsaPedoni = true; //Inizio la prima corsa con i pedoni
	
	public FuniviaSempahore(int pedoni, int ciclisti) {
		super(pedoni, ciclisti); //Passo sopra per poter inizializzare il tutto
		salitaPersone = new Semaphore(0, true); //possono salire 6 persone
		salitaCiclisti = new Semaphore(0, true); //possono salire al massimo 3 ciclisti
		mutex = new Semaphore(1); //Mi serve per gestire i nominativi dei thread che salgono
		nominativi = new LinkedList<>(); //Prendo i nomi di chi è salito sulla funivia
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
			mutex.acquire(); //Devo operare sulla coda
			while (!nominativi.isEmpty()) {
				System.out.println("Ho portato a monte: "+nominativi.poll()); //Stampo chi ho portato su
			}
			mutex.release(); //rialscio per le operrazioni sulla coda
			fineCorsa.release((corsaPedoni)?6:3); //Sveglio i thread sui sedili della funivia e gli dico che possono scendere
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
				mutex.acquire(); //Prendo la mutua esclusione per aggiungermi alla coda
				System.out.println("Sono: "+Thread.currentThread().getName()+ ", pedone e sto salendo");
				nominativi.add(Thread.currentThread().getName()); //Aggiungo il nome alla coda dei nominativi
				mutex.release(); //Rilascio la mutua esclusione
				partenzaFunivia.release();
			}
			if ( tipoTurista == 1 ) { //Se sono un ciclista e la corsa è per me salgo
				salitaCiclisti.acquire();
				mutex.acquire();
				System.out.println("Sono: "+Thread.currentThread().getName()+ ", ciclista e sto salendo");
				nominativi.add(Thread.currentThread().getName()); //Aggiungo il nome alla coda dei nominativi
				mutex.release(); //Rilascio la mutua esclusione
				partenzaFunivia.release(); //Informo di essere sceso
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
				ritornoFunivia.release(); //Informo il pilota di essere sceso
			}catch (Exception e) {
				System.out.println("Errrore nella discesa del posto pedone a monte");
			}
		}
		if ( tipoTurista == 1 ) { //Se sono un ciclista libero due posti
			try {
				fineCorsa.acquire(); //Resto seduto finchè non mi da il via per scendere il pilota
				ritornoFunivia.release(); //Informo il pilota di essere sceso
			}catch (Exception e) {
				System.out.println("Errore nella discesa del posto ciclista a monte");
			}
		}
	}//turistaScendi

}//FuniviaSemaphore