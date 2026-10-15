package sisOp.CodiciEsercitazioni.Esercitazione_9_Future_Callable;

public class Saluti implements Runnable{

	protected String nome;

	public Saluti(String nomeIn) {
		this.nome = nomeIn;
	}

	@Override
	public void run() {
		System.out.println("Ciao "+nome);
	}//run

}//Saluti