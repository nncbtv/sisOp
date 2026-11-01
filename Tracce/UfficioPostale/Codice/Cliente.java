package sisOp.Tracce.UfficioPostale.Codice;

public class Cliente extends Thread {
	
	private final String tipoOperazione;
	private final UfficioPostale ufficio;
	
	public Cliente(UfficioPostale u, String op) {
		this.ufficio = u;
		this.tipoOperazione =op;
	}//Costruttore
	
	@Override
	public void run() {
		try {
			if ( !ufficio.ritiraTicket(tipoOperazione) ) return;
			ufficio.attendiSportello(tipoOperazione);
		}catch (Exception e) {
			e.printStackTrace();
		}
	}//run
}//Cliente