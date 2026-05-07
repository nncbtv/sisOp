package sisOp.Esercitazione_5_BarbiereAddormentato;

import java.util.concurrent.TimeUnit;

public class Barbiere extends Thread{//Estendo Thread perché ho bisogno di essere un thread daemon
	//Chiedo e tengo riferimento su quale sala devo lavorare
	private Sala salone;
	public Barbiere( Sala s) {
		salone = s;
	}//Costruttore
	
	@Override
	public void run() {
		while(true) {
			try {
				salone.tagliaCapelli(); //Mi attivo per tagliare i capelli
				System.out.println("Taglio in corso...");
				TimeUnit.SECONDS.sleep(3);
			}catch (Exception e) {
				System.out.println("Errore nel barbiere");
			}
		}
	}
}//Barbiere