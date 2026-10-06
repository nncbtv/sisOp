package sisOp.CodiciEsercitazioni.Esercitazione_5_MemoriaCondivisa;

import sisOp.CodiciEsercitazioni.Esercitazione_6_MemoriaCondivisaLock.MemoriaCondivisaLock;

public class TestMemoriaCondivisa {

	public static void main(String[] arg) {
		int N_LETTORI = 4;
		int N_SCRITTORI = 2;
		//MemoriaCondivisa m = new MemoriaCondivisaSem();
		MemoriaCondivisa m = new MemoriaCondivisaLock();
		m.test(N_LETTORI, N_SCRITTORI);
	}//main
	
}//TestMemoriaCondivisa