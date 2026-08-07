package sisOp.Tracce.AziendaAgricola.Codice;

public abstract class AziendaAgricola {

	protected int sacchiDisponibili = 200; //Sacchetti disponibili alla vendita
	protected int costoSacco = 3;
	protected int saldoFinale = 0;
	
	abstract void pagaSacchetti ( int nSacchetti );
	abstract void ritiroSacchetti ( int nSacchetti );
	
	abstract void refillMagazzino();
	
}//AziendaAgricola