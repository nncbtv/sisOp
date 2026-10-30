package sisOp.Tracce.ProntoSoccorso.Codice;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		final int N_PAZIENTI = 30;
		Thread [] pazienti = new Thread[N_PAZIENTI];
		ProntoSoccorso spitale = new ProntoSoccorsoLC(N_PAZIENTI);
		Medico [] m = new Medico[2];
		for ( int i = 0; i<N_PAZIENTI; i++ ) {
			pazienti[i] = new Paziente(spitale, (i%3));
			pazienti[i].start();
			//L'operazione modulo li distribuisce
		}
		for ( int i = 0; i<2; i++ ) {
			m[i] = new Medico(spitale, i);
			m[i].start();
		}
		for ( int i = 0; i<N_PAZIENTI; i++ ) {
			pazienti[i].join();
		}
		for ( int i = 0; i<2; i++ ) {
			m[i].join();
		}
		System.out.println("I clienti sono stati visitati");
		System.out.println("Pronto Soccorso chiuso");
	}//main

}//Main