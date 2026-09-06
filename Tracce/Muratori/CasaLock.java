package sisOp.Tracce.Muratori;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CasaLock extends Casa {
	
	private ReentrantLock l = new ReentrantLock(); //Il lock per sincronizzare tutti i thread
	private Condition aspettaTurnoCemento = l.newCondition(); //Condizione su cui farò dormire chi aspetta il turno cemento
	private Condition aspettaTurnoMattoni = l.newCondition(); //Condizione su cui farò dormire chi aspetta il turno mattoni
	//Variabili booleane per la gestione a turni della posa materiali
	private boolean serveCemento = true;
	private boolean serveMattone = false;
	//code in cui inserisco i thread
	private Queue<Thread> codaCemento = new LinkedList<Thread>();
	private Queue<Thread> codaMattoni = new LinkedList<Thread>();
	private boolean svuotaCode = false;
	//Contatori per l'arresto delle lavorazioni
	private int contaStrato = 0;
	private int contaPareti = 0;
	
	public CasaLock(int nStratiPareti, int pareti, int poggiaMattoni, int spargiCemento) {
		super(nStratiPareti, pareti, poggiaMattoni, spargiCemento);
	}//Costruttore

	@Override
	boolean inizia(int operazione) {
		//Per prima cosa dobbiamo capire in quale coda metterci
		l.lock();
		try{
			if ( operazione == 0 ) {//Dobbiamo accodarci sul cemento
				Thread corrente = Thread.currentThread(); //Prendo il thread corrente
				codaCemento.add(corrente); //Lo inserisco nella coda del cemento
				while(codaCemento.peek() != corrente || !serveCemento) { //Finchè non sono il primo in coda devo attendere il mio turno
					aspettaTurnoCemento.await(); //Il lock viene rilasciato automaticamente
				}
				//A questo punto sono stato svegliato da qualcuno che ha finito di posare i mattoni ma devo chiedermi se serve cemento
				if (svuotaCode) { //Se le code si devono svuotare perché abbiamo finito di costruire
					codaCemento.remove(corrente); //Esco dalla coda per poi far passare il prossimo
					aspettaTurnoCemento.signalAll(); //Perché dopo che passo io devo informare tutti gli altri
					return !serveCemento;
				}
				return serveCemento; //Ritorno subito se serve o meno
			}
			if ( operazione == 1 ) {//Stesso procedimento ma sulla coda dei mattoni
				Thread corrente = Thread.currentThread(); //Prendo il thread corrente
				codaMattoni.add(corrente); //Lo inserisco nella coda del cemento
				while(codaMattoni.peek() != corrente || !serveMattone) { //Finchè non sono il primo in coda devo attendere il mio turno
					aspettaTurnoMattoni.await();
				}
				if (svuotaCode) {
					codaMattoni.remove(corrente);
					aspettaTurnoMattoni.signalAll();
					return !serveMattone;
				}
				return serveMattone;
			}
		}catch (Exception e) {
			System.out.println("Errore nella coda delle operazioni");
		}finally {
			l.unlock();
		}
		return false;
	}//inizia

	@Override
	void termina(int operazione) {
		l.lock();
		try {
			//Se arrivo a questo punto significa che devo svolgere le mie operazioni
			if ( operazione == 0 ) {
				//Devo posare il cemento
				System.out.println("Sono "+Thread.currentThread().getName()+" sto preparando il cemento");
				TimeUnit.SECONDS.sleep(1);
				System.out.println("Sono "+Thread.currentThread().getName()+" sto posando il cemento");
				TimeUnit.SECONDS.sleep(1);
				serveCemento = false; //L'ho appena messo quindi non serve cemento
				System.out.println("Sono "+Thread.currentThread().getName()+" mi sto un attimo riprendendo\n");
				TimeUnit.SECONDS.sleep(1);
				contaStrato += 1; //Appena arrivo allo strato prima dell'ultimo strato di mattoni informo
				if ( contaStrato == super.nStratiParete-1 ) {
					System.out.println("\nSono "+Thread.currentThread().getName()+" devo far posare l'ultimo strato di mattoni della parete "+(contaPareti+1)+"\n");
				}
				Thread corrente = Thread.currentThread(); //Prendo il thread corrente
				codaCemento.remove(corrente); //Lascio che il prossimo in coda passi avanti per la prossima posa di cemento
				serveMattone = true; //Informo che necessito di uno strato di mattoni
				//Dopo aver posato il cemento devo svegliare il prossimo thread che mi posiziona i mattoni
				aspettaTurnoMattoni.signalAll(); //Li segnalo tutti tanto passa il primo in coda
			}
			if ( operazione == 1 ) {
				System.out.println("Sono "+Thread.currentThread().getName()+" sto preparando i mattoni");
				TimeUnit.SECONDS.sleep(1);
				System.out.println("Sono "+Thread.currentThread().getName()+" sto posando i mattoni");
				TimeUnit.SECONDS.sleep(1);
				serveMattone = false;
				System.out.println("Sono "+Thread.currentThread().getName()+" mi sto un attimo riprendendo\n");
				TimeUnit.SECONDS.sleep(1);
				contaStrato += 1;
				if ( contaStrato == super.nStratiParete ) {
					System.out.println("\nSono "+Thread.currentThread().getName()+" abbiamo finito la parete "+(contaPareti+1)+"\n");
					contaPareti +=1;
					contaStrato = 0;
				}
				Thread corrente = Thread.currentThread(); //Prendo il thread corrente
				codaMattoni.remove(corrente);
				serveCemento = true;
				if (contaPareti >= super.nPareti ){
					//Altrimenti continuerranno a dormire per via del ciclo while nella condition 
					serveMattone = true;
					//Devo liberare tutti i thread che attendono nelle code
					svuotaCode = true;
					aspettaTurnoCemento.signalAll();
					aspettaTurnoMattoni.signalAll();
				}else {
					aspettaTurnoCemento.signalAll(); //Passerà sempre il primo in coda
				}
			}
		}catch (Exception e) {
			System.out.println("Errore nella posa materiali");
		}finally {
			l.unlock();
		}
	}//termina

}//CasaLock