package sisOp.CodiciEsercitazioni.Esercitazione_5_BufferIllimitato;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Produttore implements Runnable {

	private static final int MAX_RANDOM = 10;
	private Random random = new Random();
	private Buffer buffer; //Buffer su cui lavorare

	public Produttore ( Buffer b ) {
		this.buffer = b;
	}//Costruttore della classe per prendere il buffer su cui lavore

	@Override
	public void run() {
		try {
			while (true) {
				int i = produci();
				buffer.put(i);
			}
		}catch(InterruptedException e) {
			System.out.println("Errore nel produttore");
		}
	}//run

	private int produci() throws InterruptedException{
		TimeUnit.SECONDS.sleep(1); //Mi addormento per 1s come se stessi producendo
		return random.nextInt(MAX_RANDOM); //Creo un prodotto, valore random da consumare poi
	}

}//Produttore