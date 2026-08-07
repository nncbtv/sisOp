package sisOp.Tracce.Visualizzatore.Codice;

public class Visualizzatore extends Thread {

	private CodaCondivisa stack;
	
	public Visualizzatore(CodaCondivisa c) {
		this.stack = c;
	}//costruttore
	
	public void run() {
		while (true) { //Il thread non si deve fermare alla prima iterazione
			stack.print();
		}
	}//run
	
}//Visualizzatore