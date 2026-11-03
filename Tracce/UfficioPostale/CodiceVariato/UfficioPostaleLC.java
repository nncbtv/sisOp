package sisOp.Tracce.UfficioPostale.CodiceVariato;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UfficioPostaleLC extends UfficioPostale {

	//STRUTTURE DATI AUSILIARIE SINCRONIZZAZIONE
	private int clientiServiti = 0; //Contatore clienti serviti per stop sui cicli while impiegati
	private Queue<Thread> codaTicket = new LinkedList<>(); //Coda per i ticket
	private Map<String, Integer> ticketOperazione = new HashMap<>(); //Numero di ticket per ogni tipo di operazione
	@SuppressWarnings("unchecked")
	private Queue<Thread> [] codeOperazioni = new Queue[super.NUMERO_CODE]; //Code su cui devono attendere i thread clienti
	private Map<Thread, Boolean> operazioniInCorso = new HashMap<>(); //Flag per attendere l'impiegato
	
	//STRUMENTI DI SINCRONIZZAZIONE
	private ReentrantLock l = new ReentrantLock(); //Lucchetto per gestire mutua esclusione
	private Condition macchinetta = l.newCondition(); //Condition su cui far attendere per la macchinetta dei ticket
	private Condition chiamataImpiegato = l.newCondition(); //Condition su cui far attendere in fila i clienti
	private Condition esito = l.newCondition(); //Attendo che mi dia il via l'impiegato
	private Condition clienti = l.newCondition(); //Attendo che arrivino i clienti
	
	public UfficioPostaleLC(int n, int m) {
		super(n, m);
		//Inizializzo il numero di ticket diponibili per ogni coda
		ticketOperazione.put("A", super.MASSIMO_TICKET_CODA);
		ticketOperazione.put("B", super.MASSIMO_TICKET_CODA);
		ticketOperazione.put("C", super.MASSIMO_TICKET_CODA);
		for ( int i = 0; i<super.NUMERO_CODE; i++ ) {
			codeOperazioni[i] = new LinkedList<>();
		}
	}//costruttore

	@Override
	boolean isAperto() {
		//Resto aperto finché ci sono clienti da servire
		return clientiServiti < super.CLIENTI_DA_SERVIRE;
	}//isAperto

	@Override
	boolean ritiraTicket(String tipoTicket) throws InterruptedException {
		//Devo prendere il ticket in ordine FIFO
		l.lock(); //Prendo il lucchetto per avere mutua esclusione
		try {
			//Inserisco tutti i thread in coda FIFO
			Thread clienteCorrente = Thread.currentThread();
			codaTicket.add(clienteCorrente);
			while ( codaTicket.peek() != clienteCorrente ) {
				macchinetta.await();
			}
			//Se passo, sono il primo in coda
			codaTicket.remove(clienteCorrente); //Cedo il posto del primo in coda al prossimo che viene svegliato dietro di me
			//Controllo e prendo se disponibile un ticket per la mia operazione
			int ticketRimasti = ticketOperazione.get(tipoTicket);
			if ( ticketRimasti > 0 ) {
				ticketRimasti--;
				ticketOperazione.put(tipoTicket, ticketRimasti);
				macchinetta.signalAll();
				return true;
			}
			clientiServiti++; //Se sono finiti i ticket conto come servito il cliente che è andato via, evita DEADLOCK
			macchinetta.signalAll();
		}finally {
			l.unlock();
		}
		return false;
	}//ritiraTicket

	private int ticketConverter(String ticket) {
		//Questo metodo mi serve per passare da stringhe a indici, volendo posso passare le stringhe al tipo Impiegato
		switch (ticket) {
		case "A": {
			return 0;
		}
		case "B": {
			return 1;
		}
		case "C": {
			return 2;
		}
		default:
			return -1;
		}
	}//ticketConverter
	
	@Override
	void attendiSportello(String tipoTicket) throws InterruptedException {
		l.lock();
		try {
			//Devo mettermi in attesa sulla coda e aspettare che l'impiegato venga a servirmi
			Thread clienteCorrente = Thread.currentThread();
			int codaDiAttesa = ticketConverter(tipoTicket);
			codeOperazioni[codaDiAttesa].add(clienteCorrente);
			clienti.signalAll();
			while ( codeOperazioni[codaDiAttesa].peek() != clienteCorrente ) {
				chiamataImpiegato.await();
			}
			//Esco perché sono il primo, attendo la fine delle operazioni dell'impiegato
			operazioniInCorso.put(clienteCorrente, true);
			while ( operazioniInCorso.get(clienteCorrente) ) {
				esito.await();
			}
		}finally {
			l.unlock();
		}
	}//attendiSportello

	@Override
	void prossimoCliente() throws InterruptedException {
		l.lock();
		try {
			//Qui il nostro impiegato deve far avanzare la coda
			Thread impiegatoCorrente = Thread.currentThread();
			Impiegato imp = (Impiegato) impiegatoCorrente;
			int codaServita = imp.getTipoUfficio();
			Thread clienteServito = codeOperazioni[codaServita].poll();
			operazioniInCorso.put(clienteServito, false);
			clientiServiti++;
			esito.signalAll();
			chiamataImpiegato.signalAll();
			if ( !isAperto() ) {
				clienti.signalAll();
			}
		}finally {
			l.unlock();
		}
	}//prossimoCliente

	@Override
	void eseguiOperazione() throws InterruptedException {
		l.lock();
		try {
			Thread impiegatoCorrente = Thread.currentThread();
			Impiegato imp = (Impiegato) impiegatoCorrente;
			int codaServita = imp.getTipoUfficio();
			//Eseguo l'operazione appena entra un cliente
			while ( isAperto() && codeOperazioni[codaServita].isEmpty() ) {
				clienti.await();
			}
			if ( !isAperto() ) return;
		}finally {
			l.unlock();
		}
		//Simulo le operazioni
		TimeUnit.SECONDS.sleep(2);
	}//eseguiOperazione

}//UfficioPostale