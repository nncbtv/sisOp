package sisOp.Esercitazione_5_CinqueFilosofi;

public abstract class Tavolo {
	private final static int FILOSOFI = 5;
	public abstract void prendiBacchette(int i) throws InterruptedException;
	public abstract void rilasciaBacchette(int i);
	public void start() {
		for (int i = 0; i<FILOSOFI; i++) {
			new Thread(new Filosofo(this, i)).start();
			//con i passo la posizione del tavolo in cui è seduto
		}
	}
	public int getFilosofi() { return FILOSOFI;}
}//Tavolo