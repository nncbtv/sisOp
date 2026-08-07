package sisOp.Esercitazione_3_1;

import java.util.concurrent.atomic.AtomicInteger;

public class workerColonnaTS implements Runnable {
	
	AtomicInteger [][] matrice; //MAtrice da cui prendere gli elementi
	private int nOperazioni; //Numero di operazioni che deve fare il thread
	private int colonna; //Colonna su cui fare +1
	public workerColonnaTS(AtomicInteger [][] matrice, int nOperazioni, int colonna) {
		if ( matrice == null || nOperazioni == 0 ) throw new IllegalArgumentException();
		this.matrice = matrice;
		this.nOperazioni = nOperazioni;
		this.colonna = colonna;
	}//Costruttore, non creo una copia ma lavoro sullo stesso riferimento della colonna

	@Override
	public void run() {
		for ( int i = 0; i<nOperazioni; i++) {//nOperazioni
			for ( int j = 0; j<matrice.length; j++) {//Scorro le righe
				matrice[j][colonna].incrementAndGet(); //Aggiungo alla colonna +1
			}//scorrimento righe
		}//noperazioni
	}//run

}//WorkerColonnaTS