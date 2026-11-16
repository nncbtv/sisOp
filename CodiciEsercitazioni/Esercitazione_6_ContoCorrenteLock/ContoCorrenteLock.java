package sisOp.CodiciEsercitazioni.Esercitazione_6_ContoCorrenteLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class ContoCorrenteLock extends sisOp.CodiciEsercitazioni.contoCorrente.ContoCorrente{

	private Lock l = new ReentrantLock();

	public ContoCorrenteLock(int D) {
		super(D);
	}//Costruttore

	@Override
	public void preleva(int importo) {
		l.lock(); //Prendo il lucchetto per avere la mutua esclusione
		try {//Entro in sezione critica
			super.Saldo = super.Saldo-importo;
		}finally {
			l.unlock(); //Rilascio il lucchetto
		}
	}//preleva

	@Override
	public void deposita(int importo) {
		l.lock(); //Prendo il lucchetto per avere la mutua esclusione
		try {//Entro in sezione critica
			super.Saldo = super.Saldo+importo;
		}finally {
			l.unlock(); //Rilascio il lucchetto
		}
	}//deposita
}//ContoCorrenteLock