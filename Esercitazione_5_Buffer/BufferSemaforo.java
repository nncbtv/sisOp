package sisOp.Esercitazione_5_Buffer;

import java.util.concurrent.Semaphore;

public class BufferSemaforo extends Buffer {
	
	private Semaphore ciSonoElementi = new Semaphore(0); //Per garantire che la lettura venga fatta solo se ci sono gli elementi
	private Semaphore ciSonoPostiVuoti;
	private Semaphore mutex = new Semaphore(1); //Per garantire la mutua esclusione

	public BufferSemaforo(int dimensione) {
		super(dimensione);
		ciSonoPostiVuoti = new Semaphore(dimensione); //L'array è vuoto, i produttori hanno a disposizione tutte le celle su cui scrivere
	}//Costruttore

	@Override
	public void put(int i) throws InterruptedException {
		ciSonoPostiVuoti.acquire(); //Prendo un posto vuoto
		mutex.acquire();//Attendo il mutex per scrivere sulla cella
		buffer[in] = i; //Scrivo sulla cella
		System.out.println("Sto scrivendo: "+i);
		in = (in+1)%buffer.length; //Resetto l'indice con l'operazione di modulo, (n+1) perchè devo andare alla prosiima cella
		mutex.release(); //Lascio il mutex per far scrivere ad un altro thread
		ciSonoElementi.release(); //Aggiungo un permesso per la lettura
		
	}

	@Override
	public int get() throws InterruptedException {
		ciSonoElementi.acquire(); //Cerco un permesso per la lettura
		mutex.acquire(); //Mi cerco il semaforo per l'accesso in mutua esclusione
		int i = buffer[out];
		System.out.println("Sto leggendo: "+i);
		out = (out+1)%buffer.length;
		mutex.release();
		ciSonoPostiVuoti.release(); //Rilascio il semaforo per una nuova scrittura sulla cella
		return i; //ritorno la lettura
	}

}//BufferSemaforo