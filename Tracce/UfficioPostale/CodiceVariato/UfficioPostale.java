package sisOp.Tracce.UfficioPostale.CodiceVariato;

public abstract class UfficioPostale {

	protected final int CLIENTI_DA_SERVIRE;
	protected final int MASSIMO_TICKET_CODA = 50;
	protected final int NUMERO_CODE;
	
	public UfficioPostale(int n, int m) {
		this.CLIENTI_DA_SERVIRE = n;
		this.NUMERO_CODE = m;
	}//costruttore
	
	abstract boolean isAperto(); //Metodo aggiunto per gestire il flusso
	abstract boolean ritiraTicket(String tipoTicket) throws InterruptedException;
	abstract void attendiSportello(String tipoTicket) throws InterruptedException;
	abstract void prossimoCliente() throws InterruptedException;
	abstract void eseguiOperazione() throws InterruptedException;
	
}//UfficioPostale