package sisOp.CodiciEsercitazioni.Esercitazione_9_Future_Callable;

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

public class TestMain {

	public static void main(String[] args) throws InterruptedException, ExecutionException {
		
		//Creo una coda con 2 thread attivi, più di questi attenderanno in coda
		ExecutorService es = Executors.newFixedThreadPool(2);
		for ( int i = 0; i<3; i++) {
			es.submit(new Saluti("Marco"+i));
		}
		es.shutdown(); //Faccio terminare tutti i thread
		
		//CALLABLE
		ExecutorService ex = Executors.newSingleThreadExecutor();
		//Per vedere il suo risultato uso i future
		Future<String> ris = ex.submit(new SalutiC()); //Passo un callable
		String risultato = ris.get(); //richiamo il risultato
		System.out.println(ris.isDone()); //Per controllare se il mio Callable è stato eseguito
		System.out.println(risultato);
		ex.shutdown();
	}//main

}//TestMain