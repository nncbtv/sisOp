package sisOp.Tracce.AziendaAgricola.Codice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class AziendaAgricolaLock extends AziendaAgricola {

	
	private ReentrantLock lockMagazzino; //Gestisco l'accesso al magazzino
	private Condition attesaInMagazzino; //Condizione su cui dormire se il magazzino è occupato
	private Condition attesaMagazziniere; //Il magazziniere dorme finché non si caricano tutti i sacchi
	private boolean magazzinoOccupato; //Se nel magazzino c'è già un cliente
	private Queue<Thread> codaClientiMagazzino; //Accodo i thread che voglio caricare
	private boolean sacchiBastano; //Finché i sacchi bastano il magazziniere dorme
	
	private ReentrantLock lockCassa; //Gestisco la mutua esclusione per i pagamenti
	private Condition attendiTurnoPagamento; //Condizione su cui dormire per attendere il pagamento
	private Queue<Thread> codaClientiCassa; //Accodo i thread che vogliono pagare
	private boolean cassaOccupata; //Perché si può pagare uno alla volta
	
	public AziendaAgricolaLock() {
		//Inizializzo il magazzino
		lockMagazzino = new ReentrantLock();
		attesaInMagazzino = lockMagazzino.newCondition();
		attesaMagazziniere = lockMagazzino.newCondition();
		magazzinoOccupato = false;
		sacchiBastano = true;
		codaClientiMagazzino = new LinkedList<Thread>();
		//Inizializzo la cassa
		lockCassa = new ReentrantLock();
		attendiTurnoPagamento = lockCassa.newCondition();
		codaClientiCassa = new LinkedList<Thread>();
		cassaOccupata = false;
	}//Costruttore
	
	@Override
	void pagaSacchetti(int nSacchetti) throws InterruptedException {
		lockCassa.lock(); //Arrivo alla cassa e prendo il lucchetto
		try {
			
			Thread corrente = Thread.currentThread(); //Chi ha il lucchetto in mano viene identificato
			codaClientiCassa.add(corrente); //Lo mettiamo in coda
			
			while ( corrente != codaClientiCassa.peek() || cassaOccupata  ) { 
				//Attendo in coda finché non sono il primo e la cassa è occupata
				attendiTurnoPagamento.await();
			}
			
			cassaOccupata = true; //Occupo la cassa per il pagamento
			
			super.saldoFinale = super.saldoFinale + (nSacchetti*super.costoSacco); //Pago
			TimeUnit.SECONDS.sleep(3); //Dormo per 3 secondi per simulare il pagamento, la traccia dice 1 minuto
			System.out.println("Il cliente: "+Thread.currentThread().getName()+" ha pagato: "+(nSacchetti*super.costoSacco)+"€");
			
			codaClientiCassa.poll(); //Lascio il posto in coda
			cassaOccupata = false; //Lascio la cassa libera per andare in magazzino
		
		}finally {
			lockCassa.unlock();
		}
	}//pagaSacchetti

	@Override
	void ritiroSacchetti(int nSacchetti) throws InterruptedException {
		lockMagazzino.lock(); //Cerco di prendere il lock del magazzino
		try {
			Thread corrente = Thread.currentThread();
			codaClientiMagazzino.add(corrente);
			while ( corrente != codaClientiMagazzino.peek() || magazzinoOccupato ) {
				attesaInMagazzino.await();
			}
			magazzinoOccupato = true; //Occupo il magazzino
			
			if ( nSacchetti >= super.sacchiDisponibili ) { //Non lo metto nel while per evitare deadlock
				sacchiBastano = false; //Informo il magazziniere che non mi bastano i sacchi
				attesaMagazziniere.signalAll(); //Il magazziniere deve rifornire i sacchetti
				System.out.println("Il cliente: "+Thread.currentThread().getName()+" ha informato il magazziniere, mancano "+(nSacchetti-super.sacchiDisponibili)+" sacchetti");
				while ( !sacchiBastano ) {
					attesaInMagazzino.await(); //Lascio il comando al magazziniere
				}
				System.out.println("Il magazziniere ha portato i sacchetti mancanti");
			}
			
			super.sacchiDisponibili = super.sacchiDisponibili - nSacchetti; //Prendo i sacchetti
			TimeUnit.SECONDS.sleep(5); //Simulo il caricamento dei sacchetti
			System.out.println("Il cliente: "+Thread.currentThread().getName()+" ha caricato: "+nSacchetti);
			
			magazzinoOccupato = false; //Lascio il magazzino
			codaClientiMagazzino.poll(); //Faccio passare il prossimo
			attesaInMagazzino.signalAll(); //Sveglio tutta la coda passando ccon i miei sacchetti fiammeggianti
		}finally {
			lockMagazzino.unlock();
		}
	}//ritiroSacchetti

	@Override
	void refillMagazzino() throws InterruptedException {
		lockMagazzino.lock();
		try {
			while ( sacchiBastano ) {
				attesaMagazziniere.await();
			}
			super.sacchiDisponibili = 20;
			TimeUnit.SECONDS.sleep(5);
			System.out.println("Il Magazziniere: "+Thread.currentThread().getName()+" ha riempito il magazzino");
			sacchiBastano = true;
			attesaInMagazzino.signalAll();
		}finally {
			lockMagazzino.unlock();
		}
	}//refillMagazzino

}//AziendaAgricolaLock