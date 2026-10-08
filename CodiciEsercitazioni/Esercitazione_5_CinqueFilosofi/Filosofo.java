package sisOp.CodiciEsercitazioni.Esercitazione_5_CinqueFilosofi;

public class Filosofo implements Runnable {
	
	private Tavolo t;
	private int posizione;

	public Filosofo(Tavolo t, int i) {
		this.t = t;
		this.posizione = i;
	}//Costruttore
	
	@Override
	public void run() {
		while(true) {
			try {
				t.prendiBacchette(posizione);
				System.out.println("Il filosofo: "+Thread.currentThread().getName()+" sta mangiando");
				Thread.sleep(1000); //Mi addormento per un secondo simulando di star mangiando
				t.rilasciaBacchette(posizione);
				System.out.println("Il filosofo: "+Thread.currentThread().getName()+" sta pensando");
				Thread.sleep(1000); //Mi addormento per un secondo e mezzo simulando di star pensando
			}catch(Exception e) {
				System.out.println("Errore nel filosofo: "+Thread.currentThread().getName());
			}
		}
	}//run

}
