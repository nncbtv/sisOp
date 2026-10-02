package sisOp.Tracce.CantieriFerroviari.Codice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class CantiereFerroviarioSem extends CantiereFerroviarioA {
	
	//Modello il binario con un semaforo, ogni semaforo è un binario in costruzione
	//Allo stato iniziale mi serve posizionare su ogni binario una traversa
	boolean [] serveTraversa = new boolean[super.binariInCostruzione];
	boolean [] binarioOccupato = new boolean[super.binariInCostruzione]; //Perché su un binario ci può lavorare solo un operatore
	//Ogni binario verrà costruito in questo modo: traversa,rotaia in modo alternato
	//Permessi per la rotaia e per la traversa
	Semaphore [] posizionaRotaia = new Semaphore[super.binariInCostruzione];
	Semaphore [] posizionaTraversa = new Semaphore[super.binariInCostruzione];
	//Se non ho trovato nessun binario su cui lavorare mi devo mettere in attesa
	Semaphore attendiTurnoTraversa = new Semaphore(0);
	Semaphore attenditurnoRotaia = new Semaphore(0);
	//Mutua esclusione per aggiornare lo stato del binario
	Semaphore mutex = new Semaphore(1);
	
	Map<Thread, Integer> sceltaBinari = new HashMap<Thread, Integer>(); //Per associare un thread ad un binario scelto

	public CantiereFerroviarioSem(int binariDaCostruire) {
		super(binariDaCostruire);
		for ( int i = 0; i<super.binariInCostruzione; i++ ) {
			serveTraversa[i] = true; //Perché come base devo avere le traverse
			binarioOccupato[i] = false;
			posizionaTraversa[i] = new Semaphore(1);
			posizionaRotaia[i] = new Semaphore(0);
		}
	}//costruttore
	
	@Override
	void lavora(int t) { //0 traversa, 1 rotaia
		//Devo cercare un binario in cui serve il mio lavoro
		try {
			int binarioScelto = -1;
			while (binarioScelto == -1 ) {
				mutex.acquire(); //Perché devo proteggere i dati condivisi
				for ( int i = 0; i<super.binariInCostruzione; i++ ) {
					if ( t == 0 ) { //Se sono un operaio delle traverse controllo se serve una traversa
						if ( serveTraversa[i] == true && !(binarioOccupato[i]) ) {
							binarioScelto = i; //Prendo il binario a cui serve la traversa
							serveTraversa[i] = false; //Dopo aver posizionato la traversa necessito della rotaia
							binarioOccupato[i] = true; //Occupo il binario
							break; //Mi fermo al primo binario in cui serve il mio lavoro
						}
					}
					if ( t == 1 ) {//Se sono un operaio delle rotaie controllo se serve una rotaia
						if ( serveTraversa[i] == false && !(binarioOccupato[i]) ) {
							binarioScelto = i; //Prendo il binario a cui serve la rotaia
							serveTraversa[i] = true; //Dopo aver posizionato la rotaia necessito della traversa
							binarioOccupato[i] = true; //Occupo il binario
							break; //Mi fermo al primo binario in cui serve il mio lavoro
						}
					}
				}
				mutex.release(); //Lascio il mutex per consentire agli altri di trovare il loro binario
				//Se non ho trovato un binario a cui serve una traversa mi devo fermare e attendere
				if ( binarioScelto == -1 ) {
					if ( t == 0)
						attendiTurnoTraversa.acquire(); //Lo faccio aspettare finché qualcuno non lo risveglia
					if ( t == 1)
						attenditurnoRotaia.acquire();
				}else {
					mutex.acquire();
					sceltaBinari.put(Thread.currentThread(), binarioScelto);
					mutex.release();
				}
			}//Fine scelta binario
			
			//Una volta scelto il binario svolgo il lavoro
			if ( t == 0 )
				System.out.println(Thread.currentThread().getName()+" Sta posizionando una traversa");
			if ( t == 1 )
				System.out.println(Thread.currentThread().getName()+" Sta posizionando una rotaia");
		}catch (Exception e) {
			e.printStackTrace();
		}
	}//lavora

	@Override
	void termina(int t) {//0 traversa, 1 rotaia
		//Arrivato a questo punto devo aggiornare i flag dicendo che sono andato via e passo il turno al prossimo
		try {
			mutex.acquire(); //Perché devo accedere alla mappa condivisa e modificare i flag condivisi
			int mioBinario = sceltaBinari.get(Thread.currentThread()); //Prendo il numero di binario su cui stavo lavorando
			if ( t == 0 ) {
				//Sono un lavoratore della traaversa
				attenditurnoRotaia.release(); //Avviso chi era in attesa di posizionare una rotaia
				serveTraversa[mioBinario] = false; //Perché l'ho appena posizionata io
				System.out.println(Thread.currentThread().getName()+" Ha posizionando una traversa");
			}
			if ( t == 1 ) {
				attendiTurnoTraversa.release();
				serveTraversa[mioBinario] = true; //Perché ho posizionato una rotaia
				System.out.println(Thread.currentThread().getName()+" Ha posizionando una rotaia");
			}
			binarioOccupato[mioBinario] = false;
			mutex.release();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}//termina

}//cantiereFerroviarioSem