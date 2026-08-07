package sisOp.Esercitazione_3_1;

public class workerRigaNTS implements Runnable {

	int [] riga; //Riga su cui devo lavorare
	private int nOperazioni; //Numero di operazioni che deve fare il thread
	
	public workerRigaNTS(int [] riga, int nOperazioni) {
		if ( riga == null || nOperazioni == 0) throw new IllegalArgumentException();
		this.riga = riga;
		this.nOperazioni = nOperazioni;
	}//Costruttore, non creo una copia ma lavoro sullo stesso riferimento della riga

	@Override
	public void run() {
		for ( int i = 0; i<nOperazioni; i++) {//Per ogni operazione diminuisco gli elementi delle righe di -1
			for ( int j = 0; j<riga.length; j++) {//Scorrimento elementi righe
				riga[j]=riga[j]-1;
			}//Scorrimento elementi righe
		}//noperazioni
	}//run

}//workerRigaNTS