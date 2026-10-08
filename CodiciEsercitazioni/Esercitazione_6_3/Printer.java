package sisOp.CodiciEsercitazioni.Esercitazione_6_3;

public class Printer implements Runnable{
	
	private int id;
	private CodaThread coda;
	
	
	public Printer(CodaThread c, int i) {
		this.coda = c;
		this.id = i;
	}//Printer
	
	@Override
	public void run() {
		try {
			coda.stampa(id);
		} catch (InterruptedException e) {
			System.out.println("Errore nel thread: "+Thread.currentThread().getName());
		}
	}//run
	
}//Printer