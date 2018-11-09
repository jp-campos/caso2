package caso2;

import java.io.IOException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import uniandes.gload.core.Task;

public class ClienteTask extends Task{

	
	private static int fallas = 0; 
	
	@Override
	public void fail() {
		System.out.println(Task.MENSAJE_FAIL);
		setFallas(getFallas() + 1); 
	}

	@Override
	public void success() {
		System.out.println(Task.OK_MESSAGE);
		
	}

	@Override
	public void execute() {
		
			try {
				try {
					Cliente cliente = new Cliente(Cliente.SEGURIDAD);
				} catch (CertificateEncodingException e) {
					
					e.printStackTrace();
				} catch (InvalidKeyException e) {
				
					e.printStackTrace();
				} catch (IllegalStateException e) {
					
					e.printStackTrace();
				} catch (NoSuchAlgorithmException e) {
					
					e.printStackTrace();
				} catch (SignatureException e) {
					
					e.printStackTrace();
				} catch (InvalidKeySpecException e) {
					
					e.printStackTrace();
				} catch (IllegalBlockSizeException e) {
					
					e.printStackTrace();
				} catch (BadPaddingException e) {
					
					e.printStackTrace();
				} catch (NoSuchPaddingException e) {
					
					e.printStackTrace();
				}
			} catch (UnknownHostException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		
	}

	public static int getFallas() {
		return fallas;
	}

	public synchronized static void setFallas(int fallas) {
		ClienteTask.fallas = fallas;
	}
	
	
	

}
