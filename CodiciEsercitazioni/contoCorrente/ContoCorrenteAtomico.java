package sisOp.contoCorrente;

import java.util.concurrent.atomic.AtomicInteger;

//SINCRONISMO CON ISTRUZIONI ATOMICHE
public class ContoCorrenteAtomico extends ContoCorrente{
	
	private AtomicInteger saldoAtomico; //Creo una varibile atomica che andranno a modificare i Thread così da non toccare l'originale
	
	/*
	 *IN QUESTO CASO LA VARIBILE ATOMICA VIENE MODIFICATA IN MODO UNIVOCO DA OGNI THREAD POICHÉ È DEFINITA IN MODO ATOMICO. 
	 *NON È POSSIBILE FARE INTERLEAVING
	 */
	
	public ContoCorrenteAtomico(int depositoIniziale) {
		super(depositoIniziale); //Passo il saldo iniziale alla classe astratta per il controllo
		this.saldoAtomico = new AtomicInteger(depositoIniziale); //Istanzio la variabile atomica su cui lavoreranno i thread
	}//Costruttore

	@Override
	public void preleva(int importo) {
		if ( importo < 0 ) throw new RuntimeException("Impossibile prelevare un numero negativo!");
		else {
			saldoAtomico.addAndGet(-importo); //Atomicamente prendo il valore e sottraggo l'importo da prelevare
		}
	}//preleva

	@Override
	public void deposita(int importo) {
		if ( importo < 0 ) throw new RuntimeException("Impossibile depositare un numero negativo!");
		else {
			saldoAtomico.addAndGet(importo); //Atomicamente prendo il valore e aggiungo l'importo da prelevare
		}
	}//deposita

	@Override
	public int getSaldo() {
		return saldoAtomico.get();
	}//Ritorno il saldo atomico senza toccare le variabili condivise ma lavorando sulla copia atomica
	
	
}//ContoCorrenteAtomico