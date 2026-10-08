package sisOp.CodiciEsercitazioni.Esercitazione_6_CinqueFilosofi;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class TavoloLock extends Tavolo{
	
	protected Lock l = new ReentrantLock();
	protected Condition[] aspettoBacchetta = new Condition[getFilosofi()];
	
	public TavoloLock() {
		for ( int i=0; i<getFilosofi(); i++) {
			//Collego le condition al lucchetto
			aspettoBacchetta[i] = l.newCondition();
		}
	}//costruttore

	@Override //i e la posizione del filosofo sul tavolo
	public void prendiBacchette(int i) throws InterruptedException {
		l.lock();
		try {
			//Finche le bacchette sono occupate aspetto
			while ( bacchette[i] == true || bacchette[(i+1)%getFilosofi()] == true ) { 
				//Siccome non so quale è occupata di bacchetta controllo
				if ( bacchette[i] ) //Aspetto sulla bacchetta alla mia dx, la sx l'ho presa
					aspettoBacchetta[i].await();
				else //Aspetto sulla bacchetta alla mia sx, la dx l'ho presa
					aspettoBacchetta[(i+1)%getFilosofi()].await();
			}
			//Mi impossesso delle bacchette se sono stato svegliato
			//Chi sta pensando me ne ha lasciato due
			bacchette[i] = true;
			bacchette[(i+1)%getFilosofi()] = true;
		} finally {
			l.unlock();
		}
	}//prendiBacchette

	@Override
	public void rilasciaBacchette(int i) {
		l.lock();
		try {
			//Libero le bacchette, sempre due alla volta
			bacchette[i] = false;
			bacchette[(i+1)%getFilosofi()] = false;
			//Segnalo chi sta aspettando le bacchette
			aspettoBacchetta[i].signal();
			aspettoBacchetta[(i+1)%getFilosofi()].signal();
		} finally {
			l.unlock();
		}
	}//rilasciaBacchette
}//TavoloLock