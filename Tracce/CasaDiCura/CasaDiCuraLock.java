package sisOp.Tracce.CasaDiCura;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class CasaDiCuraLock extends CasaDiCura {
	
	private ReentrantLock l; //Questo lock mi rappresenta la gestione della casa di cura
	private Condition attendiPaziente; //Questa condizione mi permette di far attendere il paziente al medico
	private boolean pazienteEntrato = false; //Mi serve per capire se la sala visite è occupata
	private boolean operazioneInCorso = false; //Mi serve per tenere fermi i clienti mentre li visito
	private Condition attendiSalaAttesa; //I pazienti attenderanno in sala di attesa
	private int personeInSala = 0; //massimo 3 persone in sala altrimenti aspettano
	private Condition attendiSalaVisite; //I pazienti si spostano nella sala visite
	private Condition attendiOperzione; //I pazienti aspettano che il medico finisca di visitarli
	private Queue<Thread> codaVisite; //Devo gestire una coda FIFO per l'entrata nella sala delle visite
	private Random r = new Random(); //Mi serve per generare le attese
	
	
	public CasaDiCuraLock(int nPazienti) {
		super(nPazienti); //Rimando al costruttore della superclasse
		l = new ReentrantLock(); //Inizializzo il lucchetto
		attendiPaziente = l.newCondition(); //Il medico attende su questa condition il paziente per la sala visite
		attendiSalaAttesa = l.newCondition(); //I pazienti aspetterano qui se la sala di attessa contiene già 3 persone
		attendiSalaVisite = l.newCondition(); //I pazienti verrano gestiti in ordine FIFO da questa coda per venire visitati
		attendiOperzione = l.newCondition(); //I pazienti aspetteranno che il medico finisca la visita per andare via
		codaVisite = new LinkedList<>(); //I pazienti verranno messi in coda per l'operazione
	}//Costruttore

	@Override
	void chiamaEIniziaOperazione() {
		//Per prima cosa dobbiamo informare i pazienti che la nostra sala visite è pronta
		l.lock(); //Prendo il lucchetto
		try {
			//Segnaliamo che nella sala visite c'è posto
			attendiSalaVisite.signalAll(); //La gestione FIFO verrà fatta al risveglio dei thread
			//Ora devo aspettare che il paziente entri in sala per poi visitarlo
			System.out.println("Il medico è pronto");
			while (!pazienteEntrato) { //Lo ciclo nel while per evitare risvegli strani del SO
				attendiPaziente.await(); //Non mi sveglio finche non mi chiama qualcuno
			}
			System.out.println("Il medico sta visitando");
			int tempoOperazione = r.nextInt(2, 5);
			TimeUnit.SECONDS.sleep(tempoOperazione); //simulo l'operazione
			//Dopo che mi hanno svelgliato passo a visitare il paziente
		} catch (InterruptedException e) {
			System.out.println("Errore nell'avvio visita");
		} finally {
			l.unlock(); //rilascio il lucchetto
		}
	}//chiamaEIniziaOperazione

	@Override
	void fineOperazione() {
		l.lock();
		try {
			//Devo lasciare che il paziente vada via
			operazioneInCorso = false; //Perché ho finito di operare
			attendiOperzione.signalAll(); //Segnalo che si può alzare, anche se è uno io segnalo come se fossero molti
			System.out.println("Il medico ha finito la visita");
			TimeUnit.SECONDS.sleep(2); //Simulo la sistemazione della sala per il prossimo paziente
		} catch (InterruptedException e) {
			System.out.println("Errore nel fine visita");
		}finally {
			l.unlock();
		}
	}//fineOperazione

	@Override
	void pazienteEntra() {
		//In questo punto il cliente deve poter accodarsi prima di tutto in sala di attesa e poi nella coda delle operazioni
		l.lock(); //Prendo il lucchetto per capire cosa devo fare
		try {
			while ( personeInSala > 2 ) { //Se più di 3 persone sono in sala di attesa mi fermo 
				attendiSalaAttesa.await();
			}
			//Entro nella sala di attesa
			System.out.println("Il paziente: "+Thread.currentThread().getName()+ " è entrato in sala di attesa");
			personeInSala = personeInSala + 1; //Comunico che sono in sala
			Thread corrente = Thread.currentThread(); //Prendo il thread corrente
			codaVisite.add(corrente); //Lo metto in coda
			while (codaVisite.peek() != corrente) { //Se non sono il primo della coda mi metto in attesa
				attendiSalaVisite.await();
			}
			//Se sono passato informo il medico di essere entrato
			pazienteEntrato = true; //Entro in sala
			operazioneInCorso = true; //Perché mi sta operando
			System.out.println("Il paziente: "+Thread.currentThread().getName()+ " è entrato in sala visite");
			attendiPaziente.signalAll(); //Sveglio il medico 
		} catch (InterruptedException e) {
			System.out.println("Errore nelll'inserimento coda");
		}finally {
			l.unlock();
		}

	}//pazienteEntra

	@Override
	void pazienteEsci() {
		//Devo uscire dalla coda e fa passare avanti un altra persona
		l.lock();
		try {
			while (operazioneInCorso) //Finchè il medico mi sta visitando devo attendere
				attendiOperzione.await();
			codaVisite.remove(Thread.currentThread()); //Esco dalla coda per far passare il prossimo in sala visite appena la sala è libera
			pazienteEntrato = false; //La  sala delle visite non ha pazienti al suo interno
			attendiSalaVisite.signalAll(); //Segnalo la mia uscita agli altri dietro di me
			personeInSala= personeInSala - 1; //Indico che c'è un posto in più nella sala di attesa perché un altro è entrato in sala visite
			attendiSalaAttesa.signalAll(); //Segnalo il posto a chi è fuori dalla 
			System.out.println("Il paziente: "+Thread.currentThread().getName()+ " è uscito dalla sala visite");
		}catch (Exception e) {
			System.out.println("Errore nell'uscita dalla visita");
		}finally {
			l.unlock();
		}
	}//pazienteEsci

}//CasaDiCuraLock