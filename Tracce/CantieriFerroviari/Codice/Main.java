package sisOp.Tracce.CantieriFerroviari.Codice;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		int m = 3, operaioR = 4, operaioT = 4, n = 4;
		Operaio [] operaioRotaia = new Operaio[operaioR];
		Operaio [] operaioTraversa = new Operaio[operaioT];
		CantiereFerroviarioA FRI = new CantiereFerroviarioSem(m, n);
		for ( int i = 0; i<operaioR; i++ ) {
			operaioRotaia[i] = new Operaio(1, FRI);
			operaioRotaia[i].start();
		}
		for ( int i = 0; i<operaioT; i++ ) {
			operaioTraversa[i] = new Operaio(0, FRI);
			operaioTraversa[i].start();
		}
		for ( int i = 0; i<operaioT; i++ ) {
			operaioTraversa[i].join();
		}
		for ( int i = 0; i<operaioR; i++ ) {
			operaioRotaia[i].join();
		}
		System.out.println("Tutti i binari sono stati costruiti");
	}//main

}//Main