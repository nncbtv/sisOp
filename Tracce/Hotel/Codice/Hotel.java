package sisOp.Tracce.Hotel.Codice;

public abstract class Hotel {

	protected final int N_CAMERE;
	protected final int N_CLIENTI;
	
	public Hotel(int camere, int clienti) {
		this.N_CAMERE = camere;
		this.N_CLIENTI = clienti;
	}//Costruttore
	
	abstract boolean isAperto(); //Metodo aggiunto per ciclare il receptionist
	abstract void mettiInCoda(int prenotazione) throws InterruptedException;
	abstract void checkOut() throws InterruptedException;
	abstract void iniziaCheckIn() throws InterruptedException;
	abstract void fineCheckIn() throws InterruptedException;
	
}//Hotel