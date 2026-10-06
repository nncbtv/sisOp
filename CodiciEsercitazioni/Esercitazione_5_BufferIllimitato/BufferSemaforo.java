package sisOp.CodiciEsercitazioni.Esercitazione_5_BufferIllimitato;

import java.util.concurrent.Semaphore;

public class BufferSemaforo extends Buffer {
	
	private Semaphore ciSonoElementi; //Per garantire che la lettura venga fatta solo se ci sono gli elementi
	private Semaphore mutex = new Semaphore(1); //Per garantire la mutua esclusione

	public BufferSemaforo() {
		ciSonoElementi = new Semaphore(0); //L'array è vuoto, i consumatori vanno bloccati
	}//Costruttore

	@Override
	public void put(int i) throws InterruptedException {
		mutex.acquire();//Attendo il mutex per scrivere sulla cella
		buffer.add((Integer)i); //Scrivo sulla cella
		System.out.println("Sto scrivendo: "+i);
		ciSonoElementi.release(); //Informo i consumatori che ho inserito un dato
		mutex.release(); //Lascio il mutex per far scrivere/leggere ad un altro thread
	}

	@Override
	public int get() throws InterruptedException {
		ciSonoElementi.acquire(); //Cerco un permesso per la lettura, se il buffer è vuoto mi fermo
		mutex.acquire(); //Mi cerco il semaforo per l'accesso in mutua esclusione
		int i = (int)buffer.getFirst(); //Prendo l'elemento appena inserito nella lista
		buffer.removeFirst(); //Rimuovo l'oggetto appena letto
		System.out.println("Sto leggendo: "+i);
		mutex.release();
		return i; //ritorno la lettura
	}

}//BufferSemaforo