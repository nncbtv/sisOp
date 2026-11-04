package sisOp.Tracce.Pizzeria.Codice;

public class Pizzaiolo extends Thread{

	private final Pizzeria p;
	
	public Pizzaiolo(Pizzeria vecchiTempi) {
		this.p = vecchiTempi;
	}//costruttore
	
	@Override
	public void run() {
		while ( p.isAperto() ) {
			try {
				p.preparaPizza();
				p.serviPizza();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}//run
}//Pizzaiolo