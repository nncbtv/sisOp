package sisOp.Tracce.CallCenter.Codice.Es3;

import java.util.concurrent.Semaphore;

public class Esercizio3 {

	private static Semaphore[] sems;
	
	public static void main(String[] args) {
		int n = 10;
		sems = new Semaphore[n];
		MyThread[] threads = new MyThread[n];
		for (int i = 0; i < sems.length; i++ ) {
			sems[i] = new Semaphore(0);
		}
		for ( int i = 0; i<threads.length; i++ ) {
			threads[i] = new MyThread(i,n);
			threads[i].start();
		}
	}//main
	
	static class MyThread extends Thread{
		private int i, n;
		public MyThread(int i, int n) {
			this.i = i; this.n = n;
		}//Costruttore
		
		@Override
		public void run() {
			try {
				if ( i > n/2 ) {
					sems[n-1-i].release();
				}else{
					sems[i].acquire();
					System.out.println("Thread "+i+" "+Thread.currentThread().getState());
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}//run
	}//MyThread
	
}//Esercizio3

/*
 *COMMENTO ESERCIZIO
 *Inizializziamo due vettori lunghi n, uno di tipo thread a cui associamo un'istanza della classe MyThread e come valori passiamo l'indice
 *del vettore a cui stiamo assegnando l'istanza e il valore n, ogni istanza controlla se il suo id è minore di 5, se è così rilascia il semaforo
 *per il thread che attende nella sua posizione speculare dell'array, altrimenti si mette in attesa e aspetta sul semaforo.
 *L'altro array invece è di tipo Semaphore con i semafori inizializzati tutti a zero come permessi.
 *Il programma però non termina poichè in attesa resteranno fermi il thread 4 e 5 dato che la condizione dell'iff è errata, dovrebbe essere >= per consentire al threa 5 di sbloccare il 4
 */