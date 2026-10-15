package sisOp.Tracce.CallCenter.Codice;

public class Cliente extends Thread {

	private CallCenter c;
	private int codiceTicket;
	public Cliente(CallCenter x, int codiceCliente) {
		this.c = x;
		this.codiceTicket = codiceCliente;
	}//Costruttore

	@Override
	public void run() {
		c.richiediAssistenza();
		c.terminaChiamata();
	}//run

	public int getCodiceCliente() {
		return codiceTicket;
	}//getCodiceTicket

}//Cliente