package sisOp.Tracce.Cinema;

public class Cliente extends Thread{

	private final Cinema cinema; //Cinema a cui il cliente deve fare testo

	public Cliente(Cinema c) { //Passo nel costruttore un cinema
		this.cinema = c;
	}//Costruttore

	@Override
	public void run() {
		try {
			cinema.vendiBiglietto(); //Per la variante 4b
			int salaScelta = cinema.mettiInCoda(); //Scelgo la sala in cui voglio guardare il film
			cinema.entraInSala(salaScelta); //entro nella sala per guardare il film
			//Esco dal cinema
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//run
}//Cliente