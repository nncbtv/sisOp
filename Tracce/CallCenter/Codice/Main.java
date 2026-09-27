package sisOp.Tracce.CallCenter.Codice;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		int n = 20, m = 50;
		Operatore [] operatori = new Operatore[n];
		Cliente [] clienti = new Cliente[m];
		CallCenter c = new CallCenterLC(n, m);
		for ( int i = 0; i<m; i++ ) {
			clienti[i] = new Cliente(c, i);
			clienti[i].start();
		}
		for ( int i = 0; i<n; i++ ) {
			operatori[i] = new Operatore(c, i);
			operatori[i].start();
		}
		for ( int i = 0; i<m; i++ ) {
			clienti[i].join();
			System.out.println("Cliente "+i+" servito");
		}
		for (int i = 0; i<n; i++ ) {
			operatori[i].join();
			System.out.println("Operatore "+i+" ritirato");
		}
		System.out.println("Tutti i clienti sono stati serviti");
	}//main

}//Main