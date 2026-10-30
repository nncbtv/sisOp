package sisOp.Tracce.ProntoSoccorso.Codice.Es3;

import java.util.concurrent.Semaphore;

public class Main {

	public final static int N = 10;
	public static Semaphore[] arr = new Semaphore[] {new Semaphore(0), new Semaphore(N)};
	
	public static void main(String[] args) throws InterruptedException {
		
		Thread[] threads = new Thread[N];
		for ( int i = 0; i<N; i++ ) {
			threads[i] = new Task(i);
			threads[i].setDaemon(true);
			threads[i].start();
		}
		for ( int i = 0; i<N; i++ ) {
			threads[i].join();
			System.out.print(threads[i].getState());
		}
		
	}//main

	static class Task extends Thread{
		private int id;
		
		public Task(int n) {
			this.id = n;
		}//Costruttore
		
		public void run() {
			int m = id % 2;
			try {
				arr[m].acquire();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}//run
		
	}//Task
	
}//Main

/*DESCRIZIONE ESERCIZIO PER PUNTI CHIESTI DALLA TRACCIA
 * 1) L'applicazione java crea un vettore di threads, ogni elemento di esso è un'istanza della classe Task a cui fa riferimento un indice
 * passato in fase di inizializzazione dell'array che diventa un id dell'istanza, ogni elemento viene poi impostato come thread demone 
 * e lanciato subito dopo. Una volta avviato questo array di thread si attende che vengano completati tutti i suoi elementi in oridine
 * dal primo all'ultimo.
 * 2) L'applicazione non produce nessun output poiché il main invoca rigidamente la join su ogni thread lanciato, partendo da zero che
 * attende su arr[0] dove abbiamo un semaforo con 0 permessi e si blocca tutto generando un deadlock.
 * 3) Purtroppo l'applicazione non termina poiche aspetta che finiscano i thread che stanno aspettando su un semaforo con
 * zero permessi, in particolare i thread con id 0,2,4,6,8 perché col modulo 2 attendono tutti sul semaforo di arr[0] senza permessi.
 * */