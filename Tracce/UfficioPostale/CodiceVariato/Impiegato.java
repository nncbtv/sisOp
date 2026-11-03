package sisOp.Tracce.UfficioPostale.CodiceVariato;

public class Impiegato extends Thread {

	private final int tipoUfficio;
	private final UfficioPostale ufficio;
	
	public Impiegato(UfficioPostale u, int i) {
		this.tipoUfficio = i;
		this.ufficio = u;
	}//Costruttore
	
	@Override
	public void run() {
		while ( ufficio.isAperto() ) {
			try {
				//INVERTENDO QUESTE OPERAZIONI DIVENTA UN PRODUCER/CONSUMER
				ufficio.eseguiOperazione();
				ufficio.prossimoCliente();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("[ IMPIEGATO ]: "+Thread.currentThread().getName()+" Fine turno");
	}//run
	
	public int getTipoUfficio() {
		return tipoUfficio;
	}//getTipoUfficio
	
}//Impiegato