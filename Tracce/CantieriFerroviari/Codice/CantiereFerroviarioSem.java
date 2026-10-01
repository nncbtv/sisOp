package sisOp.Tracce.CantieriFerroviari.Codice;

import java.util.concurrent.Semaphore;

public class CantiereFerroviarioSem extends CantiereFerroviarioA {
	
	//Modello il binario con un semaforo
	Semaphore [] binario = new Semaphore[super.binariInCostruzione];
	//Ogni semaforo verrà costruito in questo modo: traversa,rotaia in modo alternato
	boolean [] serveTraversa = new boolean[super.binariInCostruzione];

	public CantiereFerroviarioSem(int binariDaCostruire) {
		super(binariDaCostruire);
		for ( int i = 0; i<super.binariInCostruzione; i++ ) {
			binario[i] = new Semaphore(1);
			serveTraversa[i] = true;
		}
	}//costruttore
	
	@Override
	void lavora(int t) {
		// TODO Auto-generated method stub

	}//lavora

	@Override
	void termina(int t) {
		// TODO Auto-generated method stub

	}//termina

}//cantiereFerroviarioSem