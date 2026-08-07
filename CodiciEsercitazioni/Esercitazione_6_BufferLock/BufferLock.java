package sisOp.Esercitazione_6_BufferLock;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sisOp.Esercitazione_5_Buffer.Buffer;

public class BufferLock extends Buffer {

	//Aggiungendo ReentrantLock(true) prendo sempre quello più anziano in attesa
	protected Lock l = new ReentrantLock(true); //Lock da acquisire per la sezione critica
	//Condition su cui attendere
	protected Condition bufferPieno = l.newCondition();
	protected Condition bufferVuoto = l.newCondition();
	//Contatore degli elementi nel buffer
	protected int nElementi = 0;
	
	
	public BufferLock(int dimensione) {
		super(dimensione);
	}//Costruttore

	@Override
	public void put(int i) throws InterruptedException {
		l.lock(); //Cerco la mutua esclusione in sezione critica
		try {//Provo ad eseguire
			while ( nElementi == buffer.length) {//Controllo se è verigficata la condition
				bufferPieno.await(); //Se il buffer è pieno mi addormento sulla condition
			}
			buffer[in] = i; //In è l'indice che eredito dalla classe astratta di cui sono figlio
			System.out.println("Sto scrivendo: "+i);
			in = (in+1)%buffer.length; //Modulo per non andare fuori indice
			nElementi++; //Segnalo di aver aggiunto un elemento al contatore
			bufferVuoto.signal(); //Sveglio un thread che vuole leggere
		} finally {
			l.unlock(); //Rilascio il lucchetto
		}
		
	}//put

	@Override
	public int get() throws InterruptedException {
		int i; //Variabile da ritornare
		l.lock();
		try {
			while ( nElementi == 0) { 
				//Se non ci sono elementi nel buffer mi addormento sulla condition
				bufferVuoto.await();
			}
			i = buffer[out];
			System.out.println("Sto leggendo: "+i);
			out = (out+1)%buffer.length;
			nElementi--; //Perché ne ho letto uno
			bufferPieno.signal(); //Risveglio un thread che deve scrivere
		} finally {
			l.unlock();
		}
		return i;
	}//get

}//BufferLock