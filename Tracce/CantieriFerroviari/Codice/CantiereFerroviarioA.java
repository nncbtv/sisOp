package sisOp.Tracce.CantieriFerroviari.Codice;

public abstract class CantiereFerroviarioA {
	
	protected int binariInCostruzione;
	
	public CantiereFerroviarioA(int b) {
		this.binariInCostruzione = b;
	}//costruttore
	
	abstract void lavora(int t);
	abstract void termina(int t);

}//CantiereFerroviarioA