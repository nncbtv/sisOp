package sisOp.Tracce.Visualizzatore.Codice;

import java.util.Random;

public class Utente extends Thread {

	private CodaCondivisa stack;
	
	public Utente(CodaCondivisa c) {
		this.stack = c;
	}//costruttore
	
	public void run() {
		while (true) { //il Thread non si deve fermare alla prima iterazione
			Random r = new Random();
			stack.insert(r.nextInt(1,6)); //[1, 6)
		}
	}//run
	
}//Utente