package sisOp.Tracce.ProntoSoccorso.Codice;

public class Paziente extends Thread {

	private ProntoSoccorso p;
	private int codice;
	
	public Paziente(ProntoSoccorso pIn, int c) {
		this.p = pIn;
		this.codice = c;
	}//costruttore
	
	@Override
	public void run() {
		try {
			p.accediPaziente();
			p.esciPaziente();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}//run
	
	public int getCodice() {
		return codice;
	}//getCodice
	
}//Paziente