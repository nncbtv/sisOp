package sisOp.Tracce.CockTailBar.Codice;

import java.util.Random;

public class Cliente extends Thread {

	private Bar barItalia; //Oggetto condiviso tra tutti i clienti su cui operare, il bar
	private Random r = new Random(); //Per generare un numero randomico, il cocktail

	public Cliente(Bar in) {
		this.barItalia = in;
	}

	@Override
	public void run() {
		int tipoCocktail = r.nextInt(0,2); //[0,2) 0 il normale, 1 lo speciale
		barItalia.ordinaCocktail(tipoCocktail); //ordino il cocktail al bar
		barItalia.beviEpaga(tipoCocktail); //pago la bevuta e vado via
	}//run

}//Cliente