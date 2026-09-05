package sisOp.Tracce.CasaDiCura;

public abstract class CasaDiCura {
	
	protected int nPazienti;
	protected int pazientiServiti = 0; //Pazienti Serviti dal medico
	
	public CasaDiCura(int numeroPazienti) {
		this.nPazienti = numeroPazienti;
	}//Costruttore

	abstract void chiamaEIniziaOperazione();
	abstract void fineOperazione();
	abstract void pazienteEntra();
	abstract void pazienteEsci();
	
	public void test() throws InterruptedException {
		Thread [] pazienti = new Thread[nPazienti]; //Creo l'insieme dei pazienti
		Medico gianni = new Medico(this); //Creo il medico della clinica
		gianni.start(); //Avvio il medico per gestire tutti i pazienti
		for (int i = 0; i<nPazienti; i++) {
			pazienti[i] = new Thread(new Paziente(this));
			pazienti[i].start(); //Avvio i pazienti per cominciare a metterli in coda
		}//Inizializzazione e avvio pazienti
		for (int i = 0; i<nPazienti; i++) {
			pazienti[i].join(); //Attendo che finiscano i pazienti
		}
		gianni.join();
		System.out.println("I pazienti sono stati visitati tutti!");
	}//test
	
}//CasaDiCura