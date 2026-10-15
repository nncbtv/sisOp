package sisOp.Tracce.Muratori;

public abstract class Casa {

	protected int nStratiParete; //Strati da posizionare per finire una parete
	protected int poggiaMattoni; //Numero di muratori che poggeranno i mattoni
	protected int spargiCemento; //Numero di muratori che spargono il cemento
	protected int nPareti; //Numero di pareti da costruire, 4 per questa casa

	public Casa(int n, int pareti, int mattoni, int cemento) {
		this.nStratiParete = n;
		this.nPareti = pareti;
		this.poggiaMattoni = mattoni;
		this.spargiCemento = cemento;
	}//Costruttore

	abstract boolean inizia(int operazione); //Decide se deve essere svolto il lavoro di tipo T oppure no
	abstract void termina(int operazione); //Comunica al capocantiere che ha finito l'operazione

	public void test() throws InterruptedException {
		System.out.println("Per costruire la casa: ");
		System.out.println("Pareti: "+nPareti);
		System.out.println("Strati pareti: "+nStratiParete);
		System.out.println("Muratori per mattoni: "+poggiaMattoni);
		System.out.println("Muratori per cemento: "+spargiCemento+"\n");
		Thread [] muratoriMattoni = new Thread [poggiaMattoni];
		for ( int i = 0; i<poggiaMattoni; i++ ) {
			muratoriMattoni[i] = new Thread(new Muratori(this, 1)); //con 1 indico la stesura dei mattoni
			muratoriMattoni[i].start();
		}//Avvio poggiaMattoni
		Thread [] muratoriCemento = new Thread [spargiCemento];
		for ( int i = 0; i<spargiCemento; i++ ) {
			muratoriCemento[i] = new Thread( new Muratori(this, 0)); //Con 0 indico la stesura del cemento
			muratoriCemento[i].start();
		}//Avvio stendiCemento
		for ( int i = 0; i<poggiaMattoni; i++ ) {
			muratoriMattoni[i].join();
		}//Attendo che i mattonieri rientrino :)
		for ( int i = 0; i<spargiCemento; i++ ) {
			muratoriCemento[i].join();
		}//Attendo che i cementatori rientrino :)
		System.out.println("\nI muratori sono riusciti ad alzare le pareti");
	}//test della classe

}//Casa