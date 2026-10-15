package sisOp.CodiciEsercitazioni.Esercitazione_5_BufferIllimitato;

public class TestBuffer {

	public static void main(String[] args) {
		Buffer b = new BufferSemaforo();
		int nProduttori = 10;
		int nConsumatori = 10;
		b.test(nProduttori, nConsumatori);
	}//main

}//TestBuffer