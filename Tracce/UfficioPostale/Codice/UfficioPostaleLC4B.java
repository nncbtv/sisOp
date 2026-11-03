package sisOp.Tracce.UfficioPostale.Codice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class UfficioPostaleLC4B extends UfficioPostale {
	
	//GESTIONE PROBLEMA
	private final int numeroCodePerOperazione; //Per questa variante avremo N code per ogni operazione
	private int clientiServiti = 0; //Conto i clienti serviti per arrivare allo stop

	//STRUTURE DATI SINCRONIZZAZIONE
	private Queue<Thread> codaTicket = new LinkedList<>(); //Coda su cui aspettare per prelevare i ticket
	private Map<String, Integer> ticketCoda = new HashMap<>(); //Devo tenere conto dei ticket venduto per tipo di coda
	private Queue<Thread> [][] codeOperazioni; //Uso una matrice in cui elemento [i][j] indica operazione i coda j
	private Map<Thread, Boolean> operazioneInCorso = new HashMap<>(); //Per far attendere il primo in coda
	private Map<Thread, Integer> codaServita = new HashMap<>(); //Per associare cliente ad impiegato
	
	//STRUMENTI DI SINCRONIZZAZIONE
	private ReentrantLock l = new ReentrantLock(); //Lock per mutua esclusione
	private Condition macchinaTicket = l.newCondition(); //Condizione per attendere la macchinetta dei ticket se non sono il primo in coda
	private Condition chiamataImpiegato = l.newCondition(); //Condizione su cui attendere il medico
	private Condition esito = l.newCondition(); //Condizione su cui il cliente attende l'esito dell'operazione
	private Condition clienti = l.newCondition(); //Condizione su cui l'impiegato dorme
	
	@SuppressWarnings("unchecked")
	public UfficioPostaleLC4B(int nClienti, int mImpiegati, int numeroCodeOperazione) {
		super(nClienti, mImpiegati);
		this.numeroCodePerOperazione = numeroCodeOperazione;
		//Siccome da traccia sappiamo che sono solo 3 tipi di coda li creo direttamente, per diminuire l'accopiamento dovremmo farli variabili
		ticketCoda.put("A", super.MASSIMO_TICKET_CODA);
		ticketCoda.put("B", super.MASSIMO_TICKET_CODA);
		ticketCoda.put("C", super.MASSIMO_TICKET_CODA);
		//Inizializzo la matrice
		codeOperazioni = new Queue[super.NUMERO_CODE][numeroCodePerOperazione]; 
		for ( int i = 0; i<super.NUMERO_CODE; i++ ) {
			for (int j = 0; j<numeroCodePerOperazione; j++) {
				codeOperazioni[i][j] = new LinkedList<Thread>();
			}
		}
	}//Costruttore
	
	@Override
	boolean isAperto() { //Metodo di supporto
		return clientiServiti < super.CLIENTI_DA_SERVIRE;
	}//isAperto

	@Override
	boolean ritiraTicket(String tipoTicket) throws InterruptedException {
		l.lock(); //Prendo il lock per la mutua esclusione
		try {
			Thread cliente = Thread.currentThread(); //Prendo il cliente che è appena arrivato
			codaTicket.add(cliente); //Metto il cliente in coda
			while ( codaTicket.peek() != cliente ) { //Finchè non è il primo in coda lo faccio attendere
				macchinaTicket.await();
			}
			//Se il cliente è il primo in coda passa
			codaTicket.remove(cliente); //Lascia la prima posizione al prossimo che acquisisce il lock
			//Presumo che tipoTicket sia un insieme di quel tipo di operazioni, altrimetni andrebbe fatto controllo
			int ticketRimasti = ticketCoda.get(tipoTicket);
			if ( ticketRimasti > 0 ) {
				ticketRimasti--; //Aggiorno il contatore dei ticker rimasti
				ticketCoda.put(tipoTicket, ticketRimasti); //Aggiorno nella mappa
				macchinaTicket.signalAll(); //Segnalo ai thread addormentati che vado via
				System.out.println(cliente.getName()+" ticket comprato");
				return true;
			}
			//Se non ci sono più ticket per quel tipo di operazione esco direttamente
			clientiServiti++; //Implicitamente il cliente uscito è un cliente servito, EVITA DEADLOCK
			macchinaTicket.signalAll(); //Segnalo ai thread addormentati che vado via
			System.out.println(cliente.getName()+" ticket finiti");
			return false; //Non sono riuscito a prendere il ticket
		}finally {
			l.unlock(); //Rilascio il lucchetto
		}
	}//ritiraTicket

	private int tokenizer(String tipoTicket) { //Mi serve per passare da tipo stringa a intero per accedere alla matrice delle code
		switch (tipoTicket) {
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
	}//tokenizer
	
	@Override
	void attendiSportello(String tipoTicket) throws InterruptedException {
		//Permette al thread cliente di mettersi in coda per l'operazione che deve svolgere, code piene sceglie quella più corta
		l.lock(); //Prendo il lock per la mutua esclusione
		try {
			Thread cliente = Thread.currentThread(); //Prendo il cliente che è appena arrivato
			int operazioneCliente = tokenizer(tipoTicket); //Converto per usare l'indice intero e entrare in matrice
			int codaScelta = -1; //Per capire in quale coda si posiziona il nostro thread
			//Ora devo scegliere su quale coda andare
			for ( int i = 0; i<numeroCodePerOperazione; i++ ) {
				//Non serve scorrere il tipo di code, lo abbiamo già nella variabile operazioneCliente
				if ( codeOperazioni[operazioneCliente][i].isEmpty() ) {
					codaScelta = i; //Salvo la coda su cui mi trovo
					codeOperazioni[operazioneCliente][codaScelta].add(cliente); //Aggiungo il thread in coda
					break; //Esco dalla scelta code
				}
			}
			//Se nessuna coda è vuota mi devo mettere in quella più corta
			if ( codaScelta == -1 ) { //Valore default, non è stata sovrascritta nel for
				int codaCorta = Integer.MAX_VALUE; //valore fittizzio da sovrascrivere
				for ( int i = 0; i<numeroCodePerOperazione; i++ ) { //Controllo la lunghezza di ogni coda
					if ( codeOperazioni[operazioneCliente][i].size() < codaCorta ) { //A fine iterazioni codaScelta sarà la più piccola
						codaCorta = codeOperazioni[operazioneCliente][i].size(); //Aggiorno il massimo
						codaScelta = i;
					}
				}
				codeOperazioni[operazioneCliente][codaScelta].add(cliente); //Mi metto in coda sulla più corta
			}
			//A questo punto mi sono sicuramente accodato in una coda, devo attendere che l'impiegato mi chiami per servirmi
			clienti.signalAll(); //Sveglio gli impiegati per dirgli che sono in coda
			System.out.println(cliente.getName()+" è in per op. "+operazioneCliente+ " sulla coda "+codaScelta);
			while ( codeOperazioni[operazioneCliente][codaScelta].peek() != cliente ) {
				chiamataImpiegato.await(); //Attendo che mi chiamino se non sono il primo in coda
			}
			//Siccome mi hanno chiamato/sono il primo armo il flag per essere servito
			operazioneInCorso.put(cliente, true); //Flag armato che mi blocca nel secondo metodo
			//Adesso devo attendere l'esito dell'operazione
			System.out.println(cliente.getName()+" è il primo da servire nella sua coda");
			while ( operazioneInCorso.get(cliente) ) {
				esito.await();
			}
		}finally {
			l.unlock();
		}
	}//attendiSportello

	private boolean codeVuote(Queue<Thread> [][] code, int operazione) {
		for ( int i = 0; i<numeroCodePerOperazione; i++ ) { //Controllo la lunghezza di ogni coda
			if ( !codeOperazioni[operazione][i].isEmpty() ) return false;
			//Se una coda che devo servire non è vuota allora ritorno false per uscire dal while
		}
		return true; //Iterate tutte le code, ed ognuna di essa è vuota ritorno true
	}//codeVuote
	
	@Override
	void prossimoCliente() throws InterruptedException {
		//Devo servire il prossimo cliente, ovviamente il primo ciclo di questo metodo è inutile, il primo in coda mi sta già aspettando
		l.lock();
		try {
			Impiegato imp = (Impiegato) Thread.currentThread(); //Devo capire che tipo di  impiegato ho
			int operazioneImpiegato = imp.getTipoUfficio(); //Prendo il tipo di operazione che risolve questo impiegato
			System.out.print("[ IMPIEGATO ]: "+Thread.currentThread().getName()+" sta servendo: ");
			while ( isAperto() && codeVuote(codeOperazioni, operazioneImpiegato) ) {
				clienti.await(); //Aspetto i clienti
			}
			if ( !isAperto() ) return;
			//L'impiegato lavorerà sempre sulla prima coda non vuota
			int codaScelta = -1;
			int codaLunga = Integer.MIN_VALUE;
			for ( int i = 0; i<numeroCodePerOperazione; i++ ) { //Controllo la lunghezza di ogni coda
				if ( codeOperazioni[operazioneImpiegato][i].size() > codaLunga) {
					codaLunga = codeOperazioni[operazioneImpiegato][i].size();
					codaScelta = i;
				}
			}
			codaServita.put(Thread.currentThread(), codaScelta); //Associo la coda servita al thread
			//Appena un cliente mi sveglia devo farlo passare e poi segnalare al prossimo in coda
			Thread cliente = codeOperazioni[operazioneImpiegato][codaScelta].peek(); //Prendo il cliente in coda per capire quale sto servendo
			System.out.println(cliente.getName()); //Stampo per capire se va tutto bene
			chiamataImpiegato.signalAll(); //Sveglio i thread in attesa per dirgli che sono libero a servirli
		}finally {
			l.unlock();
		}
	}//prossimoCliente

	@Override
	void eseguiOperazione() throws InterruptedException {
		//Devo eseguire l'operazione
		Thread corrente = Thread.currentThread();
		Impiegato imp = (Impiegato) corrente;
		System.out.println("[ IMPIEGATO ]: "+corrente.getName()+" sta controllando le carte");
		int codaAssegnata = imp.getTipoUfficio(); //Prendo anche il suo tipo di servizio
		//Simulazioni delle attese di servizio
		switch(codaAssegnata) {
			case 0: {
				TimeUnit.SECONDS.sleep(1);
				break;
			}
			case 1: {
				TimeUnit.SECONDS.sleep(1);
				break;
			}
			case 2: {
				TimeUnit.SECONDS.sleep(1);
				break;
			}
		}
		//Eseguite le operazioni posso andare avanti con le operazioni di sincronizzazione
		l.lock();
		try {
			int operazioneImpiegato = imp.getTipoUfficio(); //Prendo il tipo di operazione che risolve questo impiegato
			int codaServita = this.codaServita.get(Thread.currentThread()); //Prendo la coda che stavo servendo
			Thread clienteServito = codeOperazioni[operazioneImpiegato][codaServita].poll(); //Lo rimuovo dalla coda perché ormai è stato servito
			operazioneInCorso.put(clienteServito, false); //Segnalo di aver finito di servire
			esito.signalAll(); //Sveglio chi stava aspettando me 
			clientiServiti++; //Aggiorno il contatore
			if ( !isAperto() ) {
				System.out.println("[ IMPIEGATO ]: "+corrente.getName()+" ha segnalato l'ultimo cliente");
				clienti.signalAll(); //Sveglio gli altri
				return; //Fine
			}
		}finally {
			l.unlock();
		}
	}//eseguiOperazione

}//UfficioPostaleLC4B
