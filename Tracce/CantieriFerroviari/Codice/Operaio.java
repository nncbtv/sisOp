package sisOp.Tracce.CantieriFerroviari.Codice;

import java.util.concurrent.TimeUnit;

public class Operaio extends Thread {

	int tipoOperaio; //0 traverse, 1 rotaie
	CantiereFerroviarioA cantiere;
	public boolean turnoLavoro = true; //Continuo a lavorare finché non abbiamo costruito tutti i binari

	public Operaio(int tipo, CantiereFerroviarioA c) {
		this.tipoOperaio = tipo;
		this.cantiere = c;
	}//Costruttore

	@Override
	public void run() {
		try {
			while (turnoLavoro) {
				//In base al tipo prima di tutto devo cominciare a preparere il materiale
				if ( tipoOperaio == 0 ) {
					TimeUnit.SECONDS.sleep(20); //Tempo traverse
				}else {
					TimeUnit.SECONDS.sleep(30); //Tempo rotaie
				}
				//Dopodiché inizio il lavoro
				cantiere.lavora(tipoOperaio);
				//Avviso che ho finito di lavorare e devo avvisare l'altro tipo di operai
				cantiere.termina(tipoOperaio);
				//Devo riposarmi dopo un lungo lavoro prima di ricominciare
				TimeUnit.SECONDS.sleep(10);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}//run
}//Operaio