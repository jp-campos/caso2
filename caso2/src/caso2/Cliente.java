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

	private static final String HOST = "10.0.0.10";
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
						
						//-------------------Se comienza la medida del monitor para el tiempo de verificación ---------
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
					
					//-------------------Se termina la medida del monitor para el tiempo de verificación ---------
					monitor.end("verificacion");
					//Hacer la consulta
					
					System.out.println("Haga la consulta");
					//fromUser= stdIn.readLine(); 
					byte[] bytes = encrip.encriptarConLlaveSimetrica("12345".getBytes());
					
					String consulta = Encriptar.bytesToHex(bytes);
					
					
					
					
					escritor.println(consulta);
					//-------------------Se comienza la medida del monitor para el tiempo de Consulta ------------
					monitor.start();
					
					
					
					byte[] bytesHmac = encrip.hmac("12345".getBytes());
					consulta = Encriptar.bytesToHex(bytesHmac);
					
					
					escritor.println(consulta);
					
					respuestaServer = lector.readLine(); 
					System.out.println(respuestaServer);
					
					//-------------------Termina la medida del monitor para el tiempo de Consulta ------------
					monitor.endConsu();
					
					
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
		ArrayList<ArrayList<Long>> listasVer = new ArrayList<>();
		ArrayList<ArrayList<Long>> listasConsul = new ArrayList<>();
		
		for (int i = 0; i < 10; i++) {
		
			
		
		Generator gen = new Generator(400, 20); 
		ArrayList<Long> listaVer = Monitor.getTiemposVerificacion(); 
		ArrayList<Long> listaConsul = Monitor.getTiemposConsulta();
		
		listasVer.add((ArrayList<Long>)listaVer.clone());
		listasConsul.add((ArrayList<Long>)listaConsul.clone());
		
		Monitor.reiniciarArrayListVer();
		Monitor.reiniciarArrayListConsu();
		}
		String workingPath = System.getProperty("user.dir");
		
		
		/*	seg-noseg-carga-#pools
		 * 	Ej: 400-1 para una carga de 400 y 1 pool en el servidor 
		*/
		String pruebaActual ="seg-400-1";
		
		//con|ver-carga-#pools
		String nombreArchivoVer = "ver-" + pruebaActual+ ".csv";
		String nombreArchivoConsul = "consul-" + pruebaActual + ".csv";
		
		
		
		PrintWriter writerVerificacion = new PrintWriter(workingPath+ File.separator + "data" + File.separator + nombreArchivoVer );
		PrintWriter writerConsul = new PrintWriter(workingPath+ File.separator + "data" + File.separator + nombreArchivoConsul );
		
		writerVerificacion.println("sep=,");
		writerConsul.println("sep=,");
		
		//Imprime la información
		for (ArrayList<Long> arrayList : listasVer) {
			
			ArrayList<Long> datos = arrayList; 
			StringBuilder builderVer = new StringBuilder(); 
			
			StringBuilder builder2 = new StringBuilder(); 
			
			for (int i = 0; i < datos.size(); i++) {
				if(i != datos.size()-1)
				{
					builderVer.append(datos.get(i) + ","); 
					System.out.println("Entra " + datos.get(i));
				}else {
					builderVer.append(datos.get(i)); 
				}
			}
			
	
		for (ArrayList<Long> arrayList2 : listasConsul) {
			
			
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
			
		}
	
			
			writerVerificacion.println(builderVer.toString());
			System.out.println(builder2.toString());
			writerConsul.println(builder2.toString());
			
			
		}
		System.out.println("acaba de verificar");
		
		writerVerificacion.close(); 
		writerConsul.close();
		
		System.out.println("Tiempo verificacion " +Monitor.getTiemposDeVerificacionPromedio());
		
		//System.out.println(ClienteTask.getFallas()); 
		
		//Cliente cliente = new Cliente("SEGURO");    


	}
}