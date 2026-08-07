package sisOp.Tracce.Casello.Codice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CaselloAutostradaleLock extends CaselloAutostradale {
	
	private ReentrantLock[] porteCasello = new ReentrantLock[super.nPorte]; //Un lock per ogni porta del casello
	private Condition [] c = new Condition[super.nPorte]; //Una condizione su cui attendere per ogni porta
	private Queue<Thread>[] code = new Queue[super.nPorte]; //Per gestire i thread in ordine FIFO
	private boolean [] occupata = new boolean[super.nPorte]; //Se la porta è occupata mi fermo
	private ReentrantLock mutex = new ReentrantLock(); //Per gestire la mutua esclusione sulla variabile incasso
	
	public CaselloAutostradaleLock() {
		for ( int i = 0; i<super.nPorte; i++ ) {
			porteCasello[i] = new ReentrantLock();
			c[i] = porteCasello[i].newCondition();
			code[i] = new LinkedList<>();
			occupata[i] = false;
		}
	}//costruttore e inizializzazione
	
	@Override
	public void accodamento(int p) {
		porteCasello[p].lock(); //Arrivo alla porta e voglio mettermi in coda
		try {
			//Prendo il thread corrente e lo aggiungo alla coda della porta
			//Questo lo farà ogni thread che arriva, poi dopo in autonomia controlleranno con la coda se sono i primi arrivati
			Thread corrente = Thread.currentThread();
			code[p].add(corrente);
			//Controllo da questo punto in poi se sono il primo
			//Provo a prenderne uno dalla coda e controllo se sono il primo
			while ( code[p].peek() != corrente || occupata[p] ) { //Finche non sono il primo in coda o la porta è occupata aspetto
				c[p].await();
			}
			System.out.println(Thread.currentThread().getName()+" sta entrando nella porta "+p);
			occupata[p] = true; //Occupo la porta dato che sono il primo serve a me
			code[p].remove(); //Se sono quello corretto al primo posto della coda vado avanti
		}catch (Exception e) {
			System.out.println("Errore nell'accodamento");
		}finally {
			porteCasello[p].unlock(); //Libero il lucchetto della mia porta
		}
	}//accodamento

	@Override
	public void pagaPedaggio(double km, int p) {
		try {
			//Simulo il tempo di pagamento
			Random r = new Random();
			TimeUnit.SECONDS.sleep(r.nextInt(3, 7));
			mutex.lock(); //Mutua esclusione per modificare il parametro condiviso
			try {
				super.incasso = super.incasso + (km*super.tariffa);
			}finally {
				//rilascio
				mutex.unlock();
			}
			//ora mi server il lock della porta su cui sto pagando per segnalare che la porta non è più iumpegnata e sveglaire gli altri
			porteCasello[p].lock();
			try {
				occupata[p] = false;
				c[p].signalAll();
			}finally {
				porteCasello[p].unlock();
			}
		}catch (Exception e) {
			System.out.println("Errore nel pagamento pedaggio");
		}
	}//pagaPedaggio

}//CaselloAutostradaleLock
