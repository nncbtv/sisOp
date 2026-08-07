package sisOp.Tracce.Casello.Codice;

public abstract class CaselloAutostradale {

	protected final int nPorte = 5; //numero di porte disponibili al casello
	protected final double tariffa = 1.20; //Tariffa al km
	protected double incasso = 0; //incasso del casello
	
	public abstract void accodamento(int p);//Accodamento sulla porta p
	public abstract void pagaPedaggio(double km, int p);//pagaPedaggio sulla porta p
	
	//Avvio threads
	protected void test(int V) {
		// Array di thread automobilisti
        Thread[] automobili = new Thread[V];


        // Creo e avvio i veicoli
        for(int i = 0; i < V; i++) {

            Automobilista auto = new Automobilista(this);

            automobili[i] = new Thread(auto, "Automobilista-" + i);

            automobili[i].start();

        }


        // Aspetto che tutti gli automobilisti abbiano terminato
        for(int i = 0; i < V; i++) {

            try {

                automobili[i].join();

            } catch (InterruptedException e) {

                e.printStackTrace();

            }

        }


        // Mostro l'incasso finale
        mostraIncasso();


        System.out.println("Tutti gli automobilisti hanno terminato.");
	}//test
	
	public void mostraIncasso() {
		System.out.println("L'incasso del casello è: "+incasso);
	}//mostraIncasso
	
	public int getPorte() {
		return nPorte;
	}//getPorte
	
}//CaselloAutosradale