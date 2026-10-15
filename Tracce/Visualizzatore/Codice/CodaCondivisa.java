package sisOp.Tracce.Visualizzatore.Codice;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

public abstract class CodaCondivisa {

	private final int visualizzatori = 1, utenti = 10;

	protected Queue<String> q = new ArrayBlockingQueue<>(100); //Coda di 100 elementi bloccante
	//Metodi da implementare nei thread
	abstract void insert(int x);
	abstract void print();
	//Metodo di avvio
	public void test() {
		//Avvio dei visualizzatori
		Thread [] v = new Thread [visualizzatori];
		for ( int i = 0; i<visualizzatori; i++ ) {
			v[i] = new Visualizzatore(this);
			v[i].start();
		}

		//Avvio degli utenti
		Thread [] u = new Thread [utenti];
		for ( int i = 0; i<utenti; i++ ) {
			u[i] = new Utente(this);
			u[i].start();
		}

	}//test

}//CodaCondivisa