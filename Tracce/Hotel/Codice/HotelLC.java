package sisOp.Tracce.Hotel.Codice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class HotelLC extends Hotel {
	
	private final int N_CODE = 3;
	private ReentrantLock l = new ReentrantLock();
	@SuppressWarnings("unchecked")
	private Queue<Thread> [] codePrioritarie = new Queue[N_CODE]; //Array di code, tutte linked list
	private Condition attendiClienti = l.newCondition();
	private Condition attendiReceptionist = l.newCondition();
	private Map <Thread, Boolean> stanzaAssegnata = new HashMap<>();
	private Map <Thread, Thread> clienteControllato = new HashMap<>();
	//private Map <Thread, Integer> codaServita = new HashMap<>(); //Non serve più per la variante 4b
	private Random r = new Random();
	private int camereOccupate = 0;
	private int clientiServiti = 0;
	
	public HotelLC(int camere, int clienti) {
		super(camere, clienti);
		for ( int i = 0; i<N_CODE; i++ ) {
			codePrioritarie[i] = new LinkedList<Thread>();
		}
	}//Costruttore

	boolean codeVuote() {
		boolean vuote = true; //Presumo siano vuote
		for ( int i = 0; i<N_CODE; i++ ) { //Scorro le code per controllarle
			if ( !codePrioritarie[i].isEmpty() ) { //Mi basta una coda non vuota per dire che non sono vuote
				return !vuote; //Nego la mia presunzione e la rimando in output del metodo
			}
		}
		return vuote; //La mia presunzione era vera, ho controllato tutte le code e non ne ho trovato una non vuota
	}//codeVuote
	
	@Override
	boolean isAperto() {
		return clientiServiti < super.N_CLIENTI; //Devo tenere aperto finchè non servo tutti i clienti
	}//isAperto

	@Override
	void mettiInCoda(int prenotazione) throws InterruptedException {
		//Qui arrivano tutti i thread e vanno smistati nelle varie code
		l.lock(); //Prendo il lock poiché devo lavorare sui dati condivisi
		try {
			Thread corrente = Thread.currentThread(); //Prendo il thread che arriva, le code gestiscono in automatico la FIFO
			codePrioritarie[prenotazione].add(corrente); //Metto il thread in coda
			stanzaAssegnata.put(corrente, false); //Per attendere che mi venga asseganta una stanza
			System.out.printf("Sono il cliente %s mi metto in coda su %d\n",Thread.currentThread().getName(), prenotazione);
			attendiClienti.signalAll(); //Avviso il receptionist ce sono arrivato
			while ( !stanzaAssegnata.get(corrente) ) { //ciclo per wake up improvvisi/errati
				attendiReceptionist.await(); //Devo aspettare che il receptionist mi chiami
			}
			//Siccome sono passato devo farmi controllare la prenotazione dal receptionist
		}finally {
			l.unlock();
		}
	}//mettiInCoda

	@Override
	void checkOut() throws InterruptedException {
		//A questo punto il receptionist mi deve dare le chiavi per poter occupare la stanza
		//Una volta che mi è stata data la stanza la occupo per un tempo random
		TimeUnit.SECONDS.sleep(r.nextInt(2, 4)); //Occupo la stanza
		l.lock();
		try {
			camereOccupate--; //Libero una camera
			attendiClienti.signalAll(); //Sveglio di nuovo il receptionist
			System.out.printf("Sono il cliente %s ho lasciato la stanza\n",Thread.currentThread().getName());
		}finally {
			l.unlock();
		}
	}//checkOut

	@Override
	void iniziaCheckIn() throws InterruptedException {
		//Devo attendere che un thread mi svegli per potergli assegnare una camera
		l.lock();
		try {
			System.out.printf("Sono il receptionist %s servo i clienti\n",Thread.currentThread().getName());
			while ( isAperto() && codeVuote() || camereOccupate == super.N_CAMERE ) { 
				//Aspetto che arrivi un cliente a svegliarmi o che si svuoti una camera e soprattutto che siamo ancora aperti
				attendiClienti.await();
			}
			if ( !isAperto() ) return; //Se sono stato svegliato non devo eseguire codice
			//Devo iniziare a servire i clienti delle code, sicuramente c'è almeno una camera libera altrimenti non mi svegliavo
			System.out.printf("Sono il receptionist %s cerco una stanza al cliente\n",Thread.currentThread().getName());
			Receptionist r = (Receptionist) Thread.currentThread(); //Prendo il thread corrente per arrivare alla coda a lui assegnata
			int prenotazione = r.getCoda(); //Devo salvare anche la coda da cui l'ho preso per poi toglierlo e far avanzare il prossimo
			Thread clienteDaServire = codePrioritarie[prenotazione].poll(); //Estraggo dalla coda assegnata al receptionist
			if ( clienteDaServire == null ) { //If per variante 4b, se vuoi la prima togli le tre righe sopra e metti le due sotto receptionist dopo questo if
				for ( int i = 2; i >= 0; i-- ) {//Comincio a scorrere le code dalla più prioritaria dato che in quella assegnata non ho più clienti
					clienteDaServire = codePrioritarie[i].poll(); //Prendo il thread in testa alla coda
					if ( !(clienteDaServire == null) ) { //Controllo che prenda effettivamente un thread
						prenotazione = i; //Salvo la coda da cui ho fatto avanzare il cliente
						System.out.printf("Sono il receptionist %s servo il cliente %s della coda %d\n",Thread.currentThread().getName(), clienteDaServire.getName(), prenotazione);
						break; //Mi fermo al primo thread della coda più prioritaria						
					}
				}//Fine scorrimento code
			}
			clientiServiti++; //Servo un cliente, per la variante 4b va spostato qui altrimenti era in fine check-in, due thread vedono il contatore in modo errato se lo lascio giù
			clienteControllato.put(Thread.currentThread(), clienteDaServire); //Bindo i thread per poi riprenderli dopo e finire il chek-in
			//codaServita.put(Thread.currentThread(), prenotazione); //Tengo conto della coda su cui operavo, per variante 4b non serve più
		}finally {
			l.unlock();
		}
	}//iniziaCheckIn

	@Override
	void fineCheckIn() throws InterruptedException {
		//Qui devo prendere il cliente, digli che è tutto ok e farlo accomodare in camere
		l.lock();
		try {
			if ( !isAperto() ) return; //Se l'hotel è chiuso è inutile andare avanti
			camereOccupate += 1; //Segno che ho fatto entrare un cliente
			Thread mioCliente = clienteControllato.get(Thread.currentThread()); //Prendo il cliente che stavo servendo
			stanzaAssegnata.put(mioCliente, true); //Comunico la stanza assegnata
			//Per la variante 4b la rimozione viene fatta ad inizio, altrimenti andava fatta qui e necessitavo di una mappa aggiuntiva per capire da quale coda il thread aveva preso il cliente
			//int coda = codaServita.get(Thread.currentThread); //Metodo prima variante con un solo thread
			//codePrioritarie[coda].remove(mioCliente); //Metodo per prima variante
			if ( !isAperto() ) attendiClienti.signalAll(); //Sveglio  i receptionist variante 4b
			attendiReceptionist.signalAll(); //Sveglio chi era in coda
			System.out.printf("Sono il receptionist %s ho assegnato una stanza a %s\n",Thread.currentThread().getName(),mioCliente.getName());
		}finally {
			l.unlock();
		}
	}//fineCheckIn

}//HotelLC
