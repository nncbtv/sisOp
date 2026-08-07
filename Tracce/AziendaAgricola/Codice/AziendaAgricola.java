package sisOp.Tracce.AziendaAgricola.Codice;

public abstract class AziendaAgricola {

	protected int sacchiDisponibili = 20; //Sacchetti disponibili alla vendita
	protected int costoSacco = 3;
	protected int saldoFinale = 0;
	
	abstract void pagaSacchetti ( int nSacchetti ) throws InterruptedException;
	abstract void ritiroSacchetti ( int nSacchetti ) throws InterruptedException;
	
	abstract void refillMagazzino() throws InterruptedException;
	
	void test(int nClienti) throws InterruptedException {
		Magazziniere operaioMagazziniere = new Magazziniere(this);
		Thread magazziniere = new Thread(operaioMagazziniere);
		magazziniere.start(); //Non posso metterlo demone perché tocca variabili sensibili e potrebbe essere ucciso con il lock in mano
		Thread [] clienti = new Thread [nClienti];
		for ( int i = 0; i<nClienti; i++ ) {
			clienti[i] = new Thread(new Cliente(this));
			clienti[i].start();
		}
		for ( int i = 0; i<nClienti; i++ ) {
			clienti[i].join();
		}
		mostraIncasso();
		System.out.println("Tutti i clienti sono stati serviti");
		operaioMagazziniere.chiusura();
		magazziniere.join();
	}//test
	
	void mostraIncasso() {
		System.out.println("L'incasso finale è: "+saldoFinale);
	}//mostraIncasso
	
}//AziendaAgricola