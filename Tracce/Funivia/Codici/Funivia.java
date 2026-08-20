package sisOp.Tracce.Funivia.Codici;

public abstract class Funivia {
	
	abstract void pilotaStart();
	abstract void pilotaEnd();
	abstract void turistaSali(int tipoTurista);
	abstract void turistaScendi(int tipoTurista);
	
	public void test() {
		try {
			Thread [] turistiBici = new Thread[9];
			Thread [] turistiPiedi = new Thread[18];
			Pilota tonino = new Pilota ( this );
			tonino.start();
			for ( int i = 0; i<9; i++ ) {
				turistiBici[i] = new Thread( new Turista(1, this) );
				turistiBici[i].start();
			}
			
			for ( int i = 0; i<18; i++ ) {
				turistiPiedi[i] = new Thread ( new Turista(0, this) );
				turistiPiedi[i].start();
			}
			
			for ( int i = 0; i<9; i++ ) {
				turistiBici[i].join();
			}
			
			for ( int i = 0; i<18; i++ ) {
				turistiPiedi[i].join();
			}
			
			tonino.interrupt();
			tonino.join();
		
		}catch (Exception e) {
			System.out.println("Errore funivia");
		}
		
	}//test
	
}//Funivia