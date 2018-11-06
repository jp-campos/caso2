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
import java.security.cert.X509Certificate;
import java.util.ArrayList;

import javax.crypto.Mac;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.asn1.crmf.CertId;

import uniandes.gload.core.LoadGenerator;
public class Cliente extends Thread{

	private static final String HOST = "127.0.0.1";
	public static final int PUERTO = 8080;

	private boolean ejecutar;
	private Socket sock = null;
	private PrintWriter escritor = null;
	private BufferedReader lector = null;
	private Encriptar encrip; 
	private BufferedReader stdIn = null;
	
	
	
	
	private Monitor monitor;



	public Cliente(String seguridad) throws UnknownHostException, IOException
	{
		ejecutar = true; 
	
		
		encrip = new Encriptar(); 
		sock = new Socket(HOST, PUERTO);
		escritor = new PrintWriter(sock.getOutputStream(), true);
		lector = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		stdIn = new BufferedReader(
				new InputStreamReader(System.in));

		monitor = new Monitor(); 
	
				

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

	public void protocoloConSeguridad()
	{
		String respuestaServer;
		String fromUser;
		int estado = 0;


		try{
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
						monitor.start();
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
					monitor.end("verificacion");
					//Hacer la consulta
					
					System.out.println("Haga la consulta");
					//fromUser= stdIn.readLine(); 
					byte[] bytes = encrip.encriptarConLlaveSimetrica("12345".getBytes());
					
					String consulta = Encriptar.bytesToHex(bytes);
					
					escritor.println(consulta);
					
					
				
					byte[] bytesHmac = encrip.hmac("12345".getBytes());
					
					consulta = Encriptar.bytesToHex(bytesHmac);
					
					
					escritor.println(consulta);
					
					respuestaServer = lector.readLine(); 
					System.out.println(respuestaServer);
					ejecutar = false; 
					estado++; 
				}else if(estado == 6)
				{
					System.out.println("Salir ejecutar");
					ejecutar = false; 
					
					
				}
				
				

			}
			escritor.close();
			lector.close();
			// cierre el socket y la entrada estándar
			stdIn.close();
			sock.close();
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
			System.exit(1);
		}

	}


	public void protocoloSinSeguridad()
	{
		String respuestaServer;
		String fromUser;
		int estado = 0;


		try{
			while (ejecutar) {
				//respuestaServer = lector.readLine(); 
				if (estado == 0) {
					System.out.print("Escriba el mensaje para enviar:");
					fromUser = stdIn.readLine();
					if(fromUser.equals("HOLA")){
						escritor.println(fromUser);
					}else{
						System.out.println("No va de acuerdo con el protocolo");
						ejecutar = false;
					}
					estado++; 
				}else if(estado == 1 )
				{
					respuestaServer = lector.readLine(); 
					if(respuestaServer.equalsIgnoreCase("OK"))
					{
						System.out.println("OK");

						System.out.print("Escriba algoritmos para enviar:");

						//Aqui mandar:ALGORITMOS:Blowfish:RSA:HMACMD5


						fromUser = stdIn.readLine();

						escritor.println(fromUser);



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

					fromUser = stdIn.readLine();
					escritor.println(fromUser);
					escritor.println(fromUser);

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
		} catch (Exception e) {
			System.err.println("Exception: " + e.getMessage());
			System.exit(1);
		}


	}
	
	@SuppressWarnings("unused")
	public static void main(String[] args) throws Exception
	{
		ArrayList<ArrayList<Long>> listas = new ArrayList<>(); 
		for (int i = 0; i < 10; i++) {
		
			
		
		Generator gen = new Generator(400, 20); 
		ArrayList<Long> lista = Monitor.getTiemposVerificacion(); 
	
		listas.add((ArrayList<Long>)lista.clone());
		Monitor.reiniciarArrayList();
		}
		String workingPath = System.getProperty("user.dir");
		String nombreArchivoVer = "con1pool400.csv";
		String nombreArchivoCons = "ver1pool400.csv";
		
		
		
		PrintWriter writerVerificacion = new PrintWriter(workingPath+ File.separator + "data" + File.separator + nombreArchivoVer );
		
		
		writerVerificacion.println("sep=,");
		
		for (ArrayList<Long> arrayList : listas) {
			
			ArrayList<Long> datos = arrayList; 
			StringBuilder builder = new StringBuilder(); 
			
			for (int i = 0; i < datos.size(); i++) {
				if(i != datos.size()-1)
				{
					builder.append(datos.get(i) + ","); 
					System.out.println("Entra " + datos.get(i));
				}else {
					builder.append(datos.get(i)); 
				}
				
				
			}
			writerVerificacion.println(builder.toString());
			
			
			
		}
		System.out.println("acaba de verificar");
		
		writerVerificacion.close(); 
		
		System.out.println("Tiempo verificacion " +Monitor.getTiemposDeVerificacionPromedio());
		
		//System.out.println(ClienteTask.getFallas()); 
		
		//Cliente cliente = new Cliente("SEGURO");    


	}
}