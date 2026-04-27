package sisOp.Esercitazione_3_1;

public class workerColonnaNTS implements Runnable {
	
	int [][] matrice; //Colonna su cui devo lavorare
	private int nOperazioni; //Numero di operazioni che deve fare il thread
	
	public workerColonnaNTS(int [][] matrice, int nOperazioni) {
		if ( matrice == null ) throw new IllegalArgumentException();
		this.matrice = matrice;
		this.nOperazioni = nOperazioni;
	}//Costruttore, non creo una copia ma lavoro sullo stesso riferimento della colonna

	@Override
	public void run() {
		for ( int i = 0; i<nOperazioni; i++) {
			for ( int j = 0; j<matrice[0].length; j++) {
				for ( int k = 0; k<matrice.length; k++) {
					matrice[k][j] = matrice[k][j]+1; //Aggiungo alla colonna +1
				}
			}
		}
	}//run

}//WorkerColonnaNTS
