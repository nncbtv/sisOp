package sisOp.Tracce.AziendaAgricola.Codice;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class AziendaAgricolaSemaphore extends AziendaAgricola{

	private Semaphore codaCassa;
	private Semaphore codaMagazzino;
	private Semaphore attendiMagazziniere;
	private Semaphore svegliaMagazziniere;
	
	public AziendaAgricolaSemaphore() {
		codaCassa = new Semaphore(1, true); //True mi gestisce il fifo
		codaMagazzino = new Semaphore(1, true);
		attendiMagazziniere = new Semaphore(0); //Un semaforo con zero permessi per far attendere i clienti mentre refillo il magazzino
		svegliaMagazziniere = new Semaphore(0); //Un semaforo su cui fa attendere il magazziniere per il refill
	}//costruttore

	@Override
	void pagaSacchetti(int nSacchetti) throws InterruptedException {
		try {
			codaCassa.acquire(); //Cerco di essere il primo dell coda
			super.saldoFinale = super.saldoFinale + (nSacchetti*super.costoSacco); //Pago
			TimeUnit.SECONDS.sleep(2); //Simulo il pagamento
			System.out.println("Pagato: "+super.costoSacco*nSacchetti+" dal "+Thread.currentThread().getName());
			codaCassa.release(); //Lascio il posto al prossimo
		}catch (Exception e) {
			System.out.println("Errore nella cassa");
		}
	}//pagaSacchetti

	@Override
	void ritiroSacchetti(int nSacchetti) throws InterruptedException {
		try {
			codaMagazzino.acquire();
			if ( super.sacchiDisponibili<=nSacchetti ) {
				System.out.println(Thread.currentThread().getName()+" ha svegliato il magazziniere, mancano "+(nSacchetti-super.sacchiDisponibili)+" sacchetti");
				//Devo chiamare il magazziniere per rifornire prima di ritirare il mio carico
				svegliaMagazziniere.release(); //Passo un permesso al semaforo
				attendiMagazziniere.acquire(); //Mi metto in attesa per il via dal magazziniere
			}
			//Da questo punto in poi il magazzino è stato caricato o comunque i sacchetti ci sono
			super.sacchiDisponibili = super.sacchiDisponibili - nSacchetti; //Aggiorno il magazzino con il nuovo numero di sacchetti
			TimeUnit.SECONDS.sleep(4); //Simulazione di carico
			System.out.println("Caricato: "+nSacchetti+" dal "+Thread.currentThread().getName());
			codaMagazzino.release();
		}catch (Exception e) {
			System.out.println("Errore nel ritiro sacchetti");
		}
	}//ritiroSacchetti

	@Override
	void refillMagazzino() throws InterruptedException {
		try {
			svegliaMagazziniere.acquire(); //Mi devo svegliare segnalato da un altro thread
			super.sacchiDisponibili = 200;
			TimeUnit.SECONDS.sleep(6);
			System.out.println("Sono il magazziniere, ho caricato il magazzino");
			attendiMagazziniere.release(); //Faccio ripartire il thread fermato
		}catch (Exception e) {
			System.out.println("Errore nel refill");
		}
	}//refillMagazzino
	
}//AziendaAgricolaSemaphore