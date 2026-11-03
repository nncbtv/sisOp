package sisOp.Tracce.UfficioPostale.Codice;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		final int CLIENTI = 200;
		final int IMPIEGATI = 3;
		final int CODE_OPERAZIONI = 2;
		UfficioPostale u = new UfficioPostaleLC4B(CLIENTI, IMPIEGATI, CODE_OPERAZIONI);
		Thread [] c = new Thread[CLIENTI];
		Thread [] imp = new Thread[IMPIEGATI];
		for ( int i = 0; i<60; i++ ) {
			c[i] = new Cliente(u, "A");
			c[i].start();
		}
		for ( int i = 60; i<110; i++ ) {
			c[i] = new Cliente(u, "B");
			c[i].start();
		}
		for ( int i = 110; i<CLIENTI; i++ ) {
			c[i] = new Cliente(u, "C");
			c[i].start();
		}
		for ( int i = 0; i<IMPIEGATI; i++ ) {
			imp[i] = new Impiegato(u, i);
			imp[i].start();
		}
		for ( int i = 0; i<CLIENTI; i++ ) {
			c[i].join();
		}
		System.out.println("I clienti sono stati tutti serviti");
		for ( int i = 0; i<IMPIEGATI; i++ ) {
			imp[i].join();
		}
		System.out.println("Ufficio Postale Chiuso, tutti i clienti sono stati serviti");
	}//main

}//Main