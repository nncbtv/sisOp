package sisOp.CodiciEsercitazioni.Esercitazione_5_MemoriaCondivisa;

import java.util.concurrent.Semaphore;

public class MemoriaCondivisaSem extends MemoriaCondivisa {
	private int nLettori = 0; //Numero di lettori in memoria
	//La memoria deve essere scritta in modo eslusivo, la lettura può essere fatta in maniera concorrente
	private Semaphore lettura = new Semaphore(1); //Mettendo true si risvegliano con politica FIFO
	private Semaphore memoria = new Semaphore(1); //Rappresenta il Flag libero/occupato per la scrittura

	@Override
	public void inizioScrittura() throws InterruptedException {
		memoria.acquire(); //Ottengo la mutua esclusione per la scrittura
		System.out.println("Scrittura sulla memoria da: "+Thread.currentThread().getName());
	}

	@Override
	public void fineScrittura() throws InterruptedException {
		System.out.println("Fine Scrittura sulla memoria da: "+Thread.currentThread().getName());
		memoria.release(); //Rilascio i permessi per scrivere
	}

	@Override
	public void inizioLettura() throws InterruptedException {
		lettura.acquire(); //Il semaforo di lettura mi serve per proteggere il contatore nLettori
		if (nLettori == 0) {
			memoria.acquire(); //Se non ci sono lettori, io sono il primo ed escludo gli scrittori
		}
		nLettori++; //Incremento in maniera eslusiva il contatore
		System.out.println("Inizio lettura da: "+Thread.currentThread().getName());
		lettura.release(); //Rilascio il posto ad un altro thread che vuole scrivere
	}

	@Override
	public void fineLettura() throws InterruptedException {
		lettura.acquire(); //Riprendo la mutua esclusione per modificare il contatore
		nLettori--;
		if (nLettori == 0) {
			memoria.release(); //Se sono l'ultimo lettore ad andare via, la memoria può essere scritta
		}
		System.out.println("Fine lettura da: "+Thread.currentThread().getName());
		lettura.release(); //Rilascio il metodo ad un altro thread
	}

}
