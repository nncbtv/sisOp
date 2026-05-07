package sisOp.Esercitazione_6_MemoriaCondivisaLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sisOp.Esercitazione_5_MemoriaCondivisa.MemoriaCondivisa;

public class MemoriaCondivisaLock extends MemoriaCondivisa {
	/*
	 * Tanti lettori leggono dalla memoria ma solo uno scrittore alla volta può scriverci
	 */
	
	private int nLettori = 0; //Numero di lettori presenti in memoria
	private boolean scrittoreScrive = false; //Per ciclare la wait sulla condition
	private Lock l = new ReentrantLock();
	private Condition possoScrivere = l.newCondition();
	private Condition possoLeggere = l.newCondition();
	
	@Override
	public void inizioScrittura() throws InterruptedException {
		l.lock();
		try {
			while ( nLettori>0 || scrittoreScrive) { //Aspetto finchè ci sono lettori nella memoria o c'è un altro scrittore
				possoScrivere.await();
			}
			scrittoreScrive = true;
			System.out.println("Lo scrittore: "+Thread.currentThread().getName()+" sta scrivendo");
		}finally {
			l.unlock();
		}
	}

	@Override
	public void fineScrittura() throws InterruptedException {
		l.lock();
		try {
			scrittoreScrive = false;
			possoLeggere.signalAll(); //Segnalo a tutti i lettori che possono leggere
			possoScrivere.signal(); //Sveglio uno scrittore a caso
		}finally {
			l.unlock();
		}
	}

	@Override
	public void inizioLettura() throws InterruptedException {
		l.lock(); //Cerco l'accesso nel monitor
		try {
			while(scrittoreScrive) { //Se stanno scrivendo aspetto su quella condition
				possoLeggere.await();
			}
			nLettori++; //Aggiorno il contatore dopo l'attesa
			System.out.println("Il lettore: "+Thread.currentThread().getName()+" sta leggendo");
		}finally {
			l.unlock();//Lascio il monitor
		}
	}

	@Override
	public void fineLettura() throws InterruptedException {
		l.lock();
		try {
			nLettori--;
			if (nLettori == 0) possoScrivere.signal(); //Mando il segnale nella coda di attesa per scrivere
		}finally {
			l.unlock();
		}
	}

}//MemoriaCondivisaLock
