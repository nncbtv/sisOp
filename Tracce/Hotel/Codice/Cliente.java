package sisOp.Tracce.Hotel.Codice;

public class Cliente extends Thread{

	private final Hotel HOTEL_SCELTO;
	private final int PRIORITA;
	
	public Cliente(Hotel h, int p) {
		this.HOTEL_SCELTO = h;
		this.PRIORITA = p;
	}//Costruttore
	
	@Override
	public void run() {
		try {
			HOTEL_SCELTO.mettiInCoda(PRIORITA);
			HOTEL_SCELTO.checkOut();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}//run
	
}//Cliente