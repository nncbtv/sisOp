package sisOp.Tracce.Cinema;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CinemaSem extends Cinema {

	private volatile boolean cinemaAperto = true; //Garantisce che gli addetti ciclino finché non servono tutti i clienti
	private Random r = new Random(); //Generatore numeri random per simulare le azioni
	private int [] postiInSala = new int[super.N_SALE]; //Posti rimanenti nella sala
	private int clientiCinema = 0; //Clienti da servire

	private Semaphore mutex = new Semaphore(1); //Mutex per proteggere le variabili condivise
	private Semaphore [] addettoSala = new Semaphore[super.N_SALE]; //Associazione di un addetto alla sala
	private Semaphore [] codaSala = new Semaphore[super.N_SALE]; //Code di clienti che vogliono entrare in sala
	private Semaphore [] attendiInizioProiezione = new Semaphore[super.N_SALE]; //Una volta entrati aspettano che il film cominci
	private Semaphore [] attendiFineProiezione = new Semaphore[super.N_SALE];
	private int [] bigliettiControllati = new int[super.N_SALE]; //Appena tutti i biglietti della sala sono controllati parte la proiezione

	public CinemaSem(int sale, int capienza, int clienti) {
		super(sale, capienza, clienti); //Rimando alla classe astratta
		for ( int i = 0; i<super.N_SALE; i++ ) {
			postiInSala[i] = super.CAPIENZA_SALE;
			addettoSala[i] = new Semaphore(0);
			codaSala[i] = new Semaphore(0, true); //True garantisce FIFO
			attendiInizioProiezione[i] = new Semaphore(0);
			attendiFineProiezione[i] = new Semaphore(0);
			bigliettiControllati[i] = 0;
		}
	}//Costruttore

	@Override
	boolean isAperto() { //Metodo usato per far ciclare il thread addetto, deve eseguire le sue operazioni finché ci sono clienti da servire
		return cinemaAperto;
	}//isAperto

	@Override
	int mettiInCoda() throws InterruptedException {
		mutex.acquire(); //Acquisisco il mutex per modificare i dati condivisi
		int salaScelta = r.nextInt(0,N_SALE); //Scelgo la sala
		while ( postiInSala[salaScelta] == 0 ) { //Non vado avanti finché non ne trovo una libera
			//Non devo avere nella sala 0 11 clienti e nella sala 1 6 clienti, errore!!!
			salaScelta = r.nextInt(0, N_SALE);
		}
		postiInSala[salaScelta] -=1; //Decremento i posti liberi
		mutex.release();
		addettoSala[salaScelta].release(); //Informo l'addetto che sono arrivato, ora lui mi controlla il biglietto
		codaSala[salaScelta].acquire(); //Non vado avanti finché non mi viene controllato il biglietto
		return salaScelta;
	}//mettiInCoda

	@Override
	void controllaBiglietto() throws InterruptedException {
		//Casting per accedere alla sala dell'addetto
		Thread corrente = Thread.currentThread();
		Addetto a = (Addetto) corrente;
		int salaAddetto = a.getSala();
		addettoSala[salaAddetto].acquire(); //Aspetto che arrivi qualche cliente alla porta della mia sala
		if ( isAperto() ) {
			mutex.acquire();
			System.out.println("Sono l'addetto "+Thread.currentThread().getName()+" della sala "+salaAddetto+" sto controllando il biglietto");
			bigliettiControllati[salaAddetto] += 1;
			codaSala[salaAddetto].release(); //Faccio passare il cliente avanti
			mutex.release();
		}
	}//controllaBiglietto

	@Override
	void entraInSala(int salaScelta) throws InterruptedException{
		//Per prima cosa attendo che cominci la proiezione
		attendiInizioProiezione[salaScelta].acquire();
		//Una volta iniziato il film lo guardo/vado via
		TimeUnit.SECONDS.sleep(2);
		//Quando vado via informo l'addetto
		attendiFineProiezione[salaScelta].release();
	}//entraInSala

	@Override
	void avviaProiezione() throws InterruptedException{
		//Casting per accedere alla sala dell'addetto
		Thread corrente = Thread.currentThread();
		Addetto a = (Addetto) corrente;
		int salaAddetto = a.getSala();
		//Se tutti i biglietti sono stati controllati parto con la proiezione
		if ( isAperto() ) {
			if ( bigliettiControllati[salaAddetto] == super.CAPIENZA_SALE ) { //Con il modulo sono sicuro che ogni tot capienza avvia la proiezione
				System.out.printf("Sono l'addetto %s della sala %d sta partendo la proiezione\n",Thread.currentThread().getName(), salaAddetto);
				attendiInizioProiezione[salaAddetto].release(super.CAPIENZA_SALE); //Faccio partire la proiezine
				mutex.acquire();
				clientiCinema = clientiCinema + super.CAPIENZA_SALE; //Segnalo che con la mia proiezione ho servito dei clienti
				if ( clientiCinema == super.N_CLIENTI ) {//Aggiorno lo stop se ho servito gli ultimi
					cinemaAperto = false;
					for ( int i= 0; i<super.N_SALE; i++ ) {
						addettoSala[i].release();
					}
				}
				mutex.release();
				attendiFineProiezione[salaAddetto].acquire(super.CAPIENZA_SALE); //Attendo che finisca il film
				System.out.printf("Sono l'addetto %s della sala %d è finita la proiezione\n",Thread.currentThread().getName(), salaAddetto);
			}
		}
	}//AvviaProiezione

	@Override
	void vendiBiglietto() throws InterruptedException{
		System.out.println("Questa è la prima variante, nue un di vindimu biglietta!!");
	}//vendiBiglietto

}//CinemaSem