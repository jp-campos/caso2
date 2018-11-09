package caso2;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.management.ServiceNotFoundException;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.asn1.crmf.CertId;

import uniandes.gload.core.LoadGenerator;
public class Cliente extends Thread{

	private static final String HOST = "127.0.0.1";
	public static final int PUERTO = 8080;	
	/*
	 * Las pruebas van
	 * 400 carga 20 retardo
	 * 200 carga 40 retardo
	 * 80 carga 100 retardo 
	 */
	public static int NUMERO_CARGA = 80; 
	public static int RETRASO= 100; 
	
	
	/*
	 *Modificar en el servidor también 
	 */
	public static int NUMERO_THREADS = 1; 
	
	/*
	 * Probar con SEGURO cambiar a NOSEGURO
	 * SEGURO 
	 * NOSEGURO
	 */
	public static String SEGURIDAD  = "SEGURO"; 
	
	
	
	
	private boolean ejecutar;
	private Socket sock = null;
	private PrintWriter escritor = null;
	private BufferedReader lector = null;
	private Encriptar encrip; 
	private BufferedReader stdIn = null;
	
	
	
	
	private Monitor monitor1;
	private Monitor monitor2; 


	public Cliente(String seguridad) throws UnknownHostException, IOException, CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException
	{
		ejecutar = true; 
	
		
		encrip = new Encriptar(); 
		sock = new Socket(HOST, PUERTO);
		escritor = new PrintWriter(sock.getOutputStream(), true);
		lector = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		stdIn = new BufferedReader(
				new InputStreamReader(System.in));

		monitor1 = new Monitor("verificacion"); 
		monitor2 = new Monitor("consulta"); 
	
				

				//String respuesta = stdIn.readLine(); 
				//TODO Cambiar por el parametro despues de probar
				if(seguridad.equals("NOSEGURO"))
				{	System.out.println("Protocolo no seguro");
					protocoloSinSeguridad();

				}else if(seguridad.equals("SEGURO"))
				{
					System.out.println("Protocolo seguro");
					protocoloConSeguridad();
					
				}

	//	System.out.println("SALE DEL EJECUTAR");
	}

	/**
	 * @throws IOException
	 * @throws CertificateEncodingException
	 * @throws InvalidKeyException
	 * @throws IllegalStateException
	 * @throws NoSuchAlgorithmException
	 * @throws SignatureException
	 * @throws InvalidKeySpecException
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws NoSuchPaddingException
	 */
	public void protocoloConSeguridad() throws IOException, CertificateEncodingException, InvalidKeyException, IllegalStateException, NoSuchAlgorithmException, SignatureException, InvalidKeySpecException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException
	{
		String respuestaServer;

		int estado = 0;
		System.out.println("Entra al protocolo");

		
			while (ejecutar) {
				//respuestaServer = lector.readLine(); 
				if (estado == 0) {

					//fromUser = stdIn.readLine();
					escritor.println("HOLA");

					estado++; 
				}else if(estado == 1 )
				{
					respuestaServer = lector.readLine(); 
					if(respuestaServer.equalsIgnoreCase("OK"))
					{
						System.out.println("OK");

						//System.out.print("Escriba los algoritmos para enviar:");

						//Aqui mandar:ALGORITMOS:Blowfish:RSA:HMACMD5


						//fromUser = stdIn.readLine();

						encrip.setAlgoritmos("ALGORITMOS:Blowfish:RSA:HMACMD5");

						escritor.println("ALGORITMOS:Blowfish:RSA:HMACMD5");



						estado++; 
					}else 
					{
						System.out.println(respuestaServer);
						ejecutar = false;
					}
				}else if(estado == 2)
				{
					//Aqui mandar certificado
					respuestaServer = lector.readLine(); 
					System.out.println(respuestaServer);
					if(respuestaServer.equalsIgnoreCase("OK"))
					{

						System.out.println("Enviar Certificado");
						X509Certificate certificado = encrip.getCertificado(encrip.getKeyPair());
						byte[] certificadoEnBytes = certificado.getEncoded( );
						String certificadoEnString = Encriptar.bytesToHex(certificadoEnBytes);



						escritor.println(certificadoEnString);
						estado++;

					}else
					{
						ejecutar= false;
					}

				}else if (estado == 3)
				{
					//RECIBIR CERTIFICADO

					respuestaServer = lector.readLine(); 
					System.out.println(respuestaServer);


					if(respuestaServer.equalsIgnoreCase("OK"))
					{
						respuestaServer = lector.readLine(); 
						System.out.println("Recibir certificado");
						byte[] certificadoByte = DatatypeConverter.parseHexBinary(respuestaServer); 
						X509Certificate certificadoServer =  encrip.setCertificadoServer(certificadoByte);

						try{
							certificadoServer.checkValidity();
						}catch (Exception e) {
							escritor.println("ERROR");
							e.printStackTrace();
						}
						escritor.println("OK");
						
						//-------------------Se comienza la medida del monitor para el tiempo de verificación ---------
						monitor1.start();
						estado++;

					}else
					{
						ejecutar= false;
					}

				}else if(estado == 4)
				{
					//Devolver la llave
					System.out.println("Recibe llave");
					respuestaServer = lector.readLine(); 
					
					byte[] llaveByte = DatatypeConverter.parseHexBinary(respuestaServer);


					byte[] llaveByteRespuesta = encrip.encriptarLlaveSimetrica(llaveByte); 	
					String llaveStringRespuesta = Encriptar.bytesToHex(llaveByteRespuesta);
					System.out.println("Devolver llave");
					escritor.println(llaveStringRespuesta);

					estado++; 

				}else if(estado == 5)
				{
					respuestaServer = lector.readLine(); 
					System.out.println(respuestaServer);
					
					//-------------------Se termina la medida del monitor para el tiempo de verificación ---------
					
					synchronized (monitor1) {
						monitor1.notify();
					}
					
					
					//Hacer la consulta
					
					System.out.println("Haga la consulta");
					//fromUser= stdIn.readLine(); 
					
					
					
					String consultaNumeros = "12345"; 
					
					byte[] bytes = encrip.encriptarConLlaveSimetrica(consultaNumeros.getBytes());
					
					String consulta = Encriptar.bytesToHex(bytes);
			
					escritor.println(consulta);
					byte[] bytesHmac = encrip.hmac(consultaNumeros.getBytes());
					consulta = Encriptar.bytesToHex(bytesHmac);
					escritor.println(consulta);
					//-------------------Comienza la medida del monitor para el tiempo de Consulta ------------
					monitor2.start();
					estado ++;
					
				}
					else if(estado == 6)
					{
					
					
					
					respuestaServer = lector.readLine(); 
					System.out.println("Respuesta Server " + respuestaServer);
					
					
					
					//-------------------Termina la medida del monitor para el tiempo de Consulta ------------
						synchronized (monitor2) {
							System.out.println("Se supone que notifica");
							monitor2.notifyAll();
					
						}
					
					
					//System.out.println(monitor2.isAlive());
					
					int i = 0; 	
					while(monitor2.isAlive())
					{	
						
						synchronized (monitor2) {
							//System.out.println("Se supone que notifica 2");
							monitor2.notifyAll();
					
						}
						
						
					}
					
					if(respuestaServer.equals("OK:DEBE")||respuestaServer.equals("OK:PAZYSALVO"))
					{
						estado++; 
					}
					
				}else if(estado == 7)
				{
					
					
					ejecutar = false; 
					
					
				}
				
				

			}
			escritor.close();
			lector.close();
			// cierre el socket y la entrada estándar
			stdIn.close();
			sock.close();
		

	}


	public void protocoloSinSeguridad() throws IOException
	{
		String respuestaServer;
		
		int estado = 0;


		
			while (ejecutar) {
				//respuestaServer = lector.readLine(); 
				if (estado == 0) {
					System.out.print("Escriba el mensaje para enviar:");
				
					
						escritor.println("HOLA");
					
					estado++; 
				}else if(estado == 1 )
				{
					respuestaServer = lector.readLine(); 
					if(respuestaServer.equalsIgnoreCase("OK"))
					{
						System.out.println("OK");

						System.out.print("Escriba algoritmos para enviar:");

						//Aqui mandar:ALGORITMOS:Blowfish:RSA:HMACMD5


						escritor.println("ALGORITMOS:Blowfish:RSA:HMACMD5");



						estado++; 
					}else 
					{
						System.out.println(respuestaServer);
						ejecutar = false;
					}
				}else if(estado == 2)
				{
					//Aqui mandar certificado
					respuestaServer = lector.readLine(); 
					System.out.println(respuestaServer);
					if(respuestaServer.equalsIgnoreCase("OK"))
					{
						String certificadoEnString = "Certificado super seguro";
						System.out.println("Mandar certificado cliente");
						escritor.println(certificadoEnString);
						estado++;

					}else
					{	
						System.out.println(respuestaServer);
						ejecutar= false;
					}

				}else if (estado == 3)
				{
					//RECIBIR CERTIFICADO

					respuestaServer = lector.readLine(); 
					System.out.println(respuestaServer);


					if(respuestaServer.equalsIgnoreCase("OK"))
					{
						respuestaServer = lector.readLine(); 
						System.out.println(respuestaServer);
						System.out.println("Recibir certificado servidor");

						escritor.println("OK");
						
						estado++;

					}else
					{
						ejecutar= false;
					}

				}else if(estado == 4)
				{
					//Devolver la llave
					System.out.println("Dolver la llave");
					respuestaServer = lector.readLine(); 
					escritor.println(respuestaServer);

					estado++; 

				}else if(estado == 5)
				{
					respuestaServer = lector.readLine(); 
					System.out.println(respuestaServer);
					System.out.println("Haga la consulta");

					
					escritor.println("1234");
					escritor.println("1234");

					respuestaServer = lector.readLine(); 
					System.out.println(respuestaServer);

					ejecutar= false;
				}

			}
			escritor.close();
			lector.close();
			// cierre el socket y la entrada estándar
			stdIn.close();
			sock.close();
		


	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception
	{
		
		
		//Cliente c = new Cliente("SEGURO"); 
		
		
		ArrayList<ArrayList<Long>> listasVer = new ArrayList<>();
		ArrayList<ArrayList<Long>> listasConsul = new ArrayList<>();
		
		for (int i = 0; i < 10; i++) {
		
			
		
		Generator gen = new Generator( Cliente.NUMERO_CARGA, Cliente.RETRASO); 
		ArrayList<Long> listaVer = Monitor.getTiemposVerificacion(); 
		ArrayList<Long> listaConsul = Monitor.getTiemposConsulta();
		
		listasVer.add((ArrayList<Long>)listaVer.clone());
		listasConsul.add((ArrayList<Long>)listaConsul.clone());
		
		Monitor.reiniciarArrayListVer();
		Monitor.reiniciarArrayListConsu();
		}
		String workingPath = System.getProperty("user.dir");
		
		
		/*	SEGURO|NOSEGURO-carga-#threads
		 * 
		*/
		String pruebaActual = Cliente.SEGURIDAD + "-" + Cliente.NUMERO_CARGA + "-" + Cliente.NUMERO_THREADS;
		
		
		String nombreArchivoVer = "ver-" + pruebaActual+ ".csv";
		String nombreArchivoConsul = "consul-" + pruebaActual + ".csv";
		
		PrintWriter writerVerificacion =null;
		PrintWriter writerConsul = null; 
		try {
		writerVerificacion = new PrintWriter(workingPath+ File.separator + "data" + File.separator + nombreArchivoVer );
		 writerConsul = new PrintWriter(workingPath+ File.separator + "data" + File.separator + nombreArchivoConsul );
		}catch(Exception e )
		{
			
		}
		writerVerificacion.println("sep=,");
		writerConsul.println("sep=,");
		
		//Imprime la información
		for (ArrayList<Long> arrayList : listasVer) {
			
			ArrayList<Long> datos = arrayList; 
			StringBuilder builderVer = new StringBuilder(); 
			
			
			
			for (int i = 0; i < datos.size(); i++) {
				if(i != datos.size()-1)
				{
					builderVer.append(datos.get(i) + ","); 
					System.out.println("Entra " + datos.get(i));
				}else {
					builderVer.append(datos.get(i)); 
				}
			}
			
	
		
			
			writerVerificacion.println(builderVer.toString());
			
			
			
			
		}
		for (ArrayList<Long> arrayList2 : listasConsul) {
			
			StringBuilder builder2 = new StringBuilder(); 
			ArrayList<Long> datos2 = arrayList2;
			
			for (int i = 0; i < datos2.size(); i++) {
				if(i != datos2.size()-1)
				{
					builder2.append(datos2.get(i) + ","); 
					System.out.println("Entra " + datos2.get(i));
				}else {
					builder2.append(datos2.get(i)); 
				}
				
			}	
			writerConsul.println(builder2.toString());
		}
	
		System.out.println("acaba de verificar");
		
		writerVerificacion.close(); 
		writerConsul.close();
		
		System.out.println("Tiempo verificacion " +Monitor.getTiemposDeVerificacionPromedio());
		
		


	}
}