package sisOp.Esercitazione_5_Buffer;

public class TestBuffer {

	public static void main(String[] args) {
		int dimensione = 10;
		Buffer b = new BufferSemaforo(dimensione);
		int nProduttori = 10;
		int nConsumatori = 10;
		b.test(nProduttori, nConsumatori);
	}//main
	
}//TestBuffer