package sisOp.Tracce.CasaDiCura;

public class Paziente implements Runnable {

	private CasaDiCura c; //Casa di cura a cui mi presento

	public Paziente(CasaDiCura cIn) {
		this.c = cIn;
	}//Costruttore

	@Override
	public void run() {
		c.pazienteEntra(); //Cerco di entrare
		c.pazienteEsci(); //Cerco di uscire
	}//run

}//Paziente