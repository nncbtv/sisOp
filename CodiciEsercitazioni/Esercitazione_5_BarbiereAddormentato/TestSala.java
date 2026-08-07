package sisOp.Esercitazione_5_BarbiereAddormentato;

import sisOp.Esercitazione_6_BarbiereAddormentatoLock.SalaLock;

public class TestSala {
	public static void main(String[] args) throws InterruptedException {
		//Sala tiziano = new SalaSem(4); //Implemetazione sincronismo semafori
		Sala tiziano = new SalaLock(4); //Implementazione Lock
		tiziano.test(30);
	}//main
}//TestSala