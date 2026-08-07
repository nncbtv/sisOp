package sisOp.Esercitazione_6_ContoCorrenteLock;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sisOp.contoCorrente.ContoCorrente;

public class ContoCorrenteLock extends ContoCorrente{

	private Lock l = new ReentrantLock();
	
	public ContoCorrenteLock(int D) {
		super(D);
	}//Costruttore

	@Override
	public void preleva(int importo) {
		l.lock(); //Prendo il lucchetto per avere la mutua esclusione
		try {//Entro in sezione critica
			Saldo = Saldo-importo; 
		}finally {
			l.unlock(); //Rilascio il lucchetto
		}
	}//preleva

	@Override
	public void deposita(int importo) {
		l.lock(); //Prendo il lucchetto per avere la mutua esclusione
		try {//Entro in sezione critica
			Saldo = Saldo+importo; 
		}finally {
			l.unlock(); //Rilascio il lucchetto
		}
	}//deposita
}//ContoCorrenteLock