package sisOp.Tracce.Hotel.Codice;

import java.util.concurrent.TimeUnit;

public class Receptionist extends Thread{

	private final Hotel HOTEL_ASSEGNATO;
	private final int PRIORITA;
	
	public Receptionist(Hotel h, int i) {
		this.HOTEL_ASSEGNATO = h;
		this.PRIORITA = i;
	}//Costruttore
	
	@Override
	public void run() {
		while ( HOTEL_ASSEGNATO.isAperto() ) {
			try {
				HOTEL_ASSEGNATO.iniziaCheckIn();
				 TimeUnit.SECONDS.sleep(3); // simulazione check-in
				HOTEL_ASSEGNATO.fineCheckIn();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}//run
	
	public int getCoda() {
		return PRIORITA;
	}//getPriorita
	
}//Receptionist