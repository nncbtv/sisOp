package sisOp.CodiciEsercitazioni.contoCorrente;

public abstract class ContoCorrente { //Astraggo l'oggetto ContoCorrente così che ogni correntista abbia il suo

	protected int Saldo;

	public ContoCorrente(int D) {
		if (D<=0) {
			throw new RuntimeException("Il saldo iniziale non può esserre negativo");
		}
		this.Saldo = D;
	}//Costruttore

	//Metodi da ridefinire in modo concreto
	public abstract void preleva(int importo);

	public abstract void deposita(int importo);

	//Ritorno il saldo che ho impostato come deposito inziale
	public int getSaldo() {
		return Saldo;
	}//Saldo

}//ContoCorrente
