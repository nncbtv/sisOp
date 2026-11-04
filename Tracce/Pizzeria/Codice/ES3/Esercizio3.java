package sisOp.Tracce.Pizzeria.Codice.ES3;

import java.util.concurrent.TimeUnit;

public class Esercizio3 {

	public static void main(String[] args) {
		int n = 10;
		MyThread [] threads = new MyThread[n];
		for ( int i = 0; i<n; i++ ) {
			threads[i] = new MyThread(i, threads);
		}
		for ( int i = 0; i<n; i++ ) {
			threads[i].start();
		}
	}//main
	
	static class MyThread extends Thread{
		private int id;
		private MyThread[] threads;
		public MyThread(int id, MyThread[] threads) {
			this.id = id;
			this.threads = threads;
		}//Costruttore
		@Override
		public void run() {
			try {
				int p = id - 1;
				if ( p>=0 ) {
					threads[p].join();
				}
				TimeUnit.SECONDS.sleep(id);
				if ( p>= 0 ) {
					System.out.printf("Thread-%d Thread-%d %s\n",id,p,threads[p].getState());
				}else {
					System.out.printf("Thread-%d\n", id);
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}//run
	}//MyThread
}//Esercizio3

/*COMMENTO ESERCIZIO
 * 1)L'applicazione avvia una pool di thread della classe MyThread, dopo averli inizializzati li lancia uno dopo l'altro, essi eseguono 
 * il loro metodo run nel cui interno troviamo in base all'id delle istruzioni da svolgere. Se l'id è superiore a 0 il thread aspetta
 * il suo precedente nell'array, quando esso termina attende un tempo in secondi pari al suo id e stampa il suo stato più quello del
 * thread precedente. Il primo thread con id pari a zero non aspetta, entra e stampa direttamtente il suo id terminando subito dopo.
 * 2)L'output comincia con il primo thread avente id zero che stampa il suo id, dopodiché tutti gli altri stamperanno il loro id più
 * quello del thread precendente con stato TERMINATED.
 * 3)L'applicazione termina appena tutti i thread finsicono il loro metodo run.
 */