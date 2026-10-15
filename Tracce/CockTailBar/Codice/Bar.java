package sisOp.Tracce.CockTailBar.Codice;

public abstract class Bar {

	protected int nClienti; //Clienti da servire al bar
	protected int incassoFinale = 0; //Incasso del bar, da stampare a chiusura
	protected int clientiServiti = 0;

	public Bar(int numeroClienti) {
		this.nClienti = numeroClienti;
	}//costruttore

	//Metodi da implementare con la sincronizzazione, classe concreta Semaphore/Lock
	abstract void ordinaCocktail(int tipo);
	abstract void preparaCocktail();
	abstract void beviEpaga(int tipo);
	abstract void chiudiBar();

	public boolean hasMoreClient() {
		return nClienti != clientiServiti;
	}//hasMoreClient

	public void test() throws InterruptedException {
		Thread barista = new Thread ( new Barman(this) ); //Creo il barista
		Thread [] clienti = new Thread [nClienti]; //Array di clienti da servire
		for ( int i = 0; i<nClienti; i++ ) {
			clienti[i] = new Cliente(this); //Inizializzo la classe da lanciare come thread
			clienti[i].start(); //Avvio il thread
		}
		barista.start(); //Avvio il barista, dopo i clienti perché si deve per forza presentare qualcuno prima di poter cominciare a fare i cocktail
		for ( int i = 0; i<nClienti; i++ ) {
			clienti[i].join(); //Aspetto che tutti i thread abbiano finito
		}
		barista.join(); //Devo attendere che il barista chiuda il locale
		System.out.println("Il bar è chiuso, l'antifurto è acceso");
	}//test

}//Bar