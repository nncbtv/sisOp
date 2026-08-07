package sisOp.Tracce.AziendaAgricola.Codice;

import java.util.Random;

public class Cliente implements Runnable {

	private AziendaAgricola azienda;
	
	public Cliente(AziendaAgricola a) {
		this.azienda = a;
	}//Costruttore
	
	@Override
	public void run() {
		try {
			Random r = new Random();
			int sacchettiCliente = r.nextInt(1,11); //[1,11)
			azienda.pagaSacchetti(sacchettiCliente); 
			azienda.ritiroSacchetti(sacchettiCliente);
		}catch (Exception e) {
			System.out.println("Errore nel cliente");
		}
	}//run

}//Cliente