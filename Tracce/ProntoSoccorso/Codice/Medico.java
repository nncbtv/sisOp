package sisOp.Tracce.ProntoSoccorso.Codice;

public class Medico extends Thread{

	private ProntoSoccorso p;
	private int numeroCoda;
	
	public Medico(ProntoSoccorso pIn, int n) {
		this.numeroCoda = n;
		this.p = pIn;
	}//costruttore
	
	@Override
	public void run() {
		while ( p.isAperto() ) {
			try {
				p.iniziaVisita();
				p.terminaVisita();
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		System.out.println("Il medico è andato via");
	}//run
	
	public int getNumeroCoda() {
		return numeroCoda;
	}//getNumeroCoda
	
}//Medico