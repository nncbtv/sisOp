package sisOp.Tracce.Cinema;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		final int N_SALE = 6; //Metto una sala in più per la variante 4b, la sala aggiuntiva rappresenta la biglietteria
		final int CAPIENZA_SALE = 10;
		final int N_CLIENTI = 50;
		final int N_ADDETTI = 6; //Aggiungo un addetto, l'addetto in più si occupa della biglietteria
		Cinema lamezia = new CinemaSem4b(N_SALE, CAPIENZA_SALE, N_CLIENTI);
		//Cinema lamezia = new CinemaSem(N_SALE, CAPIENZA_SALE, N_CLIENTI);
		Thread [] clienti = new Thread[N_CLIENTI];
		Thread [] addetti = new Thread[N_ADDETTI];
		for ( int i = 0; i < N_CLIENTI; i++) {
			clienti[i] = new Cliente(lamezia);
			clienti[i].start();
		}
		for ( int i = 0; i<N_ADDETTI; i++ ) {
			addetti[i] = new Addetto(lamezia, i);
			addetti[i].start();
		}
		for ( int i = 0; i < N_CLIENTI; i++) {
			clienti[i].join();
		}
		for ( int i = 0; i<N_ADDETTI; i++ ) {
			addetti[i].join();
		}
		System.out.println("Tutti i clienti sono stati serviti, cinema chiuso");

	}//main

}//main