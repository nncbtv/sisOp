package sisOp.Tracce.CallCenter.Codice;

import java.util.concurrent.TimeUnit;

public class Operatore extends Thread {

	private int clientiServiti = 0; //Variabile interna per chiedere poi la pausa
	private int nOperatore;
	private CallCenter c;
	public boolean stop = false;
	public int codiceTicket = -1;

	public Operatore(CallCenter vodafone, int nOperatore) {
		this.c = vodafone;
		this.nOperatore = nOperatore;
	}//Costruttore

	@Override
	public void run() {
		try {
			while ( true ) {
				c.fornisciAssistenza(); //Fornisco Assistenza al cliente
				if ( stop ) {
					return; //Se durante la ricerca di clienti non trovo nulla termino
				}
				c.prossimoCliente(); //Passa al prossimo cliente
				clientiServiti +=1; //Incremento il mio contatore dei clienti serviti
				if ( clientiServiti%15 == 0 ) {
					TimeUnit.SECONDS.sleep(5);
					System.out.println(Thread.currentThread().getName()+" ritorna dalla pausa caffe");
				}
			}
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