package sisOp.Tracce.BarMod.Codice;

public abstract class BarMod {
	
	//Le classi concrete implementeranno i miei metodi, con i semafori oppure con i lock
	public abstract int scegli(); //Restituisce l'indice dell'operazione, 1 se pago 2 se bevo caffe
	public abstract void inizia(int i); //Comincia l'operazione scelta
	public abstract void finisci(int i); //Finisce l'operazione scelta
	
	public void test( int nClienti ) {
		ClientiBar [] clienti = new ClientiBar[nClienti]; //Array  di clienti, è una classe Runnable
		Thread [] clientiAvviati = new Thread [nClienti]; //Array di Thread che rappresentano i Clienti
		for ( int i = 0; i<nClienti; i++ ) {
			clienti[i] = new ClientiBar(this); //Inizializzo la classe passandogli me stesso come bar
			clientiAvviati[i] = new Thread(clienti[i]); //Creo un Thread
			clientiAvviati[i].start(); //Avvio il Thread
		}
		for ( Thread x : clientiAvviati ) { //Per ogni Thread aspetto che finisca le sue operazioni
			try {
				x.join();
			} catch (InterruptedException e) {
				System.out.println("Non sono riuscito a servire tutti i clienti :(");
			}
		}
		System.out.println("Tutti i clienti sono stati serviti");
	}//test
}//BarMod