package sisOp.Tracce.UfficioPostale.Codice.Es3;

public class Esercizio3 {

	public static void main(String [] args) {
		int n = 10;
		MyThread[] threads = new MyThread[n];
		for ( int i=0; i<n; i++ ) {
			threads[i] = new MyThread(i, threads);
		}
		for ( int i=0; i<n; i++ ) {
			threads[i].start();
		}
	}//main
	
	static class MyThread extends Thread{
		private int id;
		private MyThread[] threads;
		public MyThread(int i, MyThread[] t) {
			this.id =i;
			this.threads = t;
		}//costruttore
		@Override
		public void run() {
			try {
				int p = threads.length-id-1;
				if ( p >= threads.length/2 ) {
					threads[p].join();
					System.out.printf("Thread-%d Thread-%d %s \n", id,p,threads[p].getState());
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}//run
	}//MyThread
}//Esercizio3
/*
 * COMMENTO ESERCIZIO 3
 * 1)L'applicazione avvia una pool di thread della classe MyThread passandogli un id e la pool stessa, 
 * ogni thread le proprie operazioni del metodo run.
 * 2)L'output non è deterministico, anche se i thread vengono lanciati uno dopo l'altro non è detto che lo scheduler della CPU segua lo stesso 
 * criterio. Potrebbe produrre una stampa in cui abbiamo l'id di due thread e lo stato del secondo che è non deterministico per il motivo detto prima.
 * 3)Si l'applicazione termina, metà degli elementi della pool aspetta l'altra metà.
 * */