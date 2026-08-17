package sisOp.Tracce.BarMod.Codice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class BarModLock extends BarMod {
	
	//Lock e condizioni su cui far attendere i thread
	private ReentrantLock cassa;
	private Condition aspettaCassa;
	private ReentrantLock bancone;
	private Condition aspettaBancone;
	
	//Code per ogni sezione
	private Queue<Thread> codaCassa;
	private Queue<Thread> codaBancone;
	private int postiBancone = 4; //Mi serve per far passare 4 thread alla volta dalla coda al bancone fino a sedersi
	
	public BarModLock() {
		cassa = new ReentrantLock();
		aspettaCassa = cassa.newCondition();
		bancone = new ReentrantLock();
		aspettaBancone = bancone.newCondition();
		
		codaCassa = new LinkedList<>();
		codaBancone = new LinkedList<>();
	}//costruttore

	@Override
	public int scegli() {
		//Controllo a quale fila conviene mettermi in coda
		if ( codaCassa.size() == 0 ) return 1;
		if ( codaBancone.size() == 0 && postiBancone > 0) return 2;
		if ( codaBancone.size() < codaCassa.size() ) return 2;
		return 1;
		/*
		 * Metodo che fa un piccola stima, ovviemente qui c'é race condition poiche un Thread può aggiungersi in coda mentre controllo,
		 * non è però un problema, sto simulando una stima di due code ad occhio quindi ci sta
		 * */
	}//scegli

	@Override
	public void inizia(int i) {
		//Comincio l'operazione a cui mi sono accodato
		if (i == 1) {//Sono in coda alla cassa
			cassa.lock();
			try {
				//Preso il lock devo mettermi in coda 
				Thread corrente = Thread.currentThread();
				codaCassa.add(corrente); //Mi metto in coda alla cassa
				while ( !codaCassa.peek().equals(corrente) ) {
					aspettaCassa.await();
				}
				//Se riesco a passare la condition e uscire dal ciclo while vado a pagare
				System.out.println("Il cliente: "+corrente.getName()+" sta pagando");
			}catch (Exception e) {
				System.out.println("Errore nella coda cassa");
			}finally {
				cassa.unlock();
			}
		}else { //Sono in coda al bancone
			bancone.lock();
			try {
				//Preso il lock devo mettermi in coda 
				Thread corrente = Thread.currentThread();
				codaBancone.add(corrente); //Mi metto in coda alla cassa
				while ( !codaBancone.peek().equals(corrente) || postiBancone == 0 ) {
					aspettaBancone.await();
				}
				//Qui ci saranno 4 thread in ordine fifo
				postiBancone--; //Tolgo un posto al bancone
				codaBancone.poll(); //Esco dalla coda e il prossimo può controllare se ci sono posti al bancone
				//Se riesco a passare la condition e uscire dal ciclo while vado a consumare
				System.out.println("Il cliente: "+corrente.getName()+" sta consumando");
			}catch (Exception e) {
				System.out.println("Errore nella coda bancone");
			}finally {
				bancone.unlock();
			}
		}
	}//inizia

	@Override
	public void finisci(int i) {
		//Siccome qui devo fare le operazioni inverse per ridurre il codice ripetuto scambio gli indici e richiamo la funzione precedente
		if ( i == 1 ) { //Significa che ho pagato e devo togliermi dalla coda
			cassa.lock();
			try {
				codaCassa.poll(); //Faccio scorrere la coda della cassa
				aspettaCassa.signalAll(); //Segnalo il posto libero
			}finally {
				cassa.unlock();
			}
		}else {
			bancone.lock();
			try {
				postiBancone++; //Libero un posto al bancone
				aspettaBancone.signalAll(); //Segnalo il posto libero
			}finally {
				bancone.unlock();
			}
		}
	}//finisci

}//BarModLock