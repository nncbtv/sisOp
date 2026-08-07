package sisOp.Tracce.Casello.Codice;

public class Main {

	public static void main(String [] args) {
		int numeroAutomobilisti = 150;
		CaselloAutostradaleLock casello = new CaselloAutostradaleSemaphore();
		casello.test(numeroAutomobilisti);
	}//main
	
}//Main