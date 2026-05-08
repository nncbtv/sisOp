package sisOp.Esercitazione_6_CinqueFilosofi;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TavoloLock extends Tavolo{
	
	protected Lock l = new ReentrantLock();
	protected Condition[] aspettoBacchetta = new Condition[getFilosofi()];
	
	public TavoloLock() {
		for ( int i=0; i<getFilosofi(); i++) {
			aspettoBacchetta[i] = l.newCondition();
		}
	}//costruttore

	@Override //i e la posizione del filosofo sul tavolo
	public void prendiBacchette(int i) throws InterruptedException {
		l.lock();
		try {
			while ( bacchette[i] == true || bacchette[i+1] == true ) {
				aspettoBacchetta[i].await();
				aspettoBacchetta[i+1].await();
			}
		} finally {
			l.unlock();
		}
	}//prendiBacchette

	@Override
	public void rilasciaBacchette(int i) {
		l.lock();
		try {
			//Libero le condizioni di sleep
			bacchette[i] = false;
			bacchette[i+1] = false;
			//Segnalo chi sta aspettando le bacchette
			aspettoBacchetta[i].signal();
			aspettoBacchetta[i+1].signal();
		} finally {
			l.unlock();
		}
	}//rilasciaBacchette
}//TavoloLock