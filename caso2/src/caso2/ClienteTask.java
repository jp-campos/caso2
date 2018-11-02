package caso2;

import java.io.IOException;
import java.net.UnknownHostException;

import uniandes.gload.core.Task;

public class ClienteTask extends Task{

	@Override
	public void fail() {
		System.out.println(Task.MENSAJE_FAIL);
		
	}

	@Override
	public void success() {
		System.out.println(Task.OK_MESSAGE);
		
	}

	@Override
	public void execute() {
		
			try {
				Cliente cliente = new Cliente("NOSEGURO");
			} catch (UnknownHostException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		
	}
	
	
	

}
