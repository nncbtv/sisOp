package sisOp.Esercitazione_5_Buffer;

public abstract class Buffer {

	protected int [] buffer; //Vettore che vedo come buffer
	protected int in = 0; //Contatore
	protected int out = 0;
	
	public Buffer ( int dimensione ) {
		this.buffer = new int [dimensione]; //Inizializzo il buffer alla dimensione passata
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