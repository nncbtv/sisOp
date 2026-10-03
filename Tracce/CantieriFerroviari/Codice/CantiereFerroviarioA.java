package sisOp.Tracce.CantieriFerroviari.Codice;

public abstract class CantiereFerroviarioA {
	
	protected final int binariInCostruzione;
	protected final int N_TRAVERSE;
	
	public CantiereFerroviarioA(int b, int lb) {
		this.binariInCostruzione = b;
		this.N_TRAVERSE = lb;
	}//costruttore
	
	abstract void lavora(int t);
	abstract void termina(int t);

}//CantiereFerroviarioA