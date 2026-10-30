package sisOp.Tracce.ProntoSoccorso.Codice;

public abstract class ProntoSoccorso {

	protected final int N_PAZIENTI;
	
	public ProntoSoccorso(int clienti) {
		this.N_PAZIENTI = clienti;
	}//Costruttore
	
	abstract boolean isAperto(); //Metodo aggiuntivo per far terminare il medico dal ciclo
	
	abstract void iniziaVisita() throws InterruptedException;
	abstract void terminaVisita() throws InterruptedException;
	abstract void accediPaziente() throws InterruptedException;
	abstract void esciPaziente() throws InterruptedException;
}//ProntoSoccorso