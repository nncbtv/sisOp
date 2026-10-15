package sisOp.Tracce.Muratori;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CasaSemaphore extends Casa {

	private int contaStrato = 0; //per tenere conto a quale strato sono arrivato
	private int contaPareti = 0; //per tenere conto delle pareti da costruire
	private Semaphore stratoCemento = new Semaphore(1, true); //Devo tenere la coda in FIFO di chi mi sparge il cemento
	private Semaphore stratoMattone = new Semaphore(0, true); //Devo tenere la coda in FIFO di chi mi poggia i mattoni
	private boolean serveMattone = false; //Se mi serve uno strato di mattone
	private boolean serveCemento = true;

	public CasaSemaphore( int nStratiPareti, int pareti, int poggiaMattoni, int spargiCemento ) {
		super(nStratiPareti, pareti, poggiaMattoni, spargiCemento);
	}//costruttore

	@Override
	boolean inizia(int operazione) {
		//Cominciamo a capire di che tipo di operazione necessitiamo, 0 cemento e 1 mattoni
		try {
			//Prima di tutto capiamo se dobbiamo ancora costruire pareti
			if ( operazione == 0 ) {
				stratoCemento.acquire(); //Mi metto in coda per il cemento
				if (serveCemento) {
					return true; //Una volta svegliato controllo se serve ancora posiziona il cemento
				}
			}
			if ( operazione == 1 ) {
				stratoMattone.acquire();
				if (serveMattone) {
					return true; //Una volta svegliato controllo se serve il mattone
				}
			}
		}catch (Exception e) {
			System.out.print("Errore nella scelta del materiale da poggiare");
		}
		return false;
	}//inizia

	@Override
	void termina(int operazione) {
		try {
			if ( operazione == 0 ) {
				System.out.println("Sono "+Thread.currentThread().getName()+" sto preparando il cemento");
				TimeUnit.SECONDS.sleep(1);
				System.out.println("Sono "+Thread.currentThread().getName()+" sto posando il cemento");
				TimeUnit.SECONDS.sleep(1);
				serveCemento = false; //L'ho appena messo quindi non serve cemento
				System.out.println("Sono "+Thread.currentThread().getName()+" mi sto un attimo riprendendo\n");
				TimeUnit.SECONDS.sleep(1);
				contaStrato += 1;
				if ( contaStrato == super.nStratiParete-1 ) {
					System.out.println("\nSono "+Thread.currentThread().getName()+" devo far posare l'ultimo strato di mattoni della parete "+(contaPareti+1)+"\n");
				}
				serveMattone = true; //Informo che necessito di uno strato di mattoni
				stratoMattone.release(); //Cedo il posto a chi deve poggiare il mattone
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
				serveCemento = true;
				if (contaPareti >= super.nPareti ){
					serveCemento = false;
					stratoCemento.release(spargiCemento); //Libero tutti i thread che attendono
					stratoMattone.release(poggiaMattoni);
				}else {
					stratoCemento.release();
				}
			}
		}catch (Exception e) {
			System.out.println("Errore posa materiali");
		}
	}//termina

}//CasaSemaphore