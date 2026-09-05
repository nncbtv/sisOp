package sisOp.Tracce.CasaDiCura;

import java.util.Random;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class CasaDiCuraSemaphore extends CasaDiCura {
	
	private Semaphore salaPreparazione; //Questo semaforo mi gestisce le entrate nella sala di preparazione
	private Semaphore salaOperazione; //Questo semaforo mi gestisce la sala operazione
	private Semaphore attendiOperazione; //Mi serve per non fare uscire dalla sala operatoria i pazienti
	private Random r; //Mi serve per generare numeri randomici per simulare le azioni tramite le attese
	private Semaphore attendiPaziente; //Il medico deve attendere un paziente che entri nella sala operatoria
	
	public CasaDiCuraSemaphore(int numeroPazienti) {
		super(numeroPazienti); //Uso il costruttore della classe da cui eredito
		salaPreparazione = new Semaphore(3); //Possono entrare al massimo 3 pazienti per volta
		salaOperazione = new Semaphore(0, true); //Gestista dal medico in ordine FIFO
		attendiOperazione = new Semaphore(0); //Perché un paziente non può andare via mentre sta venendo operato
		r = new Random(); //Generatore di numeri casuali per le attese
		attendiPaziente = new Semaphore(0); //Rendo il programma più deterministico possibile
	}//Costruttore che rimanda al superiore
	

	@Override
	void chiamaEIniziaOperazione() {
		//Il medico chiama un nuovo paziente ed attende che arrivi in sala operatoria per iniziare l'operazione
		//L'operazione dura dai 20 ai 40 minuti
		try {
			//Per prima cosa devo informare un paziente che l'operazione può cominciare
			salaOperazione.release(); //La sala operatoria è pronta
			System.out.println("Il medico è pronto");
			attendiPaziente.acquire(); //Aspetta il paziente per poter cominciare ad operare
			//Comincio a svolgere l'operazione
			System.out.println("Il medico sta visitando");
			int tempoOperazione = r.nextInt(2, 5);
			TimeUnit.SECONDS.sleep(tempoOperazione); //simulo l'operazione
		}catch (Exception e) {
			System.out.println("Errore nell'inzio delle operazioni");
		}
	}//chiamaEIniziaOperazione

	@Override
	void fineOperazione() {
		//Il medico segnala la fine dell'operazione al cliente, impiega 20 minuti a preparare la sala per una nuova operazione
		try {
			attendiOperazione.release(); //Rilascio il thread che stavo operando
			System.out.println("Il medico ha finito la visita");
			TimeUnit.SECONDS.sleep(2); //Simulo la preparazione della sala
		}catch (Exception e) {
			System.out.println("Errore nella conclusione dell'operazione");
		}
	}//fineOperazione

	@Override
	void pazienteEntra() {
		//Il paziente entra e aspetta nella sala di preparazione, al massimo 3
		//Nella sala di preparazione aspetta di essere chiamato in sala operatoria dal medico
		try {
			//Per prima cosa devo cercare di entrare in sala di preparazione
			salaPreparazione.acquire(); //Una volta entrato in sala di preparazione attendo il mio turno in sala operatoria
			System.out.println("Il paziente: "+Thread.currentThread().getName()+ " è entrato in sala di attesa");
			salaOperazione.acquire(); //Questo semaforo viene gestito in oridne FIFO
			System.out.println("Il paziente: "+Thread.currentThread().getName()+ " è entrato in sala visite");
			attendiPaziente.release(); //Mi presento al medico
		}catch (Exception e) {
			System.out.println("Errore nell'entrata del paziente");
		}
	}//pazienteEntra

	@Override
	void pazienteEsci() {
		//Abbandono la sala di operazione e libero un posto nella sala di preparazione per un altro cliente
		try{
			attendiOperazione.acquire(); //Attendo che il medico mi dia il via per potermi alzare dall'operazione
			salaPreparazione.release(); //Rilascio un posto nella sala di preparazione
			System.out.println("Il paziente: "+Thread.currentThread().getName()+ " è uscito dalla sala visite");
		}catch (Exception e) {
			System.out.println("Errore nell'uscita del paziente");
		}
	}//pazienteEsci

}//CasaDiCuraSemaphore