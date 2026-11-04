package sisOp.Tracce.Pizzeria.Codice;

public class Cliente extends Thread{

	private final Pizzeria p;
	
	public Cliente(Pizzeria vecchiTempi) {
		this.p = vecchiTempi;
	}//costruttore

	@Override
	public void run() {
		try {
			p.entra();
			p.mangiaPizza();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//run
	
}//Cliente