package sisOp.Esercitazione_5_MemoriaCondivisa;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Lettore implements Runnable {

private MemoriaCondivisa memoria;
	
	public Lettore(MemoriaCondivisa memoriaCondivisa) {
		this.memoria = memoriaCondivisa; //Salvo la memoria su cui lavorare
	}

	@Override
	public void run() {
		try {
			while(true) {
				memoria.inizioLettura(); //Notifico la memoria concreta che voglio leggere
				leggi(); //Metodo fittizio per simulare il tempo di lettura
				memoria.fineLettura(); //Notifico che ho finito
				faiAltro(); //Faccio altro prima di terminare, metodo fittizio
			}
		}catch(InterruptedException e) {
			System.out.println("Errore nello scrittore!");
		}
	}//run
	
	private static int MIN_TEMPO_LETTURA = 1;
	private static int MAX_TEMPO_LETTURA = 4;
	private static int MIN_TEMPO_ALTRO = 6;
	private static int MAX_TEMPO_ALTRO = 10;
	private Random random = new Random();
	
	private void leggi() throws InterruptedException {
		attendi(MIN_TEMPO_LETTURA, MAX_TEMPO_LETTURA);
	}
	
	private void faiAltro() throws InterruptedException {
		attendi(MIN_TEMPO_ALTRO, MAX_TEMPO_ALTRO);
	}
	private void attendi ( int min, int max ) throws InterruptedException {
		TimeUnit.SECONDS.sleep(random.nextInt(max-min+1)+min);
	}

}//Lettore