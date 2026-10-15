package sisOp.Tracce.CockTailBar.Codice;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

public class BarSem extends Bar {

	private Semaphore aspettaOrdinazioni = new Semaphore(0); //Il barista attende le comande di tutti i clienti prima di iniziare
	private Semaphore aspettaBarista = new Semaphore(0); //Quando devo attendere il cocktail dal barista
	private Semaphore mutex = new Semaphore(1); //Semaforo per dare le comande uno alla volta
	private Semaphore pagaCassa = new Semaphore(1, true); //Per pagare alla cassa con ordine FIFO
	private int nCocktailNormali = 0; //Per capire quante ordinazioni sono state fatte di questo tipo
	private int nCocktailSpeciali = 0;

	public BarSem(int numeroClienti) {
		super(numeroClienti); //Rimando il numero di clienti all'oggetto condiviso astratto
	}//costruttore

	@Override
	void ordinaCocktail(int tipo) {
		//Il cliente deve poter ordinare il cocktail del suo tipo, passandolo al barman
		try {
			mutex.acquire(); //Prendo il mutex per passare la comanda al barista
			Thread clienteCorrente = Thread.currentThread(); //Prendo il thread che è davanti al bancone
			System.out.println(clienteCorrente.getName()+" sta passando l'ordinazione al barista"); //Stampa a video per debug
			if ( tipo == 0 ) {
				nCocktailNormali += 1; //Se voglio un coktail normale incremento il contaore
			}
			if ( tipo == 1 ) {
				nCocktailSpeciali += 1;
			}
			aspettaOrdinazioni.release(); //Faccio una release sul semaforo per indicare che la mia ordinazione è stata presa
			mutex.release(); //Faccio ordinare al prossimo della comitiva
		} catch (Exception e) {
			System.out.println("Erroe nell'ordinazione dei cocktail");
		}
	}//ordinaCocktail

	@Override
	void preparaCocktail() {
		//Una volta raccolti tutti i cocktail dei clienti li prepara tutti e poi li serve
		try {
			aspettaOrdinazioni.acquire(super.nClienti); //Aspetto finchè non hanno ordinato tutti i clienti
			//Comincio a preparare i cocktail normali
			while ( nCocktailNormali > 0 ) {
				System.out.println("Sto preparando un cocktail normale");
				TimeUnit.SECONDS.sleep(1); //Simulazione della preparazione
				super.clientiServiti += 1; //Aggiungo il cliente servito
				nCocktailNormali -= 1; //Decremento il contatore
			}
			while ( nCocktailSpeciali > 0 ) {
				System.out.println("Sto preparando un cocktail speciale");
				TimeUnit.SECONDS.sleep(1); //Simulazione della preparazione
				super.clientiServiti += 1; //Aggiungo il cliente servito
				nCocktailSpeciali -= 1; //Decremento il contatore
			}
			aspettaBarista.release(super.nClienti); //Informo i clienti che i loro cocktail sono pronti
		} catch (Exception e) {
			System.out.println("Errore nella preparazione dei cocktail");
		}
	}//preparaCocktail

	@Override
	void beviEpaga(int tipo) {
		//Il cliente attende che il suo cocktail sia pronto e dopodichè si dirige alla coda FIFO della cassa per pagare
		try {
			aspettaBarista.acquire(); //Devo attendere che il barista mi porti il drink
			Thread clienteCorrente = Thread.currentThread(); //Prendo il thread che è davanti al bancone
			System.out.println(clienteCorrente.getName()+" sta bevendo il drink"); //Stampa a video per debug
			TimeUnit.SECONDS.sleep(1);
			System.out.println(clienteCorrente.getName()+" si sta dirigendo alla cassa"); //Stampa a video per debug
			pagaCassa.acquire(); //Prendoi il semaforo per pagare, esso gestisce la coda in ordine FIFO
			mutex.acquire(); //Prendo il mutex per aggiornare l'incasso
			if ( tipo == 0 ) {
				super.incassoFinale += 6; //Pago 6 denari
			}
			if ( tipo == 1 ) {
				super.incassoFinale += 8; //Pago 8 denari
			}
			System.out.println(clienteCorrente.getName()+" ha pagato"); //Stampa a video per debug
			aspettaOrdinazioni.release(); //Riutilizzo il semaforo per capire quando tutti i clienti hanno pagato
			mutex.release(); //Rilascio il mutex
			pagaCassa.release(); //Faccio passare avanti un altro per pagare
		}catch (Exception e) {
			System.out.println("Errore nella consumazione o nel pagamento");
		}
	}//beciEpaga

	@Override
	void chiudiBar() {
		//Attendo che tutti i clienti paghino per poi chiudere il baretto
		try {
			aspettaOrdinazioni.acquire(super.nClienti); //Aspetto che i clienti finiscano di pagare
			System.out.println("\nIl Semaforo, incasso: "+super.incassoFinale);
		}catch (Exception e) {
			System.out.println("Errore nella chiusura bar");
		}
	}//chiudiBar

}//barSem
