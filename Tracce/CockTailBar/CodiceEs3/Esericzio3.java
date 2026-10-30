package sisOp.Tracce.CockTailBar.CodiceEs3;

import java.lang.Thread.State;

public class Esericzio3 {

	public static void main(String[] args) throws InterruptedException {
		Thread t1 = new Thread( new MyThread() ); //Creo il thread
		t1.start(); //Lo avvio
		t1.join(); //Aspetto che finisca il suo metodo, la stampa
		System.out.println(t1.getState().equals(State.RUNNABLE)); //Restituisce false
		Thread t2 = new Thread ( new MyThread() ); //Creo un altro thread
		t2.start(); //Lo avvio
		//Nessuna join, il thread Main non aspetterà il thread t2
		System.out.println(t2.getState().equals(State.RUNNABLE)); //Non deterministico, potrebbe finire prima o dopo del thread Main
	}//Main

	static class MyThread implements Runnable{
		@Override
		public void run() {
			System.out.println("SISOP");
		}//run
	}//MyThread

}//Main
/*
 *SPIEGAZIONE
 *Sostanzialmente fino a prima di avviare t2 è tutto deterministico poichè sospendo il Main ed attendo t1, al contrario con t2
 *lo stacco dal thread Main e non so che fine farà e come si comporterà, dipende molto dallo scheduling della cpu.
 *A video mi compare: SISOP dato da t1 e poi false dato dal thread Main, per t2 l'output non è deterministico
 */