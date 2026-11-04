package sisOp.Tracce.Pizzeria.Codice;

public class Main {
	
	public static void main(String[] args) throws InterruptedException {
		final int nClienti = 10;
		final int nMaxiTavoli = 2;
		Thread [] cliente = new Thread[nClienti];
		final Pizzeria vecchiTempi = new PizzeriaLC4B(nClienti, nMaxiTavoli);
		for ( int i = 0; i<nClienti; i++ ) {
			cliente[i] = new Cliente(vecchiTempi);
			cliente[i].start();
		}
		Pizzaiolo matteo = new Pizzaiolo(vecchiTempi);
		matteo.start();
		for ( int i = 0; i<nClienti; i++ ) {
			cliente[i].join();
		}
		System.out.println("Tutti i clienti sono stati serviti");
		matteo.join();
		System.out.print("Il pizzaiolo ha chiuso il locale");
	}//main

}//Main