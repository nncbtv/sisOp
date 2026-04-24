package sisOp.contoCorrente;

public abstract class ContoCorrente { //Astraggo l'oggetto ContoCorrente così che ogni correntista abbia il suo

	protected int Saldo;
	
	public ContoCorrente(int D) {
		if (D<=0) throw new RuntimeException("Il saldo iniziane non può esserre negativo");
		this.Saldo = D;
	}//Costruttore
	
	public abstract void preleva(int importo);
	
	public abstract void deposita(int importo);
	
	public int getSaldo() {
		return Saldo;
	}//Saldo
	
}//ContoCorrente
