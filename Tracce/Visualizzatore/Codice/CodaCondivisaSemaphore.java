package sisOp.Tracce.Visualizzatore.Codice;

import java.util.concurrent.Semaphore;

public class CodaCondivisaSemaphore extends CodaCondivisa {

	//Semafori per gestire i permessi e la mutua esclusione per l'inserimento di dati multipli
	private Semaphore p; //Numero di dati da inserire nella coda
	private Semaphore mutex; //Mutua esclusione

	public CodaCondivisaSemaphore() {
		this.p = new Semaphore(100); //Inizialmente posso inserire tanti dati quanto la dimensione della coda
		this.mutex = new Semaphore(1); //Inserimento in mutua esclusione per non rompere i dati
	}//Costruttore

	@Override
	void insert(int x) {
		try {
			p.acquire(x); //Comunico alla coda quanti dati voglio inserire
			mutex.acquire(); //Cerco la mutua esclusione per la scrittura
			for (int i =0; i<x; i++) {
				q.add(Thread.currentThread().getName()+" inserisce stringa "+(i+1));
			}
			//dopo aver scritto rilascio il permesso di scrittura
			mutex.release();
		} catch (InterruptedException e) {
			System.out.println("Errore nell'utente");
		}
	}//insert

	@Override
	void print() {
		//Siccome è una blockingQueue mi blocca se la coda è vuota
		System.out.println("Visualizzatore: "+q.peek());
		p.release(); //Rilascio man mano che leggo nuovo spazio nella coda
	}//print

}//CodaCondivisaSemaphore