package sisOp.Tracce.BarMod.Codice;

public class ClientiBar implements Runnable {
	
	BarMod baretto;
	
	public ClientiBar(BarMod bar) {
		this.baretto = bar; //Prendo il bar che mi passano per poi usarlo
	}//Costruttore

	@Override
	public void run() {
		try {
			int scelta = baretto.scegli(); //Mi presento al bar e scelgo cosa fare
			baretto.inizia(scelta); //Faccio la coda
			baretto.finisci(scelta); //Eseguo l'operazione
		}catch (Exception e) {
			System.out.println("Il cliente non è riuscito ad interagire con il bar"+e.toString());
		}
	}//run

}//ClientiBar