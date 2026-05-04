package sisOp.Esercitazione_5_MemoriaCondivisa;

public class TestMemoriaCondivisa {

	public static void main(String[] arg) {
		int N_LETTORI = 4;
		int N_SCRITTORI = 2;
		MemoriaCondivisa m = new MemoriaCondivisaSem();
		m.test(N_LETTORI, N_SCRITTORI);
	}//main
	
}//TestMemoriaCondivisa