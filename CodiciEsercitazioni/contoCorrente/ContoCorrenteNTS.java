package sisOp.CodiciEsercitazioni.contoCorrente;

//QUESTA IMPLEMENTAZIONE NON HA MECCANISMI DI SINCRONISMO TRA I THREAD
public class ContoCorrenteNTS extends ContoCorrente{

	public ContoCorrenteNTS(int importo) { //Costruttore
		super(importo); //Rimando alla classe concreta
	}//ContoCorrenteNTS

	@Override
	public void deposita(int importo) {//Metodo di deposito
		if (importo<0) {
			throw new RuntimeException("Impossibile depositare una somma negativa");
		} else {
			Saldo = Saldo+importo; //Aggiorno la varibile della classe astratta
		}
	}//deposita

	@Override
	public void preleva(int importo) {
		if ( importo>Saldo ) {
			System.out.println("Fondi insufficenti!");
		}
		if (importo<0) {
			throw new RuntimeException("Impossibile prelevare una somma negativa");
		} else {
			Saldo = Saldo-importo;
		}
	}//preleva

}//ContoCorrenteNTS