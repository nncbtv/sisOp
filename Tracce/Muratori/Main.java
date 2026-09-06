package sisOp.Tracce.Muratori;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		int stratoParete = 20;
		int nPareti = 4;
		int poggiaMattoni = 5;
		int posaCemento = 7;
		Casa porcellino = new CasaLock(stratoParete, nPareti, poggiaMattoni, posaCemento);
		porcellino.test();
	}//main

}//Main