package sisOp.Tracce.Muratori;

public class Muratori extends Thread {

	private Casa casaDacostruire;
	private int azione;
	private boolean contratto;

	public Muratori(Casa progetto, int lavoro) {
		this.casaDacostruire = progetto;
		this.azione = lavoro;
	}//Costruttore della classe

	@Override
	public void run() {
		do{
			contratto = casaDacostruire.inizia(azione); //Controllo se è necessario posizionare un altro strato oltre a questo
			if (contratto) {
				casaDacostruire.termina(azione); //Svolgo l'azione
			}
		}while(contratto);
		System.out.println(Thread.currentThread().getName()+"Licenziato");
	}//run

}//Muratori