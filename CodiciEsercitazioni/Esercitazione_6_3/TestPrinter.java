package sisOp.CodiciEsercitazioni.Esercitazione_6_3;

public class TestPrinter {

	public static void main(String[] args) throws InterruptedException {
		int n = 10; //Nummero di thread da sincronizzare
		CodaThread c = new CodaThreadLock(n); //Sincronizzazione lock
		c.avviaStampa();
	}//main

}//TestPrinter