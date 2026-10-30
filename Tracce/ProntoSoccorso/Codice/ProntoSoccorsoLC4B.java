package sisOp.Tracce.ProntoSoccorso.Codice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ProntoSoccorsoLC4B extends ProntoSoccorso {

	//Gestione flusso
	private int contaPazientiVisitati = 0; //Conto i pazienti serviti dal medico
	private Random r = new Random(); //Generatore random per simulazione azioni
	private boolean pazienteCritico = false; //Flag per servire un paziente giallo dopo i turni limite
	private static final int LIMITE_TURNI = 5; //Costante limite turni di attesa per un paziente di tipo giallo
	private static final int CODA_VARIABILE = 1; //Per indicare quale coda deve gestire una variazione di priorità sul primo in coda
	private static final int N_CODE = 3; //Code della nostra triage
	private final int M_MEDICI = 2; //Per la variante devo tenere conto che possono esserci più medici, non sappiamo quanti
	private boolean [] visitaInCorso = new boolean [M_MEDICI]; //Per fare uscire il cliente dalla sala del medico, in questa variante diventa un array, lungo quanto i medici
	
	//Gestione code
	@SuppressWarnings("unchecked") //Va aggiunte perché l'ide la segna come errore altrimenti
	private Queue<Thread> [] code = new Queue[N_CODE]; //Array di code
	private Map<Thread, Integer> turniAttesi = new HashMap<>(); //Devo tenere conto di quanti turni ha aspettato un codice giallo
	private Map<Thread, Boolean> pazienteInVisita = new HashMap<>(); //Per segnalare al thread che sta venendo visitato
	private Thread [] pazienteEntrato = new Thread[M_MEDICI]; //Paziente entrato dal medico, in questa variante diventa un array
	
	//Gestione sezione critica
	private ReentrantLock l = new ReentrantLock();
	private Condition attendiMedico = l.newCondition();
	private Condition attendiPaziente = l.newCondition();
	private Condition attendiEsito = l.newCondition();
	
	public ProntoSoccorsoLC4B( int clienti ) {
		super(clienti);
		for ( int i = 0; i<code.length; i++ ) {
			code[i] = new LinkedList<>(); //Inizializzo ogni coda
		}
		for ( int i = 0; i<M_MEDICI; i++ ) {
			visitaInCorso[i] = false;
			pazienteEntrato[i] = null;
		}
	}//Costruttore

	@Override
	boolean isAperto() {
		//Resto aperto finche ci sono pazienti da visitare
		return contaPazientiVisitati < super.N_PAZIENTI;
	}//isAperto

	@Override
	void iniziaVisita() throws InterruptedException {
		l.lock(); //Prendo il lock per cominciare ad operare sulla sezione critica
		try {
			Medico m = (Medico) Thread.currentThread(); //castizzo per usare i suoi metodi
			int studioMedico = m.getNumeroCoda(); //Prendo il numero dello studio medico assegnato al medico
			while ( pazienteEntrato[studioMedico] == null && isAperto() ) {
				attendiPaziente.await(); //Devo attendere che arrivino i clienti
			}
			if ( !isAperto() ) return; //Abbiamo finito di servire i pazienti
			System.out.println("[MEDICO]: Sta visitanto "+pazienteEntrato[studioMedico].getName()+ " paziente critico "+pazienteCritico);
			//Mi serve il codice del tipo di visita del paziente
			Paziente p = (Paziente) pazienteEntrato[studioMedico]; //Castizzo per usare i metodi di paziente
			pazienteInVisita.put(p, true); //Segno che è in visita da me
			int tipoVisita = p.getCodice(); //Prendo il tipo della visita
			if ( tipoVisita == 0 ) { //Codice verde
				TimeUnit.SECONDS.sleep(r.nextInt(1,3));
			}
			if ( tipoVisita == 1 ) {//Codice giallo
				TimeUnit.SECONDS.sleep(r.nextInt(3,5));
			}else {//Codice Rosso
				TimeUnit.SECONDS.sleep(r.nextInt(5,7));
			}
			//Non rilascio il lock perché non devo far passare altri nella sala ecc.
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			l.unlock(); //Rilascio il lucchetto qualsiasi cosa succede nel try
		}
	}//iniziaVisita

	@Override
	void terminaVisita() throws InterruptedException {
		l.lock();
		try {
			//Ho finito la visita quindi devo ricominciare ad armare tutti i flag
			Medico m = (Medico) Thread.currentThread(); //castizzo per usare i suoi metodi
			int studioMedico = m.getNumeroCoda(); //Prendo il numero dello studio medico assegnato al medico
			Paziente p = (Paziente) pazienteEntrato[studioMedico]; //Castizzo per usare i metodi di paziente
			pazienteInVisita.put(p, false); //Lo avviso che la visita è finita e può andare via
			System.out.println("[MEDICO]: Ha visitato "+pazienteEntrato[studioMedico].getName()+ " paziente "+contaPazientiVisitati);
			contaPazientiVisitati++;
			visitaInCorso[studioMedico] = false; //Do il via a chi stava aspettando l'esito
			pazienteCritico = false; //Il flag lo disarmo perché sicuramente il paziente critico è stato assegnato ad un medico libero, dal thread appena uscito
			pazienteEntrato[studioMedico] = null; //Per prendere poi il prossimo
			attendiEsito.signalAll(); //Sveglio il paziente che era entrato
		} finally {
			l.unlock();
		}
	}//terminaVisita

	@Override
	void accediPaziente() throws InterruptedException {
		l.lock();
		try {
			Thread corrente = Thread.currentThread(); //Prendo il thread corrente nel metodo
			Paziente p = (Paziente) corrente; //Cast per prendere il codice e indirizzarlo sulla propria coda
			int codice = p.getCodice(); //Codice coda
			System.out.println(corrente.getName()+" aspetta su coda "+codice);
			if ( codice == CODA_VARIABILE ) turniAttesi.put(corrente, 0); //Comicio a contare i turni se è nella coda variabile
			code[codice].add(corrente); //Lo butto in coda
			while ( attendiTurno(corrente, codice) || mediciOccupati() ) { //Faccio aspettare finchè non è il paziente corretto o la visita è in corso
				attendiMedico.await();
			}
			//Devo andare dal medico libero
			for ( int i = 0; i < M_MEDICI; i++ ) {
				if (pazienteEntrato[i] == null ) {
					visitaInCorso[i] = true;
					pazienteEntrato[i] = corrente;
					break; //Mi fermo al primo libero, inutile andare avanti
				}
			}
			attendiPaziente.signalAll(); //Sveglio il medico in attesa
		} finally {
			l.unlock();
		}
	}//accediPaziente
	
	private boolean attendiTurno( Thread x, int codice ) {
		for ( int i = 0; i < M_MEDICI; i++ ) {
			if (pazienteEntrato[i] == x ) return false;
		}
		if ( pazienteCritico ) return true; //Deve attendere che finiscano di servire il paziente critico
		if ( codice == N_CODE-1 ) {
			if ( x == code[codice].peek() ) return false; //Non esiste cliente critico e sono il primo della coda più prioritaria
			return true; //Non sono il primo della coda prioritaria
		}
		if ( !code[codice+1].isEmpty() ) return true; //Deve svuotarsi prima la coda più prioritaria alla mia
		if ( code[codice].peek() == x ) return false; //Sono il primo della mia coda
		return true; //Non sono il primo della coda
	}//Prossimo

	private boolean mediciOccupati() {
		for ( int i = 0; i < M_MEDICI; i++ ) {
			if (pazienteEntrato[i] == null ) return false; //c'è un medico libero
		}
		return true; //sono tutti occupati
	}//mediciOccupati
	
	@Override
	void esciPaziente() throws InterruptedException {
		l.lock();
		try {
			Thread corrente = Thread.currentThread(); //Prendo il thread corrente nel metodo
			System.out.println(corrente.getName()+" attende esito visita");
			while ( pazienteInVisita.get(corrente) ) { //Aspetto finché sono in visita
				attendiEsito.await(); //Devo attendere il via dal medico per uscire
			}
			Paziente p = (Paziente) corrente; //Cast per prendere il codice e indirizzarlo sulla propria coda
			int codice = p.getCodice(); //Codice coda
			code[codice].remove(corrente); //Abbandono la coda per cedere il posto al prossimo
			System.out.println(corrente.getName()+" esce");
			//Devo aggiornare i turni del primo in coda alla coda variabile se non è vuota
			if ( code[CODA_VARIABILE].isEmpty() ) {
				pazienteCritico = false;
				attendiMedico.signalAll();
				return;
			}
			Thread primoCodaVariabile = code[CODA_VARIABILE].peek(); //Prendo il primo in coda
			int turniVecchi = turniAttesi.get(primoCodaVariabile); //Prendo anche il suo numero di turni vecchio
			turniAttesi.put(primoCodaVariabile, turniVecchi+1); //Aggiorno i turni
			if ( turniVecchi+1 == LIMITE_TURNI ) {
				pazienteCritico = true; //Armo il flag per dire che devo servirlo subito
				for ( int i = 0; i < M_MEDICI; i++ ) {
					if ( pazienteEntrato[i] == null ) {
						visitaInCorso[i] = true;
						pazienteEntrato[i] = corrente; //Faccio passare lui se è arrivato al limite
						break; //Mi fermo al primo libero, inutile andare avanti
					}
				}
			}else {
				pazienteCritico = false; //Non ci sono pazienti critici
			}
			if ( !isAperto() ) //Se ero l'ultimo paziente sveglio tutti i medici
				attendiPaziente.signalAll();
			attendiMedico.signalAll(); //Sveglio gli altri in coda
		} finally {
			l.unlock();
		}
	}//esciPaziente

}//ProntoSoccorsoLC4b
