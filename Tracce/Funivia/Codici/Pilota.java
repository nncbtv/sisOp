package sisOp.Tracce.Funivia.Codici;

import java.util.concurrent.TimeUnit;

public class Pilota extends Thread{

	private Funivia f;

	public Pilota(Funivia funivia) {
		this.f = funivia;
	}

	@Override
	public void run() {
		try {
			while( f.personeValle > 0 ) {
				f.pilotaStart(); //Scendo a valle e faccio salire i passeggeri
				TimeUnit.SECONDS.sleep(5); //Simulo la risalita
				f.pilotaEnd();
				TimeUnit.SECONDS.sleep(2); //Simulo la discesa
			}
		}catch (InterruptedException e) {
			System.out.println("Il pilota è stato interrotto");
		}
	}//run

}//Pilota