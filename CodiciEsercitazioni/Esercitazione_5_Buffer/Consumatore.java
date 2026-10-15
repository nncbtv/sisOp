package sisOp.CodiciEsercitazioni.Esercitazione_5_Buffer;

import java.util.concurrent.TimeUnit;

public class Consumatore implements Runnable {

	private Buffer buffer;

	public Consumatore (Buffer b) {
		this.buffer = b;
	}//Costruttore

	@Override
	public void run() {
		try {
			while (true) {
				int i = buffer.get();
				consuma(i);
			}
		}catch(InterruptedException e) {
			System.out.println("Errore nel consumatore");
		}
	}//run

	private void consuma(int i ) throws InterruptedException {
		TimeUnit.SECONDS.sleep(i); //Mi addormento quando basta per consumare il prodotto
	}

}//Consumatore
