package sisOp.Esercitazione_5_CinqueFilosofi;

import sisOp.Esercitazione_6_CinqueFilosofi.TavoloLock;

public class TestCinqueFilosofi {
	public static void main(String[] arg) {
		//Tavolo t = new TavoloSem(); //Tavolo semaforico
		Tavolo t = new TavoloLock(); //Tavolo lock
		t.start();
	}
}//TestCinqueFilosofi