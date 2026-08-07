package sisOp.Esercitazione_5_CinqueFilosofi;

import java.util.concurrent.Semaphore;

public class TavoloSem extends Tavolo {
	
	Semaphore [] bacchetta = new Semaphore[getFilosofi()]; //Le bacchette sono le risorse da assegnare
	Semaphore mutex = new Semaphore(1); //Per garantire assenza di deadlock
	
	public TavoloSem() {
		for (int i = 0; i<getFilosofi(); i++) {
			bacchetta[i] = new Semaphore(1); //Inizializzo ad 1 ogni permesso per la bacchetta, una bacchetta un filosofo
		}
	}
	
	@Override
	public void prendiBacchette(int i) throws InterruptedException {
		mutex.acquire(); //Atomicamente sono l'unico a prendere le bacchette, cosi non c'è un altro che mi prende la mia
		bacchetta[i].acquire(); //Prendo la bacchetta alla mia dx
		bacchetta[(i+1)%5].acquire(); //Prendo la bacchetta alla mia sx
		//Uso il modulo per non andare fuori indice
	}//prendiBacchette

	@Override
	public void rilasciaBacchette(int i) {
		bacchetta[i].release();
		bacchetta[(i+1)%5].release();
		mutex.release();//rilascio la presa delle bacchette
	}//rilasciaBacchette

	//Questo accesso alle bacchette secondo me è il migliore, fare il filosofo mancino non avrebbe senso
	//Dato che dipende dal numero di filosofi anche se per ovviare potrei fare il primo
	
}//TavoloSem