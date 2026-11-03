package sisOp.Tracce.UfficioPostale.CodiceVariato;

public class UfficioPostaleLC extends UfficioPostale {

	public UfficioPostaleLC(int n, int m) {
		super(n, m);
		// TODO Auto-generated constructor stub
	}//costruttore

	@Override
	boolean isAperto() {
		// TODO Auto-generated method stub
		return false;
	}//isAperto

	@Override
	boolean ritiraTicket(String tipoTicket) throws InterruptedException {
		// TODO Auto-generated method stub
		return false;
	}//ritiraTicket

	@Override
	void attendiSportello(String tipoTicket) throws InterruptedException {
		// TODO Auto-generated method stub

	}//attendiSportello

	@Override
	void prossimoCliente() throws InterruptedException {
		// TODO Auto-generated method stub

	}//prossimoCliente

	@Override
	void eseguiOperazione() throws InterruptedException {
		// TODO Auto-generated method stub

	}//eseguiOperazione

}//UfficioPostale