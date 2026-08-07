package sisOp.Tracce.AziendaAgricola.Codice;

public class Magazziniere implements Runnable {
	
	AziendaAgricola azienda;
	private boolean chiusura = false;
	
	public Magazziniere(AziendaAgricola a) {
		this.azienda = a;
	}//costruttore

	@Override
	public void run() {
		try {
			while ( !chiusura ) {
				azienda.refillMagazzino();
			}
		} catch (Exception e) {
			System.out.println("Errore nel magazziniere");
		}
	}//run

	public void chiusura(){
		this.chiusura = true;
	}
	
}//Magazziniere