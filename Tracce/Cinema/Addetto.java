package sisOp.Tracce.Cinema;

public class Addetto extends Thread {

	private final Cinema cinema; //Cinema a cui l'addetto deve fare testo
	private final int salaAddetto;
	public Addetto(Cinema c, int salaAssegnata) {
		this.cinema = c;
		this.salaAddetto = salaAssegnata;
	}//Costruttore

	@Override
	public void run() {
		while( cinema.isAperto() ) { //Finché ci sono clienti da servire l'addetto lavora
			try {
				cinema.controllaBiglietto(); //Controllo il biglietto per la sala
				cinema.avviaProiezione(); //Avvio la proiezione nella sala
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}//run

	public int getSala() {
		return salaAddetto;
	}//getSala

}//Addetto