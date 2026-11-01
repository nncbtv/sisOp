package sisOp.Tracce.UfficioPostale.Codice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UfficioPostaleLC extends UfficioPostale {

	//GESTIONE FLUSSO
	private int clientiServiti = 0; //Contatore di clienti serviti per poi interrompere i cicli sugli impiegati
	private Map<String, Integer> ticketRimasti = new HashMap<>(); //Contatore dei ticket emessi per ogni tipo coda
	
	//GESTIONE SINCRONIZZAZIONE
	private ReentrantLock l = new ReentrantLock(); //Gestisce la mutua esclusione
	private Queue<Thread> codaMacchinetta = new LinkedList<>(); //Coda FIFO macchinetta
	private Condition attendiMacchinetta = l.newCondition(); //Aspetto il turno per la macchinetta dei ticket
	private Map<Thread, Boolean> attendiServizio = new HashMap<>(); //Preso il ticket e passata la fila l'impiegato deve darmi il via
	private Condition attendiImpiegato = l.newCondition(); //Per far attendere che l'impiegato finisca il servizio
	private Condition attendiCliente = l.newCondition();
	private Condition attendiFineServizio = l.newCondition();//Per far attendere un cliente ad un impiegato quando è fermo
	
	@SuppressWarnings("unchecked")
	private Queue<Thread> [] code = new Queue[super.NUMERO_CODE]; //Per smistare in code i clienti
	
	public UfficioPostaleLC(int n, int m) {
		super(n,m);
		for ( int i = 0; i<super.NUMERO_CODE; i++ ) {
			code[i] = new LinkedList<>();
		}
		ticketRimasti.put("A", super.MASSIMO_TICKET_CODA);
		ticketRimasti.put("B", super.MASSIMO_TICKET_CODA);
		ticketRimasti.put("C", super.MASSIMO_TICKET_CODA);
	}//costruttore
	
	@Override
	boolean isAperto() {
		return clientiServiti < super.CLIENTI_DA_SERVIRE;
	}//isAperto

	@Override
	boolean ritiraTicket(String tipoTicket) throws InterruptedException {
		l.lock();
		try {
			Thread corrente = Thread.currentThread(); //Prendo il thread per metterlo in coda
			codaMacchinetta.add(corrente); //Aggiungo il thread alla coda macchinetta
			//System.out.println(corrente.getName()+" sta comprando il ticket per la coda "+tipoTicket);
			while ( codaMacchinetta.peek() != corrente ) { //Aspetto finche non sono il primo in coda
				attendiMacchinetta.await();
			}
			if ( ticketRimasti.get(tipoTicket) > 0 ) {
				ticketRimasti.put(tipoTicket, ticketRimasti.get(tipoTicket)-1); //Prendo un ticket per la mia coda
				codaMacchinetta.remove(corrente);//Mi rimuovo dalla coda
				attendiMacchinetta.signalAll(); //Sveglio chi stava aspettando la macchinetta
				//System.out.println(corrente.getName()+" ticket comprato");
				return true;
			}
			codaMacchinetta.remove(corrente);
			attendiMacchinetta.signalAll();
			//System.out.println(corrente.getName()+" ticket finiti");
			clientiServiti++; //Non devo tenere conto di questi clienti, sono già stati "serviti"
			return false;
		}finally {
			l.unlock();
		}
	}//ritiraTicket

	private int getIndiceCoda( String tipoTicket ) {
		if ( tipoTicket.equals("A")) return 0; //Non confronto i riferimenti in memoria con == ma faccio equals per essere sicuro di avere un codice
		if ( tipoTicket.equals("B")) return 1;
		if ( tipoTicket.equals("C")) return 2;
		return -1;
	}//getIndiceCoda
	
	@Override
	void attendiSportello(String tipoTicket) throws InterruptedException {
		l.lock();
		try {
			int ticket = getIndiceCoda(tipoTicket); //Per castizzare da stringa a indice e accordalo per bene
			Thread corrente = Thread.currentThread(); //Prendo il thread corrente
			code[ticket].add(corrente); //Lo metto nella coda
			attendiCliente.signalAll(); //Sveglio gli impiegati per dirgli che mi sto mettendo in coda
			//System.out.println(corrente.getName()+" è in coda");
			while ( code[ticket].peek() != corrente ) { //Lo faccio aspettare finché non è il primo e la coda può essere servita
				attendiImpiegato.await();
			}
			//non rimuovo qui il cliente poiché deve essere il thread impiegato a farlo
			attendiServizio.put(corrente, true); //Aspetto che mi diano il via gli impiegati
			System.out.println(corrente.getName()+" è il primo da servire nella sua coda");
			while ( attendiServizio.get(corrente) ) {
				attendiFineServizio.await();
			}
		} finally {
			l.unlock();
		}
	}//attendiSportello

	@Override
	void prossimoCliente() throws InterruptedException {
		l.lock();
		try {
			//Prendo l'impiegato corrente
			Thread corrente = Thread.currentThread();
			Impiegato i = (Impiegato) corrente;
			int codaAssegnata = i.getTipoUfficio(); //Prendo anche il suo tipo di servizio
			System.out.print("[ IMPIEGATO ]: "+corrente.getName()+" sta servendo: ");
			while ( isAperto() && code[codaAssegnata].isEmpty() ) { //Aspetto finchè non ci sono clienti da servire
				attendiCliente.await();
			}
			if ( !isAperto() ) return; //Se l'ufficio è chiuso torno senza fare nulla
			Thread cliente = code[codaAssegnata].peek(); //Lo tolgo dalla coda cosi poi al prossimo signal passa quello dietro
			attendiImpiegato.signalAll(); //Segnalo il prossimo per farlo avanzare
			System.out.println(cliente.getName());
		}finally {
			l.unlock();
		}
	}//prossimoCliente

	@Override
	void eseguiOperazione() throws InterruptedException {
		Thread corrente = Thread.currentThread();
		Impiegato i = (Impiegato) corrente;
		System.out.println("[ IMPIEGATO ]: "+corrente.getName()+" sta controllando le carte");
		int codaAssegnata = i.getTipoUfficio(); //Prendo anche il suo tipo di servizio
		//Simulazioni delle attese di servizio
		if ( codaAssegnata == 0 ) {
			TimeUnit.SECONDS.sleep(3);
		}
		if ( codaAssegnata == 1 ) {
			TimeUnit.SECONDS.sleep(5);
		}
		if ( codaAssegnata == 2 ) {
			TimeUnit.SECONDS.sleep(7);
		}
		l.lock();
		try {
			Thread cliente = code[codaAssegnata].poll(); //rimuovo il cliente dalla coda
			attendiServizio.put(cliente, false); //Disarmo il flag che lo faceva aspettare
			attendiFineServizio.signalAll(); //Sveglio chi stava aspettando che finissi
			clientiServiti++; //Conto che ho servito un cliente
			if ( !isAperto() ) { //Se ho servito l'ultimo devo dare il via alla chiusura
				System.out.println("[ IMPIEGATO ]: "+corrente.getName()+" ha segnalato l'ultimo cliente");
				attendiCliente.signalAll();
				return;
			}
		} finally {
			l.unlock();
		}
	}//eseguiOperazione

}//UfficioPostaleLC