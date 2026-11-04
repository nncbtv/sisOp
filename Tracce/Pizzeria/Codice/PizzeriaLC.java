package sisOp.Tracce.Pizzeria.Codice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class PizzeriaLC extends Pizzeria {

	//STRUTTURE DATI AUSILIARI SINCRONIZZAZIONE
	private int clientiServiti = 0; //Per arrestare il pizzaiolo a fine clienti
	private Queue<Thread> codaEntrata = new LinkedList<>(); //Coda per attendere all'entrata il tavolo
	private int postiTavolo = 5; //Posti a sedere nel maxi tavolo
	private boolean pizzaInForno = false; //Per attendere che ci porti la pizza il pizzaiolo
	private boolean tavoloOccupato = false; //Per impedire che passino i thread che stanno aspettando fuori
	
	//STRUMENTI DI SINCRONIZZAZIONE
	private ReentrantLock l = new ReentrantLock(); //Lock per la mutua esclusione
	private Condition postoTavolo = l.newCondition(); //Per attendere il tavolo fuori dalla coda
	private Condition pizzaiolo = l.newCondition(); //Per attendere che il pizzaiolo ci dia il via per mangiare
	private Condition clienti = l.newCondition(); //Per attendere che tutti i clienti si siedano
	
	public PizzeriaLC(int nClienti) {
		super(nClienti);
	}//costruttore

	@Override
	boolean isAperto() {
		//Resto aperto finche ci sono clienti da servire
		return clientiServiti < super.CLIENTI_PIZZERIA;
	}//isAperto

	@Override
	void entra() throws InterruptedException {
		l.lock(); //Prendo il lucchetto per la mutua esclusione
		try {
			//Mi inserisco in coda
			Thread cliente = Thread.currentThread();
			codaEntrata.add(cliente);
			System.out.println("[ CLIENTE ]: "+cliente.getName()+" è fuori dalla pizzeria");
			while ( codaEntrata.peek() != cliente || tavoloOccupato ) {
				postoTavolo.await(); //Attendo di essere il primo in coda o che vengano liberati i posti
			}
			codaEntrata.remove(cliente); //Rimuovo il primo della coda per poi far passare quello dietro di lui
			postiTavolo--; //Occupo il tavolo
			System.out.println("[ CLIENTE ]: "+cliente.getName()+" si è seduto al tavolo");
			if ( postiTavolo == 0 ) {
				System.out.println("[ CLIENTE ]: "+cliente.getName()+" si è seduto al tavolo per ultimo");
				tavoloOccupato = true; //Armo il flag per non far passarepiù persono, il tavolo è pieno
				pizzaInForno = true; //Per sbloccare il thread pizzaiolo e bloccare i thread clienti
				clienti.signalAll(); //Segnalo al pizzaiolo che siamo tutti seduti e può inziare a preparare la pizza
			}
			postoTavolo.signalAll(); //Sveglio gli altri per far passare il prossimo
		}finally {
			l.unlock();
		}
	}//entra

	@Override
	void mangiaPizza() throws InterruptedException {
		l.lock();
		try {
			System.out.println("[ CLIENTE ]: "+Thread.currentThread().getName()+" aspetta la pizza");
			while ( pizzaInForno ) { //Attendo che mi chiami il thread pizzaiolo
				pizzaiolo.await();
			}
		}finally {
			l.unlock();
		}
		//Simulo la mangiata della pizza
		TimeUnit.SECONDS.sleep(1);
		l.lock();
		try {
			postiTavolo++;
			if ( postiTavolo == 5 ) {
				tavoloOccupato = false; //Ci siamo alzati tutti dal tavolo
				postoTavolo.signalAll(); //Sveglio chi stava aspettando il tavolo
			}
			clientiServiti++;
			System.out.println("[ CLIENTE ]: "+Thread.currentThread().getName()+" sta uscendo");
			if ( !isAperto() ) {
				System.out.println("[ CLIENTE ]: "+Thread.currentThread().getName()+" ha salutato il pizzaiolo");
				pizzaInForno = true; //Per disarmare il flag al pizzaiolo
				clienti.signalAll(); //Ero l'ultimo cliente ad andare via, quindi saluto il pizzaiolo (lo sveglio se non vado in DEADLOCK)
			}
		}finally {
			l.unlock();
		}
	}//mangiaPizza

	@Override
	void preparaPizza() throws InterruptedException {
		l.lock();
		try {
			while (isAperto() && postiTavolo > 0 || !pizzaInForno ) {
				//Attendo che si siedano tutti oppure che finiscano di mangiare la pizza con !pizzaInForno
				clienti.await();
			}
			if ( !isAperto() ) return; //Sono finiti i clienti
		}finally {
			l.unlock();
		}
		System.out.println("[ PIZZAIOLO ]: "+Thread.currentThread().getName()+" sta preparando la pizza");
		TimeUnit.SECONDS.sleep(2); //Simulo l'infornata della pizza
	}//preparaPizza

	@Override
	void serviPizza() throws InterruptedException {
		l.lock();
		try {
			System.out.println("[ PIZZAIOLO ]: "+Thread.currentThread().getName()+" ha finito di preaparare la pizza");
			pizzaInForno = false; //Per sbloccare i thread clienti e bloccare il pizzaiolo 
			pizzaiolo.signalAll(); //Per far mangiare ai clienti la pizza
		}finally {
			l.unlock();
		}
	}//serviPizza

}//PizzeriaLC
