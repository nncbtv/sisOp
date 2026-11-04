package sisOp.Tracce.Pizzeria.Codice;

public abstract class Pizzeria {

	protected final int CLIENTI_PIZZERIA;
	
	public Pizzeria(int nClienti) {
		this.CLIENTI_PIZZERIA = nClienti;
	}//Costruttore
	
	abstract boolean isAperto(); //Metodo ausiliario
	abstract void entra() throws InterruptedException;
	abstract void mangiaPizza() throws InterruptedException;
	abstract void preparaPizza() throws InterruptedException;
	abstract void serviPizza() throws InterruptedException;

}//Pizzeria