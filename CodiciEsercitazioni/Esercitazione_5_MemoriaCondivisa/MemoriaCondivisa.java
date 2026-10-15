package sisOp.CodiciEsercitazioni.Esercitazione_5_MemoriaCondivisa;

public abstract class MemoriaCondivisa {
	//Questa classe garantisce l'interfaccia di accesso alla memoria condivisa da parte dei thread
	public abstract void inizioScrittura() throws InterruptedException;
	public abstract void fineScrittura() throws InterruptedException;

	public abstract void inizioLettura() throws InterruptedException;
	public abstract void fineLettura() throws InterruptedException;

	//Avvio threads
	protected void test (int nLettori, int nScrittori) {
		Lettore l[] = new Lettore[nLettori];
		for ( int i = 0; i<nLettori; i++) {
			l[i] = new Lettore(this);
		}
		Scrittore s[] = new Scrittore[nScrittori];
		for ( int i = 0; i<nScrittori; i++) {
			s[i] = new Scrittore(this);
		}
		System.out.println("Test della classe: "+this.getClass().getSimpleName());
		for ( int i = 0; i<nLettori; i++) {
			new Thread(l[i]).start(); //Creo il thread e lo avvio
		}
		System.out.println("Sono stati avviati: "+nLettori+" lettori");
		for ( int i = 0; i<nScrittori; i++) {
			new Thread(s[i]).start();
		}
		System.out.println("Sono stati avviati: "+nScrittori+" scrittori");
	}

}//MemoriaCondivisa