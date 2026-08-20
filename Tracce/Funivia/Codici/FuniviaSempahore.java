package sisOp.Tracce.Funivia.Codici;

import java.util.concurrent.Semaphore;

public class FuniviaSempahore extends Funivia {
	
	private Semaphore salitaPersone;
	private Semaphore salitaCiclisti;
	private Semaphore partenzaFunivia;
	private Semaphore mutex;
	private Boolean corsaPedoni = true; //Inizio la prima corsa con i pedoni
	
	public FuniviaSempahore() {
		salitaPersone = new Semaphore(6); //possono salire 6 persone
		salitaCiclisti = new Semaphore(3); //possono salire al massimo 3 ciclisti
		partenzaFunivia = new Semaphore(0);
		mutex = new Semaphore(1);
	}//FuniviaSemaphore

	@Override
	void pilotaStart() {
		//Questo metodo fa salire i passeggeri sulla funivia, una volta salito un tipo di passeggeri continuo con quello
		try {
			//Devo attendere che tutti salgano sulla funivia e appena è piena blocco la salita e parto
			System.out.println("Attendo che i passeggeri salgano");
			partenzaFunivia.acquire();
			System.out.println("Tutti i passeggeri sono saliti, partiamo");
		}catch (Exception e) {
			System.out.println("Errore nella salita passeggeri");
		}
	}//pilotaStart

	@Override
	void pilotaEnd() {
		//Questo metodo fa scendere i passeggeri e imposta il nuovo tipo di corsa
		try {
			partenzaFunivia.acquire();
			System.out.print("Sono scesi tutti, ho trasportato: ");
			if (corsaPedoni) {
				System.out.println("pedoni");
			}else {
				System.out.println("Ciclisti");
			}
			corsaPedoni = !corsaPedoni; //Cambio tipo di corsa
		}catch (Exception e) {
			System.out.println("Errore nela discesa passeggeri");
		}
	}//pilotaEnd

	@Override
	void turistaSali(int tipoTurista) {
		try {
			if ( tipoTurista == 0 && corsaPedoni ) { //Se sono un pedone e la corsa è per me salgo
				salitaPersone.acquire(); //Questo per forza mi darà 6 thread qui dentro
				mutex.acquire();
				System.out.println("Sono: "+Thread.currentThread().getName()+ ", pedone e sto salendo");
				//Controllo se ci sono ancora posti a sedere, se sono stato l'ultimo a salire informo il pilota per farlo partire
				if (salitaPersone.availablePermits() == 0) {
					partenzaFunivia.release(); //Sveglio il pilota
				}
				mutex.release();
			}
			if ( tipoTurista == 1 && !corsaPedoni ) { //Se sono un ciclista e la corsa è per me salgo
				salitaCiclisti.acquire();
				mutex.acquire();
				System.out.println("Sono: "+Thread.currentThread().getName()+ ", ciclista e sto salendo");
				if (salitaCiclisti.availablePermits() == 0) {
					partenzaFunivia.release(); //Sveglio il pilota
				}
				mutex.release();
			}
		}catch (Exception e) {
			System.out.println("Errore posto funivia");
		}
	}//TuristaSali

	@Override
	void turistaScendi(int tipoTurista) {
		if ( tipoTurista == 0 && corsaPedoni) { //Se sono un pedone e la corsa è per me salgo
			salitaPersone.release();
			System.out.println("Sono: "+Thread.currentThread().getName()+ ", pedone e sto scendendo");
		}
		if ( tipoTurista == 1 && !corsaPedoni) { //Se sono un ciclista e la corsa è per me salgo
			salitaCiclisti.release();
			System.out.println("Sono: "+Thread.currentThread().getName()+ ", ciclista e sto scendendo");
		}
	}//turistaScendi

}//FuniviaSemaphore