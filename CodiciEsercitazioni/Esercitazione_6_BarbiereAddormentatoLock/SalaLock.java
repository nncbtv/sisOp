package sisOp.CodiciEsercitazioni.Esercitazione_6_BarbiereAddormentatoLock;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import sisOp.CodiciEsercitazioni.Esercitazione_5_BarbiereAddormentato.Cliente;

public class SalaLock extends sisOp.CodiciEsercitazioni.Esercitazione_5_BarbiereAddormentato.Sala {

	private Lock l = new ReentrantLock();
	private Condition attendiTaglio = l.newCondition();
	private Condition barbiereDorme = l.newCondition();
	protected boolean poltronaLibera = true;
	protected List<Cliente> codaClienti = new ArrayList<>();

	public SalaLock(int posti) {
		super(posti);

	}//costruttore

	@Override
	public void tagliaCapelli() throws InterruptedException {
		l.lock();
		try {
			while ( sedie == sedieLibere ) { //Se non c'è nessuno in sala faccio dormire il barbiere
				barbiereDorme.await();
			}
			TimeUnit.SECONDS.sleep(5); //Simulo il taglio di capelli con un attesa
			poltronaLibera = true; //Segnalo che la poltrona è di nuovo libera
			attendiTaglio.signalAll(); //Sveglio tutti i thread che devono tagliare i capelli
			//Il while nell' attendiTaglio() controlla se è  il loro turno altrimenti dormono di nuovo
		} finally {
			l.unlock();
		}
	}//tagliaCapelli

	@Override
	public boolean attendiTaglio() throws InterruptedException {
		l.lock();
		Cliente c = (Cliente)Thread.currentThread();
		try {
			//Il rilascio del lucchetto viene fatto dal finally anche in caso di return
			if ( sedieLibere == 0 ) {
				return false;
			}
			codaClienti.addLast(c); //Mi metto in coda
			sedieLibere--; //Mi siedo su una sedia della sala
			barbiereDorme.signal(); //Sveglio il barbiere che dorme perché è arrivato un cliente
			while ( !mioTurno(c.getID()) ) {
				//Se non è il mio turno ad essere servito dormo
				attendiTaglio.await(); //Aspetto il taglio
			}
			codaClienti.removeFirst(); //Mi alzo dalla coda Perché è il mio turno
			//Prima occupo la poltrona poi rilascio l'altra per non farmela prendere dagli altri
			poltronaLibera = false; //Occupo la poltrona
			sedieLibere++; // Libero la sedia in sala
			return true;
		} finally {
			l.unlock();
		}
	}//attendiTaglio

	private boolean mioTurno(int i) {//Controllo se è il mio turno
		return codaClienti.getFirst().getID() == i && poltronaLibera;
	}//mioTurno

}//SalaLock