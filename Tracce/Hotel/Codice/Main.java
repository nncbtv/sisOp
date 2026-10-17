package sisOp.Tracce.Hotel.Codice;

import java.util.Random;

public class Main {

	public static void main(String[] args) throws InterruptedException {
		final int N_CAMERE = 50;
		final int N_CLIENTI = 100;
		final int N_RECEPTIONIST = 3;
		final Hotel tHotel = new HotelLC(N_CAMERE, N_CLIENTI);
		Random r = new Random();
		Cliente [] c = new Cliente[N_CLIENTI];
		Receptionist [] rec = new Receptionist[N_RECEPTIONIST];
		for ( int i = 0; i<N_CLIENTI; i++ ) {
			c[i] = new Cliente(tHotel, r.nextInt(0, 3));
			c[i].start();
		}
		for ( int i = 0; i<N_RECEPTIONIST; i++ ) {
			rec[i] = new Receptionist(tHotel, i); //Passo i come indice di priorità per la variante 4b
			rec[i].start();
		}
		for ( int i = 0; i<N_CLIENTI; i++ ) {
			c[i].join();
		}
		System.out.println("Tutti i clienti sono andati via");
		for ( int i = 0; i<N_RECEPTIONIST; i++ ) {
			rec[i].join();
		}
		System.out.println("Abbiamo Servito tutti l'hotel è chiuso, il receptionist si è congedato");
	}//main

}//Main