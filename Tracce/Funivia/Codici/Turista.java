package sisOp.Tracce.Funivia.Codici;

public class Turista extends Thread{

	private int tipoTurista;
	private Funivia f;

	public Turista(int t, Funivia fun) {
		this.f = fun;
		this.tipoTurista = t;
	}//Turista

	@Override
	public void run() {
		try {
			f.turistaSali(tipoTurista);
			f.turistaScendi(tipoTurista);
		}catch (Exception e) {
			System.out.println("Errore nel turista");
		}
	}

	public int getTipoTurista() {
		return tipoTurista;
	}//getTipoTurista

	@Override
	public String toString() {
		if ( tipoTurista == 0 ) {
			return "Turista a piedi";
		}
		return "Turista in bici";
	}//tipoTurista
}//Turista