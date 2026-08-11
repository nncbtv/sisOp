package sisOp.Tracce.AziendaAgricola.Codice;

public class Magazziniere implements Runnable {
	
	AziendaAgricola azienda;
	
	public Magazziniere(AziendaAgricola a) {
		this.azienda = a;
	}//costruttore

	@Override
	public void run() {
		try {
			while ( !Thread.currentThread().isInterrupted() ) {
				azienda.refillMagazzino();
			}
		} catch (InterruptedException e) {
			System.out.println("Il magazziniere ha finito il turno");
		}
	}//run
	
}//Magazziniere