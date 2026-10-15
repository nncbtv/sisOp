package sisOp.Tracce.Visualizzatore.Codice;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CodaCondivisaLock extends CodaCondivisa {

	private int slotliberi; //Slot in cui posso scrivere la sequenza di stringhe
	private ReentrantLock l; //Lucchetto per la mutua esclusione
	private Condition attendiSlot; //Condizione su cui far attendere i thread

	public CodaCondivisaLock() {
		this.slotliberi = q.size(); //Prendo la capienza rimanente della coda
		this.l = new ReentrantLock(); //Inizializzo il lucchetto
		this.attendiSlot = l.newCondition(); //Aggancio la condizione al lucchetto
	}//costruttore

	@Override
	void insert(int x) {
		l.lock();
		try {
			while ( x > slotliberi ) {
				attendiSlot.await();
			}
			for (int i =0; i<x; i++) {
				q.add(Thread.currentThread().getName()+" ho inserito la stringa "+(i+1));
			}
			slotliberi = slotliberi-x; //Aggiorno con i nuovi slot occupati
		} catch (InterruptedException e) {
			System.out.println("Errore nell'utente");
		}finally {
			l.unlock();
		}
	}//Insert

	@Override
	void print()  {
		l.lock();
		try {
			//Siccome è una blockingQueue mi blocca se la coda è vuota
			System.out.println("Visualizzatore: "+q.peek());
			slotliberi++; //Aggiorno gli slot liberi dopo aver letto un dato
			attendiSlot.signalAll(); //Li sveglio tutti, tanto passa solo chi non eccede con gli slot
		} finally {
			l.unlock();
		}
	}//Print

}//CodaCondivisaLock