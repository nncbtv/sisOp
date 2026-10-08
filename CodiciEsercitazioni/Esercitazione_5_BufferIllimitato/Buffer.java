package sisOp.CodiciEsercitazioni.Esercitazione_5_BufferIllimitato;

import java.util.ArrayList;
import java.util.List;

public abstract class Buffer {

	protected List<Integer> buffer; //Vettore che vedo come buffer
	
	public Buffer () {
		this.buffer = new ArrayList<Integer>(10); //Inizializzo il buffer alla dimensione passata
		//Dato che è un arraylist è illimitato
	}//Costruttore
	
	//Metodi che dovranno implementare le classi per mettere e togliere dal buffer
	public abstract void put ( int i) throws InterruptedException;
	public abstract int get() throws InterruptedException;
	
	//Metodo per fare lanciare i test
	public void test(int nProduttori, int nConsumatori) {
		for ( int i=0; i<nProduttori; i++ ) {
			new Thread (new Produttore(this)).start(); //Creo un nuovo thread di tipo produttore e lo avvio
		//Passo this per passare il buffer
		}
		for ( int i=0; i<nConsumatori; i++ ) {
			new Thread (new Consumatore(this)).start(); //Creo un nuovo thread di tipo produttore e lo avvio
		//Passo this per passare il buffer
		}
	}
	
}//Buffer