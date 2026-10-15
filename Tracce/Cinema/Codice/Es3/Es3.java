package sisOp.Tracce.Cinema.Codice.Es3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Es3 {

	static int N = 10;
	static Lock l = new ReentrantLock();
	static Condition [] conditions = new Condition[N];
	static boolean [] flags = new boolean[N];
	public static void main (String[] args) throws InterruptedException{
		Thread[] threads = new Thread[N];
		for ( int i = 0; i<N; i++ ) {
			threads[i] = new MyThread(i);
			conditions[i] = l.newCondition();
			if ( i%2==0 ) {
				flags[i] = true;
			}
			threads[i].start();
		}
	}//main

	static class MyThread extends Thread{
		private final int id;
		public MyThread(int id) {
			this.id = id;
		}
		@Override
		public void run() {
			l.lock();
			try {
				while ( !flags[id] ) {
					conditions[id].await();
				}
				System.out.printf("Thread %d, state: %s\n",id,getState());
				flags[N-id-1] = true;
				conditions[N-id-1].signal();
			}catch (Exception e) {
				e.printStackTrace();
			}finally {
				l.unlock();
			}
		}
	}
}//Es3

/*RISOLUZIONE
 * Il main crea un vettore di Thread e li avvia tutti dopo aver inizializzato l'array condition e impostato come false le celle dell'array boleano con indice dispari.
 * La classe MyThrad aspetta nella propria condition se la cella passata nel campo id è ha false finche non viene posta a true e svegliato per proseguire.
 * Subito dopo la wait sulla condition un thread dispari svegliato da uno pari stampa lo stato del Thread e pone a true la cella associata al Thread N-id-1 svegliandolo successivamente, i pari non entrano nella wait sulla condition.
 * L'output non è deterministico, infatti esso dipende da come i thread posseggono il lucchetto e a che tempo riescono ad eseguire l'istruzione di stampa, sostanzialemnte però ognuno stamperà il suo stato.
 * L'applicazione termina con la stampa dello stato di tutti i thread, lo stato sarà RUNNABLE
 */