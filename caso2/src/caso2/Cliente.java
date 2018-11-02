package caso2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.cert.X509Certificate;

import javax.crypto.Mac;
import javax.xml.bind.DatatypeConverter;

import org.bouncycastle.asn1.crmf.CertId;
public class Cliente extends Thread{

	private static final String HOST = "127.0.0.1";
	public static final int PUERTO = 8080;

	private boolean ejecutar = true;
	private Socket sock = null;
	private PrintWriter escritor = null;
	private BufferedReader lector = null;
	private Encriptar encrip; 
	private BufferedReader stdIn = null;

	private Monitor monitor;



	public Cliente(String seguridad) throws UnknownHostException, IOException
	{
		encrip = new Encriptar(); 
		sock = new Socket(HOST, PUERTO);
		escritor = new PrintWriter(sock.getOutputStream(), true);
		lector = new BufferedReader(new InputStreamReader(sock.getInputStream()));
		stdIn = new BufferedReader(
				new InputStreamReader(System.in));

		monitor = new Monitor(); 

		int estado = 0; 
		while(ejecutar)
		{

			if(estado == 0)
			{

				System.out.println("Escriba NOSEGURO si quiere comunicarse con un servidor sin seguridad \n SEGURO si desea hacerlo con seguridad");

				String respuesta = stdIn.readLine(); 
				//TODO Cambiar por el parametro despues de probar
				if(respuesta.equals("NOSEGURO"))
				{	
					protocoloSinSeguridad(); 

				}else if(respuesta.equals("SEGURO"))
				{
					protocoloConSeguridad();
				}

			}else if (estado == 1)
			{

			}


		}
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
					System.out.print("Escriba el mensaje para enviar:");
					fromUser = stdIn.readLine();
					escritor.println(fromUser);

					estado++; 
				}else if(estado == 1 )
				{
					respuestaServer = lector.readLine(); 
					if(respuestaServer.equalsIgnoreCase("OK"))
					{
						System.out.println("OK");

						System.out.print("Escriba los algoritmos para enviar:");

						//Aqui mandar:ALGORITMOS:Blowfish:RSA:HMACMD5


						fromUser = stdIn.readLine();

						encrip.setAlgoritmos(fromUser);

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

						byte[] certificadoByte = DatatypeConverter.parseHexBinary(respuestaServer); 
						X509Certificate certificadoServer =  encrip.setCertificadoServer(certificadoByte);

						try{
							certificadoServer.checkValidity();
						}catch (Exception e) {
							escritor.println("ERROR");
							e.printStackTrace();
						}
						escritor.println("OK");

						estado++;

					}else
					{
						ejecutar= false;
					}

				}else if(estado == 4)
				{
					//Devolver la llave
					respuestaServer = lector.readLine(); 

					byte[] llaveByte = DatatypeConverter.parseHexBinary(respuestaServer);


					byte[] llaveByteRespuesta = encrip.encriptarLlaveSimetrica(llaveByte); 	
					String llaveStringRespuesta = Encriptar.bytesToHex(llaveByteRespuesta);
					escritor.println(llaveStringRespuesta);

					estado++; 

				}else if(estado == 5)
				{
					respuestaServer = lector.readLine(); 
					System.out.println(respuestaServer);


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

					fromUser = stdIn.readLine();
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

	public static void main(String[] args) throws Exception
	{
		Cliente cliente = new Cliente("SEGURO");    


	}
}