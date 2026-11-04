package sisOp.Tracce.Pizzeria.Codice;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PizzeriaLC4B extends Pizzeria {
	
	//STRUTTURE DATI PER LA SINCRONIZZAZIONE
	private int clientiServiti = 0; //Per arrestare il pizzaiolo a fine clientela
	private final int NUMERO_MAXI_TAVOLI; //Tavoli da servire in pizzeria
	private Queue<Thread> codaEntrata = new LinkedList<>(); //Unica coda di entrata per la pizzeria
	private int [] postiTavolo; //L'indice di accesso indica il tavolo, la cella i posti a sedere rimanenti
	private boolean [] tavoloOccupato; //Indica che il tavolo è pieno
	private boolean [] attesaPizza; //Indica che i clienti stanno aspettando la pizza
	private boolean [] pizzaServita; //Indica se ho servito o meno la pizza al tavolo i
	private Map<Thread, Integer> tavoloScelto = new HashMap<>(); //Per associare tavolo->cliente
	
	//STRUMENTI DI SINCRONIZZAZIONE
	private ReentrantLock l = new ReentrantLock(); //Lock per la mutua esclusione
	private Condition posto = l.newCondition(); //Condizione su cui attendere il posto
	private Condition clienti = l.newCondition(); //Condizione su cui attende il pizzaiolo
	private Condition pizzaiolo = l.newCondition(); //Condizione su cui far attendere i clienti
	
	public PizzeriaLC4B(int nClienti, int nTavoli) {
		super(nClienti);
		this.NUMERO_MAXI_TAVOLI = nTavoli;
		postiTavolo = new int[NUMERO_MAXI_TAVOLI];
		tavoloOccupato = new boolean[NUMERO_MAXI_TAVOLI];
		attesaPizza = new boolean[NUMERO_MAXI_TAVOLI];
		pizzaServita = new boolean[NUMERO_MAXI_TAVOLI];
		for ( int i = 0; i<NUMERO_MAXI_TAVOLI; i++ ) {
			postiTavolo[i] = 5;
			tavoloOccupato[i] = false;
			pizzaServita[i] = false;
		}
	}//Costruttore

	@Override
	boolean isAperto() {
		return clientiServiti < super.CLIENTI_PIZZERIA;
	}//isAperto

	private boolean tavoliOccupati() {
		for ( int i = 0; i<NUMERO_MAXI_TAVOLI; i++ ) {
			if ( tavoloOccupato[i] == false ) return false;
		}
		return true;
	}
	
	@Override
	void entra() throws InterruptedException {
		l.lock();
		try {
			Thread cliente = Thread.currentThread();
			codaEntrata.add(cliente);
			System.out.println("[ CLIENTE ]: "+cliente.getName()+" è fuori dalla pizzeria");
			while(codaEntrata.peek() != cliente || tavoliOccupati() ) {
				posto.await();
			}
			codaEntrata.remove(cliente);
			for ( int i = 0; i<NUMERO_MAXI_TAVOLI; i++ ) {
				if ( postiTavolo[i] > 0 ) {
					postiTavolo[i]--; //Mi siedo al tavolo
					tavoloScelto.put(cliente, i); //Per salvare il tavolo dove il thread è seduto
					System.out.println("[ CLIENTE ]: "+cliente.getName()+" si è seduto al tavolo "+i);
					attesaPizza[i] = true; //Armo il flag per attendere il pizzaiolo
					if ( postiTavolo[i] == 0 ) {
						System.out.println("[ CLIENTE ]: "+cliente.getName()+" si è seduto al tavolo per ultimo");
						tavoloOccupato[i] = true; //Segnalo il tavolo pieno
						clienti.signalAll(); //Informo il pizzaiolo che un tavolo è pronto
					}
					break;
				}
			}
			posto.signalAll();
		} finally {
			l.unlock();
		}
	}//entra

	@Override
	void mangiaPizza() throws InterruptedException {
		l.lock();
		try {
			Thread cliente = Thread.currentThread();
			int tavolo = tavoloScelto.get(cliente);
			System.out.println("[ CLIENTE ]: "+Thread.currentThread().getName()+" aspetta la pizza al tavolo "+tavolo);
			while ( attesaPizza[tavolo] ) {
				pizzaiolo.await();
			}
		}finally {
			l.unlock();
		}
		//simulo la mangiata
		TimeUnit.SECONDS.sleep(1);
		l.lock();
		try {
			Thread cliente = Thread.currentThread();
			int tavolo = tavoloScelto.get(cliente);
			postiTavolo[tavolo]++;
			if ( postiTavolo[tavolo] == 5 ) {
				tavoloOccupato[tavolo] = false;
				pizzaServita[tavolo] = false;
				posto.signalAll(); //Sveglio i clienti fuori dalla pizzeria
			}
			clientiServiti++;
			System.out.println("[ CLIENTE ]: "+Thread.currentThread().getName()+" sta uscendo");
			if (!isAperto() ) {
				System.out.println("[ CLIENTE ]: "+Thread.currentThread().getName()+" ha salutato il pizzaiolo");
				for ( int i =0; i<NUMERO_MAXI_TAVOLI; i++ ) {
					pizzaServita[i] = false; //Disarmo flag attesa pizzaiolo
				}
				clienti.signalAll(); //Ero l'ultimo cliente ad andare via, quindi saluto il pizzaiolo (lo sveglio se non vado in DEADLOCK)
			}
		}finally {
			l.unlock();
		}
	}//mangiaPizza
	
	private int tavoliDaServire() {
		for ( int i = 0; i<NUMERO_MAXI_TAVOLI; i++ ) {
			if ( pizzaServita[i] == false && tavoloOccupato[i] ) {
				return i; //Indice del tavolo da servire
			}
		}
		return -1;
	}//tavoliDaServire
	
	@Override
	void preparaPizza() throws InterruptedException {
		l.lock();
		try {
			int tavoloDaServire = -1; //Supponiamo non ci siano tavoli da servire
			while (isAperto() && tavoliDaServire() == -1) {
				//Attendo che si siedano tutti oppure che finiscano di mangiare la pizza con !pizzaInForno
				clienti.await();
			}
			tavoloDaServire = tavoliDaServire();
			if ( !isAperto() ) return; //Sono finiti i clienti
			System.out.println("[ PIZZAIOLO ]: "+Thread.currentThread().getName()+" sta preparando la pizza al tavolo "+tavoloDaServire);
			pizzaServita[tavoloDaServire] = true;
			tavoloScelto.put(Thread.currentThread(), tavoloDaServire); //Associo pizzaiolo -> tavolo servito
		}finally {
			l.unlock();
		}
		TimeUnit.SECONDS.sleep(2); //Simulo l'infornata della pizza
	}//preparaPizza

	@Override
	void serviPizza() throws InterruptedException {
		l.lock();
		try {
			if (!isAperto() ) return;
			int tavoloServito = tavoloScelto.get(Thread.currentThread());
			System.out.println("[ PIZZAIOLO ]: "+Thread.currentThread().getName()+" ha finito di preaparare la pizza per il tavolo "+tavoloServito);
			attesaPizza[tavoloServito] = false;
			pizzaiolo.signalAll();
		}finally {
			l.unlock();
		}
	}//serviPizza

}//PizzeriaLC4B
