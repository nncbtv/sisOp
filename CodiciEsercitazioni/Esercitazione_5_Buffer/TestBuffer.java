package sisOp.CodiciEsercitazioni.Esercitazione_5_Buffer;

import sisOp.CodiciEsercitazioni.Esercitazione_6_BufferLock.BufferLockFIFO;

public class TestBuffer {

	public static void main(String[] args) {
		int dimensione = 10;
		//Buffer b = new BufferSemaforo(dimensione); //Buffer sincronizzato semafori
		//Buffer b = new BufferLock(dimensione); //Buffer sincronizzato lock e condition
		Buffer b = new BufferLockFIFO(dimensione); //Buffer \\ \\ politica fifo
		int nProduttori = 10;
		int nConsumatori = 10;
		b.test(nProduttori, nConsumatori);
	}//Main

}//TestBuffer