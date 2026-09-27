package sisOp.Tracce.CallCenter.Codice;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CallCenterLC extends CallCenter {
	//Questa è la prima soluzione che mi è venuta in mente che termina e sembra corretta, spreca però un sacco di spazio, da riprovare con gli id dei thread operatori
	private Queue<Integer> codaFuoriCentralino = new LinkedList<Integer>(); //Coda di attesa FIFO
	private ReentrantLock l = new ReentrantLock();
	private Condition entrataCentralino = l.newCondition();
	private Condition [] gestioneTicket = new Condition[super.numeroClienti]; //Ticket su cui far dormire a turno i due interlocutori
	private boolean [] operatoreAssegnato = new boolean[super.numeroClienti]; //Ogni cliente deve avere assegnato un operatore per andare avanti
	private boolean [] attendiCliente = new boolean[super.numeroClienti]; //Perché poi ogni thread operatore deve aspettare il suo cliente
	private boolean [] attendiOperatore = new boolean[super.numeroClienti]; //Perché ogni thread cliente deve aspettare il suo operatore
	
	private Random r = new Random();

	public CallCenterLC(int N, int M) {
		super(N, M);
		for ( int i = 0; i<super.numeroClienti; i++ ) {
			gestioneTicket[i] = l.newCondition();
			operatoreAssegnato[i] = false;
			attendiCliente[i] = true;
			attendiOperatore[i] = true;
		}
	}//Costruttore

	@Override
	void richiediAssistenza() {
		l.lock();
		try {
			Thread threadCorrente = Thread.currentThread();
			Cliente c = (Cliente)threadCorrente;
			int codiceCliente = c.getCodiceCliente();
			//Arrivato il mio thread cliente lo devo mettere in coda
			codaFuoriCentralino.add(codiceCliente); //Si mette in coda fuori dal centralino, comunicando agli operatori il suo codice
			while ( !operatoreAssegnato[codiceCliente] ) {
				//Dormo finché non mi viene assegnato un operatore
				entrataCentralino.await();
			}
			//Appena un operatore ha preso il mio codice mi sveglia ed entro nel centralino
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			l.unlock();
		}
	}//richiediAssistenza

	@Override
	void fornisciAssistenza() {
		l.lock();
		try {
			//Entrato in questa sezione il mio operatore deve scegliere chi servire dalla coda
			Thread threadCorrente = Thread.currentThread();
			Operatore o = (Operatore) threadCorrente;
			if ( !codaFuoriCentralino.isEmpty() ) {
				o.codiceTicket = codaFuoriCentralino.poll(); //Prendo il primo id in coda, questo ci unisce come worker e cliente
			}
			if (o.codiceTicket == -1){
				System.out.println(o.getNoperatore()+" Non ha trovato clienti da servire");
				o.stop = true; //Questo operatore non riuscira a prendere altri clienti quindi lo fermo
				return; //non ci sono più thread da servire
			}
			System.out.println("Sono l'operatore "+o.getNoperatore()+" sto servendo il cliente "+o.codiceTicket);
			operatoreAssegnato[o.codiceTicket] = true; //Mi assegno al cliente
			entrataCentralino.signalAll(); //Faccio avanzare il cliente per farlo sedere nella mia scrivania
			System.out.println("Sono l'operatore "+o.getNoperatore()+" sto parlando con il cliente "+o.codiceTicket);
			int tempoDialogo = r.nextInt(1,11); //Tempo di discussione
			l.unlock();
			TimeUnit.SECONDS.sleep(tempoDialogo); //Dialogo con il thread cliente
			l.lock();
			//Ora dato che gli ho passato la soluzione devo attendere che la mette in pratica
			attendiOperatore[o.codiceTicket] = false; //Gli comunico che io ho finito
			gestioneTicket[o.codiceTicket].signal(); //Sveglio il cliente in attesa
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			l.unlock();
		}
	}//fornisciAssistenza

	@Override
	void terminaChiamata() {
		//Devo attendere che l'operatore mi dia la soluzione, applicarla e poi sbloccarlo
		l.lock();
		try {
			Thread threadCorrente = Thread.currentThread();
			Cliente c = (Cliente) threadCorrente;
			int codiceTicket = c.getCodiceCliente();
			System.out.println("Sono il cliente "+codiceTicket+" sto ascoltando l'operatore");
			//Attendo che l'operatore mi dia la soluzione
			while ( attendiOperatore[codiceTicket] ) {
				gestioneTicket[codiceTicket].await(); //Mi metto in attesa per dialogare con l'operatore
			}
			//Arrivato a questo punto l'operatore sta dormendo e aspetta me che faccia le operazioni e che poi lo libero
			int tempoOperazioni = r.nextInt(2,7);
			l.unlock();
			TimeUnit.SECONDS.sleep(tempoOperazioni); //Simulo il tempo delle operazioni
			l.lock();
			//A questo punto non mi resta che svegliare il mio operatore
			attendiCliente[codiceTicket] = false;
			gestioneTicket[codiceTicket].signal();
			super.numeroClienti -= 1;
			System.out.println("Sono il cliente "+codiceTicket+" problematica risolta");
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			l.unlock();
		}
	}//terminaChiamata

	@Override
	void prossimoCliente() {
		l.lock();
		try {
			//Qui prima di passare al prossimo cliente devo controllare se posso fare la pausa
			Thread threadCorrente = Thread.currentThread();
			Operatore o = (Operatore) threadCorrente;
			while ( attendiCliente[o.codiceTicket] ) { //Mi metto in attesa perché deve svolgere lui le operazioni
				gestioneTicket[o.codiceTicket].await();
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			l.unlock();
		}
	}//prossimoCliente

}//CallCenter