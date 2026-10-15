/*
 * Devo creare un'applicazione multirhread che prende una matrice n*m inizializzata tutta a zero.
 * N thread devono decrementare di uno ogni elemento della riga passata per x volte.
 * M thread devono incrementare di uno ogni elemento della colonna passata per x volte.
 * Dobbiamo prima fare una soluzione non thread safe e poi una thread safe con gli atomic integer.
 * Il main deve stampare alla fine lo stato della matrice
 *
 */
package sisOp.CodiciEsercitazioni.Esercitazione_3_1;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
	public static void main(String[] arg) throws InterruptedException {
		//Deve essere rettangolare => colonne>righe
		int nRighe = 5;
		int nColonne = 11;
		int nOperazioni = 10;
		int colonna = 2;
		int [][] matrice = new int[nRighe][nColonne];
		for ( int i = 0; i<nRighe; i++) {
			Arrays.fill(matrice[i], 0); //Riempio di zero la matrice
		}
		System.out.println("Matrice non thread safe: ");
		testWorkerNTS(matrice, nRighe, nColonne, nOperazioni, colonna); //Metodo chemi avvia i thread in modo non sicuro
		//Stampo la matrice dopo il lavoro dei worker
		for ( int i = 0; i<nRighe; i++) {
			System.out.println(Arrays.toString(matrice[i]));
		}
		System.out.println("\nMatrice thread safe: ");
		//INIZIO MATRICE THREAD SAFE
		AtomicInteger [][] matriceAtomica = new AtomicInteger[nRighe][nColonne];
		//Riempiamo di zeri la matrice
		for ( int i = 0; i<nRighe; i++) {
			for ( int j = 0; j<nColonne; j++) {
				matriceAtomica[i][j] = new AtomicInteger(0); //Creazione di oggetti AtomicInteger
			}
		}
		testWorkerTS(matriceAtomica, nRighe, nColonne, nOperazioni, colonna); //Metodo chemi avvia i thread sicuri
		//Stampo la matrice atomica
		for ( int i = 0; i<nRighe; i++) {
			System.out.println(Arrays.toString(matriceAtomica[i])); //Stampa ogni oggetto AtomicInteger con il suo toString
		}

	}//main
	private static void testWorkerNTS(int [][] matrice, int nRighe, int nColonne, int nOperazioni, int colonna) throws InterruptedException {
		workerRigaNTS[] wr = new workerRigaNTS[nRighe]; //Creazione di un vettore di workerRiga, devono essere tanti quante le righe
		for ( int i = 0; i<nRighe; i++) {
			wr[i] = new workerRigaNTS(matrice[i], nOperazioni); //Passo la riga e il numero di operazioni ad ogni workerRiga inizializzandolo
		}
		workerColonnaNTS[] wc = new workerColonnaNTS[nColonne]; //Creazione di un vettore di workerColonna, devono essere tanti quante le colonne
		for ( int i = 0; i<nColonne; i++) {
			wc[i] = new workerColonnaNTS(matrice, nOperazioni, colonna); //Passo la matrice, il numero di operazioni e la colonna su cui lavorare
		}
		//Ora inizializzo i thread e li faccio partire
		Thread [] threadWorkerRiga = new Thread[nRighe];
		for ( int i = 0; i<nRighe; i++) {
			threadWorkerRiga[i] = new Thread(wr[i]); //Gli oggetti worker inizializzano i thread
			threadWorkerRiga[i].start();
		}
		Thread [] threadWorkerColonna = new Thread[nColonne];
		for ( int i = 0; i<nColonne; i++) {
			threadWorkerColonna[i] = new Thread(wc[i]);
			threadWorkerColonna[i].start();
		}
		//Adesso faccio riunire tutti i thread prima di terminare
		for ( int i = 0; i<nRighe; i++) {
			threadWorkerRiga[i].join();
		}
		for ( int i = 0; i<nColonne; i++) {
			threadWorkerColonna[i].join();
		}
	}//testWorkerNTS
	//identico al metodo precedente, dovremmo fare refractoring ma mi scoccia, magari al prossimo esercizio
	private static void testWorkerTS(AtomicInteger [][] matrice, int nRighe, int nColonne, int nOperazioni, int colonna) throws InterruptedException {
		workerRigaTS[] wr = new workerRigaTS[nRighe];
		for ( int i = 0; i<nRighe; i++) {
			wr[i] = new workerRigaTS(matrice[i], nOperazioni);
		}
		workerColonnaTS[] wc = new workerColonnaTS[nColonne];
		for ( int i = 0; i<nColonne; i++) {
			wc[i] = new workerColonnaTS(matrice, nOperazioni, colonna);
		}
		//Ora inizializzo i thread e li faccio partire
		Thread [] threadWorkerRiga = new Thread[nRighe];
		for ( int i = 0; i<nRighe; i++) {
			threadWorkerRiga[i] = new Thread(wr[i]);
			threadWorkerRiga[i].start();
		}
		Thread [] threadWorkerColonna = new Thread[nColonne];
		for ( int i = 0; i<nColonne; i++) {
			threadWorkerColonna[i] = new Thread(wc[i]);
			threadWorkerColonna[i].start();
		}
		//Adesso faccio riunire tutti i thread prima di terminare
		for ( int i = 0; i<nRighe; i++) {
			threadWorkerRiga[i].join();
		}
		for ( int i = 0; i<nColonne; i++) {
			threadWorkerColonna[i].join();
		}
	}//testWorkerTS
}//Main