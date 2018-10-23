package caso2;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.cert.X509Certificate;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;

import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509V1CertificateGenerator;

public class Encriptar {

	private Key llave;
	
	private KeyPair parLLaves; 
	
	private PrivateKey llavePrivadaServidor; 
	private PublicKey llavePublicaServidor; 
	
	private PrivateKey llavePrivadaCliente; 
	private PublicKey llavePublicaCliente; 
	
	
	private X509Certificate certificado;
	
	private KeyPairGenerator generador; 
	
	
	public Encriptar()
	{
		
	
		
		
	}
	
	
	
	public void crearCertificado(PublicKey llaveServidor) throws Exception{
		
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
        keyGen.initialize(1024);
        KeyPair key = keyGen.generateKeyPair();
        
       
        
		
	}
	
	
	
	public X509Certificate getCertificado(KeyPair llaves) throws Exception
	{
		
		
			
			
			
		PrivateKey privada = llaves.getPrivate(); 
		PublicKey publica = llaves.getPublic(); 
	
		
		String algoritmoPrivada = privada.getAlgorithm(); 
		String algoritmoPublica = privada.getAlgorithm(); 
		
		
		Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
		
		
		
		
		return certificado; 
	}
	
	
	
	
	
	
	
	
}
