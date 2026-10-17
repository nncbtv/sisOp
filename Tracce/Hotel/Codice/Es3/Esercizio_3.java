package sisOp.Tracce.Hotel.Codice.Es3;

import java.util.concurrent.Semaphore;

public class Esercizio_3 {

	private static int N=4;
	private static Semaphore[] sems = new Semaphore[]{new Semaphore(N), new Semaphore(0)};
	private static Semaphore mutex = new Semaphore (1);
	
	static class Thread0 extends Thread{
		public void run() {
			try {
				sems[0].acquire();
				System.out.print("0");
				sems[1].release();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}//Thread0
	
	static class Thread1 extends Thread{
		public void run() {
			try {
				mutex.acquire();
				sems[1].acquire(N);
				System.out.print("\n1\n");
				N--;
				if ( N > 0 ) {
					sems[0].release();
					mutex.release();
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}//Thread1
	
	public static void main(String[] args) {
		while ( true ) {
			new Thread0().start();
			new Thread1().start();
			try {
				Thread.sleep(1);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}//main

}//Esercizio_3

/*SPIEGAZINE ESERCIZIO
 * Il nostro main avvia due Thread e poi dorme, questo lo esegue in loop, soltanto che va in deadlock perché Thread1 si ferma al secondo giro sul semaforo
 * aspettandosi n permessi che non gli vengono dati dal Thread0 poiché al secondo giro lui sul suo semfaro aveva un solo permesso dato da Thread 1.
 * Produce esattamente 000010 e poi va in deadlock.
 * L'applicazione non termina per il motivo spigeato precedentemente.
 */