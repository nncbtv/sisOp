package sisOp.Tracce.AziendaAgricola.Codice;

public class Magazziniere implements Runnable {
	
	AziendaAgricola azienda;
	
	public Magazziniere(AziendaAgricola a) {
		this.azienda = a;
	}//costruttore

	@Override
	public void run() {
		try {
			azienda.refillMagazzino();
		} catch (Exception e) {
			System.out.println("Errore nel magazziniere");
		}
	}//run

}//Magazziniere