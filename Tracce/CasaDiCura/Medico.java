package sisOp.Tracce.CasaDiCura;

public class Medico extends Thread {

	private CasaDiCura c; //La casa di cura su cui lavoro

	public Medico(CasaDiCura cIn) {
		this.c = cIn;
	}//Costruttore

	@Override
	public void run() {
		while ( c.pazientiServiti < c.nPazienti ) { //Continuo a svolgere le mie operazioni finché ci sono pazienti da servire
			c.chiamaEIniziaOperazione(); //Inizio l'operazione
			c.fineOperazione(); //Finisco l'operazione
			c.pazientiServiti += 1; //Incremento il contatore per poter dopo uscire
		}
	}//run

}//Medico