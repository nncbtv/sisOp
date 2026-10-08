package sisOp.Tracce.CantieriFerroviari.Codice;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;

public class BetterCantiereFerroviarioSem extends CantiereFerroviarioA{

	private int [] lavorazioniSuBinario = new int [super.binariInCostruzione]; //Ogni cella contiene zero/uno ed indica quale tipo di operaio serve
	private boolean [] binarioOccupato = new boolean[super.binariInCostruzione]; //Per garantire che un binario venga occupato da un solo thread
	private int [] traversePosizionate = new int [super.binariInCostruzione]; //Tengo conto delle traverse posizionate per finire un binario
	private volatile boolean orarioLavorativo = true; //Mi serve per arrestare il cantiere, volatile permette agli altri thread di vedere correttamente la modifica fatta da un thread
	private int binariCostruiti = 0; //Serve per tenere conto di quanti altri binari dobbiamo costruire prima di fermarci
	private Map<Thread, Integer> binarioAssociato = new HashMap<>(); //Per associare Thread->Binario
	
	private Semaphore mutex = new Semaphore(1); //Per proteggere i dati condivisi tra i thread
	private Semaphore attendiTurnoRotaia = new Semaphore(0); //Se non ci sono binari con la necessità di una rotaia aspetto, zero permessi
	private Semaphore attendiTurnoTraversa = new Semaphore(0); //Se non ci sono binari con la necessità di una traversa aspetto, qualcuno mi sveglierà
	
	public boolean cantiereAperto() { //Questo metodo mi aiuta ad arrestare i thread oltre che a eseguirli per il tempo necessario alla costruzione
		//Andrebbe modificata anche la classe astratta per essere coerenti con le specifiche della variante dell'esercizio
		return orarioLavorativo; //Il cantiere rimane aperto finche ci sono binari da costruire
	}//cantiereAperto
	
	public BetterCantiereFerroviarioSem(int binariDaCostruire, int lb, int operaiT, int operaiR) {
		super(binariDaCostruire, lb, operaiT, operaiR); //Riporto i dati alla classe astratta
		for (int i = 0; i<super.binariInCostruzione; i++ ) {
			lavorazioniSuBinario[i] = 0; //Ogni binario come lavorazione iniziale deve avere il posizionamento della traversa
			binarioOccupato[i] = false; //All'inizio nessun operaio stava lavorando sui binari
			traversePosizionate[i] = 0; //Nessun binario comincia con delle traverse posizionate
		}
	}//Costruttore

	@Override
	void lavora(int t) {//Trovo il binario su cui posso lavorare, 0 traverse e 1 rotaie
		while ( cantiereAperto() ) {//Il mio thread operaio deve continuare a ciclare se non trova un binario
			try {
				mutex.acquire(); //Prendo il mutex per non andare in race condition sui dati condivisi
				int binarioScelto = -1; //Valore fittizio da aggiornare con il binario scelto
				for ( int i = 0; i<super.binariInCostruzione; i++ ) { //Comincio a scorrere i binari
					if ( !binarioOccupato[i] ) {//Se il binario è occupato salto direttamente al prossimo
						if ( lavorazioniSuBinario[i] == t ) {//Se il binario è libero e serve il mio tipo di operaio posso sceglierlo
							binarioScelto = i; //Lo scelgo
							binarioOccupato[i] = true; //Perché devo lavorarci io soltanto
							binarioAssociato.put(Thread.currentThread(), binarioScelto); //Salvo l'associazione Thread -> Binario
							break; //Non vado avanti a cercare un altro binario
						}
					}
				}//Fine scelta binario
				mutex.release(); //cosi un altro thread può sceglierli il proprio binario
				//Ora devo controllare se ho trovato un binario e uscire da questo metodo
				if ( binarioScelto == -1 ) {//Non ho trovato nessun binario
					//In base al mio tipo di operaio devo attendere che qualcuno mi svegli
					if ( t == 0 ) {
						attendiTurnoTraversa.acquire();
						System.out.println(Thread.currentThread().getName()+" Aspetta per posizionare una traversa");
					}else {
						attendiTurnoRotaia.acquire();
						System.out.println(Thread.currentThread().getName()+" Aspetta per posizionare una rotaia");
					}
				}else {
					break; //Mi serve per interrompere il while esterno che mi fa ciclare di continuo
				}
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
	}//lavora

	@Override
	void termina(int t) {
		//Arrivato a questo punto devo finire il mio lavoro sul binario
		if ( !cantiereAperto() ) return; //Esco dal metodo perché il cantiere è chiuso
		try {
			mutex.acquire(); //Perché devo lavorare sui dati condivisi
			int mioBinario = binarioAssociato.get(Thread.currentThread()); //Prendo il binario su cui stavo lavorando
			if ( t == 0 ) {//Operatore traversa, deve riarmare i suoi flag associati
				System.out.println(Thread.currentThread().getName()+" Ha posizionando una traversa su binario "+mioBinario);
				traversePosizionate[mioBinario] += 1; //Ho appena posizionato una traversa sul binario
				binarioOccupato[mioBinario] = false; //Perché sto lasciando il binario libero
				attendiTurnoRotaia.release(); //Informo che mi serve un operatore per la rotaia
			}else {//Operatore rotaia riarma i suoi flag associati
				System.out.println(Thread.currentThread().getName()+" Ha posizionando una rotaia su binario "+mioBinario);
				if ( traversePosizionate[mioBinario] == super.N_TRAVERSE ) {//Con la mia rotaia questo binario è finito
					binariCostruiti +=1; //Aggiorno il contatore dei binari costruiti
					binarioOccupato[mioBinario] = true; //Non devono più presentarsi operai su questo binario
				}else {
					binarioOccupato[mioBinario] = false; //Se ancora non abbiamo raggiunto la lunghezza necessaria continuiamo a costruire
					attendiTurnoTraversa.release(); //Informo che mi serve un operatore per la traversa
				}
			}
			lavorazioniSuBinario[mioBinario] = (lavorazioniSuBinario[mioBinario]+1)%2; //In questo modo facendo %2 ottengo 0 per le traverse e 1 per le rotaie
			//Ora devo controllare se ci sono ancora binari da costruire
			if ( binariCostruiti == binariInCostruzione ) {//A questo punto possiamo chiudere il cantiere
				orarioLavorativo = false; //Chiusura cantiere
				//Sveglio tutti gli operai in attesa
				attendiTurnoRotaia.release(super.N_OPERAI_ROTAIE);
				attendiTurnoTraversa.release(super.N_OPERAI_TRAVERSE);
				mutex.release(); //Lo lascio per far controllare i dati condivisi agli altri, impedendo deadlock
				return; //Esco dal metodo perché abbiamo finito di costruire
			}
			mutex.release(); //Non devo più toccare dati condivisi
		}catch (Exception e) {
			e.printStackTrace();
		}
	}//termina

}//BetterCantiereFerroviarioSem