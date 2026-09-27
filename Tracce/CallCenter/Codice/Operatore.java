package sisOp.Tracce.CallCenter.Codice;

public class Operatore extends Thread {

	private int clientiServiti = 1; //Variabile interna per chiedere poi la pausa
	private int nOperatore;
	private CallCenter c;
	public boolean stop = false;
	
	public Operatore(CallCenter vodafone, int nOperatore) {
		this.c = vodafone;
		this.nOperatore = nOperatore;
	}//Costruttore
	
	@Override
	public void run() {
		try {
			do {
				c.fornisciAssistenza(); //Fornisco Assistenza al cliente
				if ( stop ) return;
				clientiServiti +=1; //Incremento il mio contatore dei clienti serviti
				c.prossimoCliente(); //Passa al prossimo cliente
			}while ( c.numeroClienti>0 );
		}catch (Exception e) {
			e.printStackTrace();
		}
	}//run
	
	public int getNoperatore() {
		return nOperatore;
	}//getNoperatore
	
	public int getClientiServiti() {
		return clientiServiti;
	}//getClientiServiti
}//Operatore