package sisOp.CodiciEsercitazioni.Esercitazione_6_3;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class CodaThreadLock extends CodaThread {

	private int turno = 0; //Per capire se è il thread che dobbiamo servire (contatore)
	private Lock l = new ReentrantLock(); //Lock per l'accesso in sezione critica
	private Condition aspetta = l.newCondition(); //Condiziopne su cui attendere se arrivo e non è il mio turno

	public CodaThreadLock(int n) {
		super(n);
	}//Costruttore

	@Override
	public void stampa(int id) throws InterruptedException {
		l.lock(); //Cerco il lucchetto per essere sevito
		try {
			while ( turno != id ) { //Se non è il mio turno lo lascio e aspetto sulla Condition
				aspetta.await();
			}
			//Svolgo le operazioni
			System.out.println("Ciao sono: "+Thread.currentThread().getName());
			turno++; //Informo la sezione critica che mi ha servito e può passare al prossimo
			aspetta.signalAll(); //Sveglio tutti gli altri thread
			//Li sveglio tutti perchè tanto se non è il loro turno si riaddormentano sulla condition
			//Svegliarne uno solo mi porterebbe a starvation
		} finally {
			l.unlock();//rilascio il lock
		}
	}//stampa

}//CodaThreadLock
