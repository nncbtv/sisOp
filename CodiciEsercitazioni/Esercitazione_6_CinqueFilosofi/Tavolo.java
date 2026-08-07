package sisOp.Esercitazione_6_CinqueFilosofi;

import sisOp.Esercitazione_5_CinqueFilosofi.Filosofo;

public abstract class Tavolo extends sisOp.Esercitazione_5_CinqueFilosofi.Tavolo { //Estendo lo scorso tavolo

	private final static int FILOSOFI = 5;
	protected boolean[] bacchette = new boolean[FILOSOFI]; //riga in più al tavolo precedente, libera false, occupata true
	
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