package sisOp.Tracce.Funivia.Codici;

public abstract class Funivia {

	abstract void pilotaStart();
	abstract void pilotaEnd();
	abstract void turistaSali(int tipoTurista);
	abstract void turistaScendi(int tipoTurista);

	protected int pedoni;
	protected int ciclisti;
	protected int personeValle;

	public Funivia(int pedoniIn, int ciclistiIn) {
		this.pedoni = pedoniIn;
		this.ciclisti = ciclistiIn;
		this.personeValle = pedoni+ciclisti;
	}//Funivia

	public void test() {
		try {
			Thread [] turistiBici = new Thread[ciclisti];
			Thread [] turistiPiedi = new Thread[pedoni];
			Pilota tonino = new Pilota ( this );
			tonino.start();
			for ( int i = 0; i<ciclisti; i++ ) {
				turistiBici[i] = new Thread( new Turista(1, this) );
				turistiBici[i].start();
			}

			for ( int i = 0; i<pedoni; i++ ) {
				turistiPiedi[i] = new Thread ( new Turista(0, this) );
				turistiPiedi[i].start();
			}

			for ( int i = 0; i<ciclisti; i++ ) {
				turistiBici[i].join();
			}

			for ( int i = 0; i<pedoni; i++ ) {
				turistiPiedi[i].join();
			}
			tonino.join();
			System.out.println("Le persone sono state tutte portate a monte");

		}catch (Exception e) {
			System.out.println("Errore funivia");
		}

	}//test

}//Funivia