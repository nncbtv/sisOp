package sisOp.CodiciEsercitazioni.Esercitazione_6_BufferLock;

import java.util.LinkedList;

public class BufferLockFIFO extends BufferLock{ //Eredito dalla classe non FIFO

	/*
	 * Provo il risveglio dei threads con lock è condition in maniera FIFO
	 */
	//Liste in cui addormento i thread
	protected LinkedList<Thread> codaProduttori = new LinkedList<>();
	protected LinkedList<Thread> codaConsumatori = new LinkedList<>();

	public BufferLockFIFO(int dimensione) {
		super(dimensione);

	}//Costruttore

	@Override
	public void put(int i) throws InterruptedException {
		l.lock(); //Lucchetto ereditato dalla classe non FIFO
		try {
			codaProduttori.add(Thread.currentThread());//Mi metto in coda
			while ( !possoInserire() ) {
				bufferPieno.await(); //Condition ereditata su cui dormo
			}
			codaProduttori.remove(); //Esco dalla coda
			buffer[super.in] = i; //In è l'indice che eredito dalla classe astratta di cui sono figlio
			System.out.println("Sto scrivendo: "+i);
			super.in = (super.in+1)%buffer.length; //Modulo per non andare fuori indice
			nElementi++; //Segnalo di aver aggiunto un elemento al contatore
			bufferVuoto.signalAll(); //Sveglio tutti i threads
		} finally {
			l.unlock();//Rilascio il lucchetto
		}
	}//Put

	private boolean possoInserire() {
		//Controllo se posso mettere ancora elementi senza sforare e se sono il primo thread in coda
		return (nElementi < buffer.length) && ( Thread.currentThread().equals( codaProduttori.getFirst() ) );
	}//possoInserire

	@Override
	public int get() throws InterruptedException {
		int i;
		l.lock();
		try {
			codaConsumatori.add(Thread.currentThread());//Mi metto in coda
			while ( !possoTogliere() ) {
				bufferVuoto.await(); //Condition ereditata su cui dormo
			}
			codaConsumatori.remove(); //Esco dalla coda
			i = buffer[super.out]; //In è l'indice che eredito dalla classe astratta di cui sono figlio
			System.out.println("Sto leggendo: "+i);
			super.out = (super.out+1)%buffer.length; //Modulo per non andare fuori indice
			nElementi--; //Segnalo di aver aggiunto un elemento al contatore
			bufferPieno.signalAll(); //Sveglio tutti i threads
		} finally {
			l.unlock();
		}
		return i;
	}//get

	private boolean possoTogliere() {
		//Controllo se posso mettere ancora elementi senza sforare e se sono il primo thread in coda
		return ( nElementi > 0) && ( Thread.currentThread().equals( codaConsumatori.getFirst() ) );
	}//possoInserire

}//BufferLockFIFO
