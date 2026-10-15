package sisOp.Tracce.BarMod.Codice;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class ClientiBar implements Runnable {

	BarMod baretto;
	Random r;

	public ClientiBar(BarMod bar) {
		this.baretto = bar; //Prendo il bar che mi passano per poi usarlo
		this.r = new Random();
	}//Costruttore

	@Override
	public void run() {
		try {
			int primaCoda = baretto.scegli(); //Mi presento al bar e scelgo la coda
			baretto.inizia(primaCoda); //Faccio la coda per l'operazione
			eseguiOperazione(primaCoda); //Simulo l'operazione
			baretto.finisci(primaCoda); //Eseguo l'operazione
			int secondaCoda = (primaCoda == 1) ? 2:1; //If implicito per fare la seconda coda, se ho pagato consumo oppure viceversa
			baretto.inizia(secondaCoda);
			eseguiOperazione(secondaCoda);
			baretto.finisci(secondaCoda);
		}catch (Exception e) {
			System.out.println("Il cliente non è riuscito ad interagire con il bar"+e.toString());
		}
	}//run

	private void eseguiOperazione(int operazione) {
		if ( operazione == 1 ) {
			int pagamento = r.nextInt(5,11); //Simulo il pagamento con [5,11) secondi di sleep
			try {
				TimeUnit.SECONDS.sleep(pagamento);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}else {
			int consumazione = r.nextInt(20, 41); //Simulo la consumazione con [20, 41) secondi di sleep
			try {
				TimeUnit.SECONDS.sleep(consumazione);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}//eseguiOperazione

}//ClientiBar