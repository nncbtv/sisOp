package sisOp.Tracce.BarMod.Codice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
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
	
	private Random r;
	
	public BarModLock() {
		cassa = new ReentrantLock();
		aspettaCassa = cassa.newCondition();
		bancone = new ReentrantLock();
		aspettaBancone = bancone.newCondition();
		
		codaCassa = new LinkedList<>();
		codaBancone = new LinkedList<>();
		
		r = new Random();
	}//costruttore

	@Override
	public int scegli() {
		//Controllo a quale fila conviene mettermi in coda
		if ( codaCassa.size() == 0 ) return 1;
		if ( codaBancone.size() == 0) return 2;
		if ( codaBancone.size() < codaCassa.size() ) return 2;
		return 1;
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
				int pagamento = r.nextInt(5,11);
				TimeUnit.SECONDS.sleep(pagamento); //Simulazione di attesa pagamento
				codaCassa.remove(corrente); //Esco dalla coda
				aspettaCassa.signalAll(); //Segnalo il posto libero
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
				while ( !codaBancone.peek().equals(corrente) ) {
					aspettaBancone.await();
				}
				//Se riesco a passare la condition e uscire dal ciclo while vado a pagare
				System.out.println("Il cliente: "+corrente.getName()+" sta consumando");
				int consumazione = r.nextInt(20,41);
				TimeUnit.SECONDS.sleep(consumazione); //Simulazione di attesa pagamento
				codaBancone.remove(corrente); //Esco dalla coda
				aspettaBancone.signalAll(); //Segnalo il posto libero
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
		if ( i == 1 ) { //Significa che ho pagato ed ora devo consumare
			inizia(2); //Mi sposto al bancone
		}else {
			inizia(1); //Mi sposto alla cassa
		}
	}//finisci

}//BarModLock