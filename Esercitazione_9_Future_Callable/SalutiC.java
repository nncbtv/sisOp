package sisOp.Esercitazione_9_Future_Callable;

import java.util.concurrent.Callable;

public class SalutiC implements Callable<String>{

	@Override
	public String call() throws Exception {
		return "Ciaoooooooo PakoCC";
	}//call

}//SalutiC