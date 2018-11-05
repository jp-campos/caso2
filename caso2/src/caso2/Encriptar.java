package caso2;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Security;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Date;
import java.util.Locale;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.security.auth.x500.X500Principal;

import org.bouncycastle.asn1.x500.X500NameBuilder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.jcajce.provider.asymmetric.X509;
import org.bouncycastle.jcajce.provider.asymmetric.x509.CertificateFactory;
import org.bouncycastle.jcajce.provider.symmetric.AES.KeyGen;
import org.bouncycastle.x509.X509AttributeCertificate;
import org.bouncycastle.x509.X509V1CertificateGenerator;
import org.bouncycastle.x509.X509V3CertificateGenerator;

public class Encriptar {

	private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

	

	private KeyPair parLlaves; 

	private PublicKey llavePublicaServidor; 

	private PrivateKey llavePrivadaCliente; 
	private PublicKey llavePublicaCliente; 

	
	private Key llaveSimetrica;

	private X509Certificate certificado;

	private X509Certificate	 certificadoServer; 


	private String algoSimetrico;
	private String algoAsimetrico; 
	private String algoHmac; 
	
	
	public Encriptar()
	{
		KeyPairGenerator generador = null;

		try {
			generador = KeyPairGenerator.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

		
		generador.initialize(1024);
		
		parLlaves = generador.generateKeyPair(); 

		llavePublicaCliente = parLlaves.getPublic(); 
		llavePrivadaCliente = parLlaves.getPrivate(); 

	}





	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for ( int j = 0; j < bytes.length; j++ ) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = hexArray[v >>> 4];
			hexChars[j * 2 + 1] = hexArray[v & 0x0F];
		}
		return new String(hexChars);
	}

	
	
	@SuppressWarnings("deprecation")
	public X509Certificate getCertificado(KeyPair llaves) throws CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException 
	{



		



		String nombre = "Certificado"; 
		BigInteger bigInt = new BigInteger(llavePublicaCliente.getEncoded().length, new SecureRandom()); 

		Date notBefore = new Date(System.currentTimeMillis()); 

		long suma = 10000000000L;
		Date notAfter = new Date(System.currentTimeMillis() + suma);

		X500Principal name = new X500Principal("O=Universidad de los Andes");




		X509V3CertificateGenerator generador =  new X509V3CertificateGenerator(); 
		generador.setPublicKey(llavePublicaCliente);
		generador.setNotAfter(notAfter);
		generador.setNotBefore(notBefore);

		generador.setIssuerDN(name);
		generador.setSubjectDN(name);
		generador.setSerialNumber(bigInt);
		generador.setSignatureAlgorithm("SHA256WithRSA");



		certificado = generador.generate(llavePrivadaCliente); 


		return certificado; 
	}



	public KeyPair getKeyPair()
	{
		return parLlaves; 
	}

	public PublicKey getLlavePublicaServidor()
	{
		return llavePublicaServidor; 
	}


	
	public void setAlgoritmos(String algos)
	{
		
		String[] partes = algos.split(":");
		
		algoSimetrico = partes[1]; 
		algoAsimetrico = partes[2];
		algoHmac = partes[3];
		
		
		
	}

	
	public byte[] encriptarConLlaveSimetrica(byte[] bytes) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException
	{
		
		Cipher cipher = Cipher.getInstance(algoSimetrico); 
		
		cipher.init(Cipher.ENCRYPT_MODE, llaveSimetrica);
		
		return cipher.doFinal(bytes);
		
	
	}
	
	
	public byte[] hmac(byte[] bytes) throws NoSuchAlgorithmException, InvalidKeyException
	{
		Mac mac = Mac.getInstance(algoHmac);
		
		mac.init(llaveSimetrica);
		return mac.doFinal(bytes); 
		
	}
	
	public byte[] encriptarLlaveSimetrica(byte[] bytes) throws InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException {

		
		String arg1 = null, arg2 = null, arg3 = null; 
		Cipher cipher = null;
		Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

		if(algoSimetrico.equals("AES"))
		{
			arg1 = "AES";
			arg2 = "ECB";
			arg3 = "PKCS5Padding";
			
			cipher = Cipher.getInstance("RSA");
			//cipher = Cipher.getInstance(arg1 + "/" + arg2 + "/" + arg3);
		}else if(algoSimetrico.equals("Blowfish"))
		{
			cipher = Cipher.getInstance("RSA");
		}	
		
		
		
		
		
		SecretKeySpec k = new SecretKeySpec(bytes, algoSimetrico);
	
		//System.out.println("En Desencriptar y encriptarLlaveServer: 1 ");
		
	
		//Descriptar la que me llegó
		
		cipher.init(Cipher.DECRYPT_MODE, llavePrivadaCliente);
		 
		//System.out.println("En Desencriptar y encriptarLlaveServer: 2 ");
		byte[] llaveD = cipher.doFinal(bytes);
		
		llaveSimetrica = new SecretKeySpec(llaveD, algoSimetrico);
		
		
		//System.out.println("En Desencriptar y encriptarLlaveServer: 3");
		//encriptarla con la del servidor
		
		
		
		
		cipher.init(Cipher.ENCRYPT_MODE, llavePublicaServidor);
		
		
		byte[] llaveE = cipher.doFinal(llaveD);
		


		

		return llaveE; 
	}

	public X509Certificate setCertificadoServer(byte[] cerByte)
	{
		CertificateFactory certFactory =new CertificateFactory();


		InputStream stream = new ByteArrayInputStream(cerByte);
		try {
			certificadoServer = (X509Certificate) certFactory.engineGenerateCertificate(stream);
		} catch (CertificateException e) {

			System.out.println("Error en el metodo: setCertificadoServer");
			e.printStackTrace();
		}

		llavePublicaServidor = certificadoServer.getPublicKey(); 

		if(llavePublicaServidor == null)
		{
			System.out.println("El certificado es null");
		}
		return certificadoServer; 
	}


}
