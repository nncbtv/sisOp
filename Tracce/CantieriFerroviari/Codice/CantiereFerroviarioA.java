package sisOp.Tracce.CantieriFerroviari.Codice;

public abstract class CantiereFerroviarioA {
	
	protected final int binariInCostruzione;
	protected final int N_TRAVERSE;
	protected final int N_OPERAI_TRAVERSE;
	protected final int N_OPERAI_ROTAIE;
	
	public CantiereFerroviarioA(int b, int lb, int operaiT, int OperaiR) {
		this.binariInCostruzione = b;
		this.N_TRAVERSE = lb;
		this.N_OPERAI_TRAVERSE = operaiT;
		this.N_OPERAI_ROTAIE = OperaiR;
	}//costruttore
	
	abstract void lavora(int t);
	abstract void termina(int t);

}//CantiereFerroviarioA