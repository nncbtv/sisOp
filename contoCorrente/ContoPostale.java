package sisOp.contoCorrente;

public class ContoPostale extends ContoCorrente{

	public ContoPostale(int importo) { //Costruttore
		super(importo); //Rimando alla classe concreta
	}//ContoPostale
	
	@Override
	public void deposita(int importo) {//Metodo di deposito
		if (importo<0) throw new RuntimeException("Impossibile depositare una somma negativa");
		else {
			Saldo = Saldo+importo; //Aggiorno la varibile della classe astratta
			System.out.print("Hai depositato");
		}
	}//deposita
	
	@Override
	public void preleva(int importo) {
		if ( importo>Saldo ) System.out.println("Fondi insufficenti!");
		if (importo<0) throw new RuntimeException("Impossibile prelevare una somma negativa");
		else {
			Saldo = Saldo-importo;
			System.out.println("Hai prelevato");
		}
	}//preleva
	
}//ContoPostale