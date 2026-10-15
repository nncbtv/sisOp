package sisOp.CodiciEsercitazioni.contoCorrente;

import sisOp.CodiciEsercitazioni.Esercitazione_6_ContoCorrenteLock.ContoCorrenteLock;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		//Inizializzo tutte le variabili
		int saldoIniziale = 1000;
		//var postaPay = new ContoCorrenteNTS(saldoIniziale); //Soluzione non thread safe
		//var postaPay = new ContoCorrenteAtomico(saldoIniziale); //Soluzione sincrona con istruzioni atomiche
		//var postaPay = new ContoCorrenteSemaforo(saldoIniziale); //Soluzione sincrona con i semafori
		var postaPay = new ContoCorrenteLock(saldoIniziale);
		int numCorrentisti = 200;
		int numOperazioni = 10;
		int importo = 100;
		System.out.println("Saldo inziale: "+saldoIniziale);
		//Chiamata ad una funzione che simula i correntisti e le operazioni con thread
		testCorrentisti(postaPay, numCorrentisti, numOperazioni, importo);
		//Vado a controllare se è tutto correto
		if (postaPay.getSaldo() == saldoIniziale ) {
			System.out.println("Saldo finale: "+postaPay.getSaldo()+"\nTutte le operazioni sono state svolte in modo corretto!");
		} else {
			System.out.println("Errore nelle operazioni!!");
		}

	}//main
	//Qui parto con la funzione che sovlge tutti i test
	private static void testCorrentisti(ContoCorrente cc, int nCorrentisti, int nOperazioni, int importo) throws InterruptedException {
		//Creo un array di correntisti
		Correntista alberto [] = new Correntista [nCorrentisti]; //Tutti i correntisti si chimano alberto (nome fittizzio)
		for ( int i = 0; i<nCorrentisti; i++ ) {//Istanzio ogni classe nekk'array
			alberto[i] = new Correntista(cc, importo, nOperazioni); //Tutti hanno lo stesso conto corrente con le stesse operazioni
		}

		//Trasformo i correntisti in un array di Thread
		Thread correntisti [] = new Thread[nCorrentisti];
		for ( int i = 0; i<nCorrentisti; i++ ) {//Assegno ogni correntista ad un Thread
			correntisti[i] = new Thread(alberto[i]);
			correntisti[i].start();
		}
		//Faccio fare le operazioni ad ognuno di loro e poi aspetto che abbiano finito tutti
		for ( int i = 0; i<nCorrentisti; i++ ) {
			correntisti[i].join();
		}
	}//testCorrentisti

}//SingletonNTS