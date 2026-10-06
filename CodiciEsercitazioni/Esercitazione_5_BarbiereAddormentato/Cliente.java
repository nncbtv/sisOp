package sisOp.CodiciEsercitazioni.Esercitazione_5_BarbiereAddormentato;

public class Cliente extends Thread {
	
	private Sala sala; //Sala in cui trovo il barbiere
	private int id; //Numero di arrivo
	
	public Cliente ( Sala s, int i) {
		this.sala = s;
		this.id = i;
	}//costruttore

	@Override
	public void run() {
		try {
			System.out.println("Il cliente: "+id+" vuole tagliarsi i capelli...");
			boolean accettato = sala.attendiTaglio(); //Questo metodo mi fa mettere in coda ed entrare nella sala
			if (accettato) {
				System.out.println("Il cliente: "+id+" è sulla poltrona");
			}else {
				System.out.println("Il cliente: "+id+" ha preso un appuntamento per domani");
			}
		}catch (Exception e) {
			System.out.println("Errore nel cliente");
		}
	}//run
	
	public String toString() {
		return "Cliente: "+id;
	}
	
	public int getID() {return id;}

}//Cliente