package sisOp.Tracce.CantieriFerroviari.Codice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class CantiereFerroviarioSem extends CantiereFerroviarioA {

	//Ogni binario verrà costruito in questo modo: traversa,rotaia in modo alternato sul vettore serveTraversa
	boolean [] serveTraversa = new boolean[super.binariInCostruzione];
	boolean [] binarioOccupato = new boolean[super.binariInCostruzione]; //Perché su un binario ci può lavorare solo un operatore
	int [] traversePosizionate = new int[super.binariInCostruzione]; //Contatore che mi serve per bloccare le traverse su un binario
    int binariCostruiti = 0; //Mi devo fermare appena finisco i binari da costruire

	//Se non ho trovato nessun binario su cui lavorare mi devo mettere in attesa e qualcuno mi deve dare il via libera
	Semaphore attendiTurnoTraversa = new Semaphore(0); //Devo attendere che qualcuno mi svegli dopo aver posizionato una rotaia
	Semaphore attenditurnoRotaia = new Semaphore(0); //Devo attendere che qualucno mi svegli dopo aver posizionato una traversa

	//Mutua esclusione per aggiornare lo stato del binario e trattare i dati condivisi
	Semaphore mutex = new Semaphore(1);

	Map<Thread, Integer> sceltaBinari = new HashMap<Thread, Integer>(); //Per associare un thread ad un binario scelto

	public CantiereFerroviarioSem(int binariDaCostruire, int lb) {
		super(binariDaCostruire, lb); //Riporto i dati alla classe astratta
		for ( int i = 0; i<super.binariInCostruzione; i++ ) { //Armo i flag
			serveTraversa[i] = true; //Perché come base devo avere le traverse
			binarioOccupato[i] = false; //Inizialmente nessun operaio stava lavorando sui binari
			traversePosizionate[i] = 0; //Quante traverse sono ste posizionate sul binario
		}
	}//costruttore

	@Override
	void lavora(int t) { //0 traversa, 1 rotaia
		//Devo cercare un binario in cui serve il mio lavoro
		try {
			int binarioScelto = -1; //Armo il flag a -1 per indicare che non ho trovato binario su cui lavorare
			while (binarioScelto == -1 ) { //Continuo a cercare finché non ne trovo uno
			    if ( binariCostruiti == super.binariInCostruzione ){
			        Operaio o = (Operaio) Thread.currentThread();
			        o.turnoLavoro = false; //Imposto l'operaio a false per farlo finire di lavorare
			        return;
			    }
				mutex.acquire(); //Perché devo proteggere i dati condivisi
				for ( int i = 0; i<super.binariInCostruzione; i++ ) { //Comincio a scorrere tutti i binari
					if ( t == 0 ) { //Se sono un operaio delle traverse controllo se serve una traversa
						if ( serveTraversa[i] == true && !(binarioOccupato[i]) ) { //Il binario deve chiedere una traversa e deve essere libero
							if ( traversePosizionate[i] == super.N_TRAVERSE ){ //Significa che in questo binario non devo continuare a costruire
							    continue; //Passo al prossimo binario
							}
							binarioScelto = i; //Prendo il binario a cui serve la traversa e salvo il suo numero
							serveTraversa[i] = false; //Dopo aver posizionato la traversa necessito della rotaia, aggiorno il flag
							binarioOccupato[i] = true; //Occupo il binario, aggiorno il flag
							break; //Mi fermo al primo binario in cui serve il mio lavoro, inutile cercare tutti gli altri
						}
					}
					if ( t == 1 ) {//Se sono un operaio delle rotaie controllo se serve una rotaia
						if ( serveTraversa[i] == false && !(binarioOccupato[i]) ) { //Il binario deve chiedere una traversa e deve essere libero
							binarioScelto = i; //Prendo il binario a cui serve la rotaia
							serveTraversa[i] = true; //Dopo aver posizionato la rotaia necessito della traversa, aggiorno il flag
							binarioOccupato[i] = true; //Occupo il binario, aggiorno il flag
							break; //Mi fermo al primo binario in cui serve il mio lavoro, inutile cercare su tutti gli altri
						}
					}
				}
				mutex.release(); //Lascio il mutex per consentire agli altri di trovare il loro binario e lavorare sui dati condivisi
				//Se non ho trovato un binario a cui serve una traversa mi devo fermare e attendere che qualcuno mi chiami
				if ( binarioScelto == -1 ) {
					if ( t == 0) {
						attendiTurnoTraversa.acquire(); //Lo faccio aspettare finché qualcuno non lo risveglia, il semaforo è inizializzato a zero permessi
						System.out.println(Thread.currentThread().getName()+" Aspetta	 per posizionare una traversa");
					}
					if ( t == 1) {
						attenditurnoRotaia.acquire(); //Lo faccio aspettare finché qualcuno non lo risveglia, il semaforo è inizializzato a zero permessi
						System.out.println(Thread.currentThread().getName()+" Aspetta per posizionare una rotaia");
					}
				} else {
					mutex.acquire(); //Appena posso scrivere sulla mappa aggancio il thread al binario scelto
					//Mi conviene lasciarlo cosi gli altri possono cercare i binari, il tutto è più veloce
					sceltaBinari.put(Thread.currentThread(), binarioScelto);
					mutex.release(); //Rilascio perché non devo più toccare dati condivisi
				}
			}//Fine scelta binario
			//Una volta scelto il binario svolgo il lavoro
			if ( t == 0 )
				System.out.println(Thread.currentThread().getName()+" Sta posizionando una traversa su binario "+binarioScelto);
			if ( t == 1 )
				System.out.println(Thread.currentThread().getName()+" Sta posizionando una rotaia su binario "+binarioScelto);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//lavora

	@Override
	void termina(int t) {//0 traversa, 1 rotaia
		//Arrivato a questo punto devo riarmare i flag dicendo che sono andato via e passo il turno al prossimo
		try {
		    if ( binariCostruiti == super.binariInCostruzione ){ //Controllo se sono stati costruiti tutti i binari
			        Operaio o = (Operaio) Thread.currentThread();
			        o.turnoLavoro = false; //Imposto l'operaio a false per farlo finire di lavorare
			        return;
			    }
			mutex.acquire(); //Perché devo accedere alla mappa condivisa e modificare i flag condivisi
			int mioBinario = sceltaBinari.get(Thread.currentThread()); //Prendo il numero di binario su cui stavo lavorando
			if ( t == 0 ) {//Sono un lavoratore della traversa
				binarioOccupato[mioBinario] = false; //Libero il binario, subito perché qualcuno potrebbe volerci accedere
				serveTraversa[mioBinario] = false; //Perché l'ho appena posizionata io
				traversePosizionate[mioBinario] += 1; //Aggiorno il contatore delle traverse
				attenditurnoRotaia.release(); //Avviso chi era in attesa di posizionare una rotaia
				System.out.println(Thread.currentThread().getName()+" Ha posizionato una traversa sul binario "+mioBinario);
			}
			if ( t == 1 ) {//Stessa logica di prima sull'operaio rotaia
				binarioOccupato[mioBinario] = false;
				serveTraversa[mioBinario] = true;
				if ( traversePosizionate[mioBinario] == super.N_TRAVERSE ){//Dopo aver posizionato la rotaia questo binario è completato
				    binariCostruiti += 1;
				}
				attendiTurnoTraversa.release();
				System.out.println(Thread.currentThread().getName()+" Ha posizionato una rotaia sul binario "+mioBinario);
			}
			mutex.release(); //Fine riarmo, fine segnali, fine modifica dati condivisi
		} catch (Exception e) {
			e.printStackTrace();
		}
	}//termina

}//cantiereFerroviarioSem