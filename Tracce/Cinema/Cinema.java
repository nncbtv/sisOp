package sisOp.Tracce.Cinema;

public abstract class Cinema {

	protected final int N_SALE, CAPIENZA_SALE, N_CLIENTI;
	//Suppongo che il numero di sale sia identico al numero di addetti, la traccia non lo specifica

	public Cinema(int sale, int capienza, int clienti) {
		this.N_SALE = sale;
		this.CAPIENZA_SALE = capienza;
		this.N_CLIENTI = clienti;
	}//Costruttore

	//Espongo i metodi per i thread
	abstract boolean isAperto(); //Metodo Aggiunto per ciclare gli addetti
	abstract void vendiBiglietto() throws InterruptedException; //Metodo Variante 4b
	abstract int mettiInCoda() throws InterruptedException;
	abstract void controllaBiglietto()throws InterruptedException;
	abstract void entraInSala(int salaScelta)throws InterruptedException;
	abstract void avviaProiezione()throws InterruptedException;

}//Cinema