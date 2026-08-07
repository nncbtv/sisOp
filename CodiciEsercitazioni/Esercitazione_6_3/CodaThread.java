package sisOp.Esercitazione_6_3;

public abstract class CodaThread {
	
	private int n;
	private Printer[] lp;
	//Metodo che i thread devono svolgere a turno ordinato, qui salutano, dal primo creato all'ultimo
	public abstract void stampa(int turno) throws InterruptedException;
	//Inizializzo gli oggetti runnable
	public CodaThread(int nin) {
		this.n = nin;
		lp = new Printer [nin];
		for ( int i = 0; i<nin; i++) {
			lp[i] = new Printer(this, i);
		}
	}//CodaThread
	//Li faccio partire
	public void avviaStampa() {
		for ( int i = 0; i<n; i++) {
			new Thread(lp[i]).start();
		}
	}//avviaStampa
}//CodaThread