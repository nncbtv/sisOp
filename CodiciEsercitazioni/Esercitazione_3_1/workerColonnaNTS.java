package sisOp.CodiciEsercitazioni.Esercitazione_3_1;

public class workerColonnaNTS implements Runnable {
	
	int [][] matrice; //Matrice da dove prendere gli elementi
	private int nOperazioni; //Numero di operazioni che deve fare il thread
	private int colonna; //Colonna da incrementare
	
	public workerColonnaNTS(int [][] matrice, int nOperazioni, int colonna) {
		if ( matrice == null || nOperazioni == 0 ) throw new IllegalArgumentException();
		this.matrice = matrice;
		this.nOperazioni = nOperazioni;
		this.colonna = colonna;
	}//Costruttore, non creo una copia ma lavoro sullo stesso riferimento della colonna

	@Override
	public void run() {
		for ( int i = 0; i<nOperazioni; i++) {//nOperazioni
			for ( int j = 0; j<matrice.length; j++) {//Scorro le righe
				matrice[j][colonna] = matrice[j][colonna]-1;
			}//scorrimento righe
		}//noperazioni
	}//run

}//WorkerColonnaNTS
