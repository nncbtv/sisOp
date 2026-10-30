package sisOp.Tracce.CantieriFerroviari.Codice.CodiceEs3;

public class Esericzio3 {

	public static void main(String[] args) {
		BW t1 = new BW(true);
		t1.start();
		Thread.State s0 = Thread.currentThread().getState();
		Thread.State s1 = t1.getState();
		System.out.println("State 0: "+s0+"; "+"State 1: "+s1);
		t1.busy_waiting = s0.equals(s1);
	}//Main

	static class BW extends Thread{
		public boolean busy_waiting;
		public BW(boolean bw) {
			busy_waiting = bw;
		}//Costruttore della classe
		@Override
		public void run() {
			do {
				try {
					Thread.sleep(10);
				}catch (Exception e) {
					e.printStackTrace();
				}
			}while(busy_waiting);
			System.out.println("sisop-corsoB");
		}//run
	}//BW
}//Main
/*COMMENTO
 * Punto i) FUNZIONAMENTO
 * Il nostro Main istanzia una classe del tipo BW che estende thread e imposta una sua variabile di istanza a True.
 * Dopo averlo avviato mette in s0 il suo stato e in s1 lo stato del thread lanciato prima, quest'ultimo dipende dallo scheduling della CPU.
 * Esegue una stampa mostrando gli stati dei due thread, dopodiché prova ad assegnare alla varibile di istanza di t1
 * il risultato booleano di un confronto tra i due stati.
 * La classe BW invece cicla costantemente l'operazioni di sleep finché la variabile busy_wainting è true, dopodiché se la variabile viene posta a false,
 * stampa sisop-corsoB
 * PUNTO ii) OUTPUT
 * Il nostro output dipende dallo stato s1 che non è deterministico, infatti esso può variare poiché dipende dallo scheduling della CPU
 * sul thread t1.
 * Se s1 è Runnable: State 0: RUNNABLE; State 1: RUNNABLE; in questo caso non stamperemo mai sisop-corsoB
 * Se s1 è New/Waiting: State 0: RUNNABLE; State 1: NEW; sisop-corsoB
 * PUNTO iii) TERMINAZIONE
 * L'applicazione non è detto che termini nella sua interezza, possono avvenire due casi, se gli stati sono uguali l'aggiornamento della
 * variabile viene posto a true, il thread t1 continuerà a ciclare all'infinito non facendo terminare nella sua interezza l'applicazione
 * mentre il thread Main termina. Se invece gli stati sono diversi l'applicazione riesce a terminare dopo la stampa di t1 perché riesce ad uscire dal ciclo.
 * N.B.
 * s1 non è deterministico, potrebbe trovarsi in uno dei seguenti stati: new quando ancora non è stato schedulato, Runnable quando è schedulato/in esecuzione e waiting quando aspetta nella sleep
 */