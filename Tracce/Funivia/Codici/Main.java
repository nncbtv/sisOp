package sisOp.Tracce.Funivia.Codici;

public class Main {

	public static void main(String[] args) {
		Funivia f = new FuniviaSempahore(180, 90);
		//I pedoni devono essere per forza multipli di 6 e i ciclisti multipli di 3 altrimenti andiamo in deadlock con i semfori
		//Perché il pilota si aspetta di riempire i seggiolini ad ogni corsa, questo è impossibile con un numero disapri ad esempio
		f.test();
	}//main

}//Main