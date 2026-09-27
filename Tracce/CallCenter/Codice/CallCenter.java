package sisOp.Tracce.CallCenter.Codice;

public abstract class CallCenter {
	
	protected int numeroOperatori; //Numero di operatori sul centralino
	protected int numeroClienti;
	
	public CallCenter(int N, int M) {
		this.numeroOperatori = N;
		this.numeroClienti = M;
	}//costruttore

	abstract void richiediAssistenza();
	abstract void fornisciAssistenza();
	abstract void terminaChiamata();
	abstract void prossimoCliente();
	
}//CallCenter