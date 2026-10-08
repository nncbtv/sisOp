package sisOp.CodiciEsercitazioni.Esercitazione_5_BarbiereAddormentato;

import java.util.concurrent.TimeUnit;

public abstract class Sala {
	
	protected int sedieLibere, sedie;
	//Creo una sala con n posti a sedere
	public Sala(int posti) {
		this.sedieLibere = this.sedie = posti;
	}//Costruttore
	//Lascio l'implementazione dei metodi alle classi eredi
	public abstract void tagliaCapelli() throws InterruptedException;
	public abstract boolean attendiTaglio() throws InterruptedException;
	//Test dei threads
	public void test ( int nClienti ) throws InterruptedException {
		Barbiere b = new Barbiere(this);
		b.setDaemon(true); //Questo thread vive per servire gli altri
		b.start(); //Lo avviamo subito, quando gli altri thread terminano anche questo muore
		Cliente [] c = new Cliente[nClienti];
		for ( int i = 0; i<nClienti; i++ ) {
			c[i] = new Cliente(this, i);
			c[i].start();
			TimeUnit.SECONDS.sleep(2);
		}
	}//test
}//Sala