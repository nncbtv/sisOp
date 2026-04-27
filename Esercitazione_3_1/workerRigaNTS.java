package sisOp.Esercitazione_3_1;

public class workerRigaNTS implements Runnable {

	int [][] matrice; //Riga su cui devo lavorare
	private int nOperazioni; //Numero di operazioni che deve fare il thread
	
	public workerRigaNTS(int [][] matrice, int nOperazioni) {
		if ( matrice == null ) throw new IllegalArgumentException();
		this.matrice = matrice;
		this.nOperazioni = nOperazioni;
	}//Costruttore, non creo una copia ma lavoro sullo stesso riferimento della riga

	@Override
	public void run() {
		for ( int i = 0; i<nOperazioni; i++) {
			for ( int j = 0; j<matrice.length; j++) {
				for ( int k = 0; k<matrice[j].length; k++) {
					matrice[j][k] = matrice[j][k]-1; //Aggiungo alla riga -1
				}
			}
		}
	}//run

}//workerRigaNTS