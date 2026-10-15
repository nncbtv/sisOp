package sisOp.CodiciEsercitazioni.contoCorrente;

import java.util.Random;

public class Correntista implements Runnable{

	//Variabili che mi serviranno poi per far attendere randomicamente il Thread
	private static final int MIN_ATTESA = 1;
	private static final int MAX_ATTESA = 3;
	private Random random = new Random();

	private ContoCorrente cc; //ContoCorrente su cui fare le operazioni
	private int importo; //Importo
	private int operazioni; //Numero di operazioni da fare

	public Correntista(ContoCorrente ccIn, int importoIn, int operazioniIn) {
		if (operazioniIn%2 != 0) {
			throw new RuntimeException("Le operazioni devono essere pari");
		}
		if (importoIn <= 0) {
			throw new RuntimeException("L'importo deve essere maggiore di zero");
		}
		if (ccIn == null) {
			throw new RuntimeException("Inserire un conto corrente");
		}
		this.cc = ccIn;
		this.importo = importoIn;
		this.operazioni = operazioniIn;
	}//Costruttore

	@Override
	public void run() {//Quello che il correntista andrà a fare
		try {
			for ( int i = 0; i<operazioni; i++ ) {//Comincio a svolgere le mie operazioni
				attesaCasuale(); //Attendo un attimo casuale
				if ( i%2 == 0) { //Se l'indice dell'operazione è pari deposito altrimenti prelevo
					cc.deposita(importo);
				}else {
					cc.preleva(importo);
				}
			}
		}
		catch(Exception e){
			System.out.println("Errore nel correntista"+Thread.currentThread().threadId());
		}
	}

	private void attesaCasuale() throws InterruptedException {//Attesa casuale nel range max e min attesa incluso
		Thread.sleep((random.nextInt(MAX_ATTESA-MIN_ATTESA+1)+MIN_ATTESA));
	}
}//Correntista