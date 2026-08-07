package sisOp.Tracce.Casello.Codice;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Automobilista implements Runnable{

	private CaselloAutostradale casello;
	private Random random = new Random(); //mi serve per calcolare un numero randomico di km
	private double km; //km che percorrerà ogni automobilista
	private int p; //porta su cui si accoderà
	
	public Automobilista(CaselloAutostradale c) {
		this.casello = c; //Salvo il casello da cui devo passare
		this.km = random.nextDouble(50, 100.99); //km percorsi
		this.p = random.nextInt(0, casello.getPorte()); //porta su cui mi voglio accodare
	}
	
	@Override
	public void run() {
		try {
			viaggia(); //Simulo il viaggio in auto
			System.out.println("Automobilista: "+Thread.currentThread().getName()+" ha percorso "+km+" si sta accodando su "+p);
			casello.accodamento(p); //Mi accodo su una porta random
			casello.pagaPedaggio(km, p);//Pago il pedaggio
		} catch (InterruptedException e) {
			e.printStackTrace();
		} 
	}//run
	
	private void viaggia() throws InterruptedException {
		TimeUnit.MILLISECONDS.sleep((int)(km*40));
		//Dato che il range è altissimo da 50 a 100 per fare i test uso i millisecondi
	}//viaggia
	
}//Automobilista