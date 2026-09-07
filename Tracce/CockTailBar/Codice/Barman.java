package sisOp.Tracce.CockTailBar.Codice;

public class Barman implements Runnable {

	private Bar barItalia; //Oggetto condiviso su cui operare, il bar
	
	public Barman(Bar inputBar) {
		this.barItalia = inputBar;
	}//costruttore
	
	@Override
	public void run() {
		while (barItalia.hasMoreClient()) { //Finché ci sono clienti da servire
			barItalia.preparaCocktail(); //Preparo i cocktail
		}
		barItalia.chiudiBar(); //Tiro giù la serranda del bar prima di andare via
	}//run

}//Barman