package sisOp.CodiciEsercitazioni.Esercitazione_5_MemoriaCondivisa;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Scrittore implements Runnable {
	
	private MemoriaCondivisa memoria;
	
	public Scrittore(MemoriaCondivisa memoriaCondivisa) {
		this.memoria = memoriaCondivisa; //Salvo la memoria su cui lavorare
	}

	@Override
	public void run() {
		try {
			while(true) {
				memoria.inizioScrittura();
				scrivi();
				memoria.fineScrittura();
				faiAltro();
			}
		}catch(InterruptedException e) {
			System.out.println("Errore nello scrittore!");
		}
	}//run
	
	private static int MIN_TEMPO_SCRITTURA = 2;
	private static int MAX_TEMPO_SCRITTURA = 3;
	private static int MIN_TEMPO_ALTRO = 10;
	private static int MAX_TEMPO_ALTRO = 20;
	private Random random = new Random();
	
	private void scrivi() throws InterruptedException {
		attendi(MIN_TEMPO_SCRITTURA, MAX_TEMPO_SCRITTURA);
	}
	
	private void faiAltro() throws InterruptedException {
		attendi(MIN_TEMPO_ALTRO, MAX_TEMPO_ALTRO);
	}
	private void attendi ( int min, int max ) throws InterruptedException {
		TimeUnit.SECONDS.sleep(random.nextInt(max-min+1)+min);
	}

}//Scrittore