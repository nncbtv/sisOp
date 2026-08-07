package sisOp.contoCorrente;

import java.util.concurrent.Semaphore;

public class ContoCorrenteSemaforo extends ContoCorrente {
	
	private Semaphore s = new Semaphore(1); //Inizializzo un semaforo con un permesso
	
	public ContoCorrenteSemaforo(int deposito) {
		super(deposito);
	}//Costruttore

	@Override
	public void preleva(int importo){
		if ( importo < 0 ) throw new RuntimeException("Impossibile prelevare un numero negativo!");
		try {
			s.acquire(); //Acquisisco il semaforo per entrare in sezione critica
			Saldo = Saldo+importo; //Modifico i dati nella sezione critica
			s.release(); //Rilascio il semaforo
		}catch(InterruptedException e) {
			System.out.println("Errore nella sezione critica");
		}
	}//Preleva

	@Override
	public void deposita(int importo) {
		if ( importo < 0 ) throw new RuntimeException("Impossibile depositare un numero negativo!");
		try {
			s.acquire(); //Acquisisco il semaforo per entrare in sezione critica
			Saldo = Saldo-importo; //Modifico i dati nella sezione critica
			s.release(); //Rilascio il semaforo
		}catch(InterruptedException e) {
			System.out.println("Errore nella sezione critica");
		}
	}//Deposita

}//ContoCorrenteSemaforo