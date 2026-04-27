/*
 * Devo creare un'applicazione multirhread che prende una matrice n*m inizializzata tutta a zero.
 * N thread devono decrementare di uno ogni elemento della riga passata per x volte.
 * M thread devono incrementare di uno ogni elemento della colonna passata per x volte.
 * Dobbiamo prima fare una soluzione non thread safe e poi una thread safe con gli atomic integer.
 * Il main deve stampare alla fine lo stato della matrice
 * 
 */
package sisOp.Esercitazione_3_1;

import java.util.Arrays;

public class Main {
	public static void main(String[] arg) throws InterruptedException {
		int nRighe = 10;
		int nColonne = 8;
		int nOperazioni = 10;
		int [][] matrice = new int[nRighe][nColonne];
		for ( int i = 0; i<nRighe; i++) {
			Arrays.fill(matrice[i], 0); //Riempio di zero la matrice
		}
		
		testWorkerNTS(matrice, nRighe, nColonne, nOperazioni); //Metodo chemi avvia i thread
		
		//Stampo la matrice dopo il lavoro dei worker
		for ( int i = 0; i<nRighe; i++) {
			System.out.println(Arrays.toString(matrice[i]));
		}
	}//main
	private static void testWorkerNTS(int [][] matrice, int nRighe, int nColonne, int nOperazioni) throws InterruptedException {
		workerRigaNTS[] wr = new workerRigaNTS[nRighe]; //Creazione di un vettore di workerRiga
		for ( int i = 0; i<nRighe; i++) {
			wr[i] = new workerRigaNTS(matrice, nOperazioni); //Passo la riga e il numero di operazioni ad ogni workerRiga
		}
		workerColonnaNTS[] wc = new workerColonnaNTS[nColonne]; //Creazione di un vettore di workerColonna
		for ( int i = 0; i<nColonne; i++) {
			wc[i] = new workerColonnaNTS(matrice, nOperazioni); //Passo la colonna e il numero di operazioni ad ogni workerColonna
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
	}//testWorker
}//Main