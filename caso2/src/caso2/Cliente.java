package caso2;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.security.cert.X509Certificate;
import javax.xml.bind.DatatypeConverter;
public class Cliente extends Thread{
	private static final String HOST = "127.0.0.1";
	public static final int PUERTO = 8080;
	boolean ejecutar = true;
	Socket sock = null;
	PrintWriter escritor = null;
	BufferedReader lector = null;
	public static void main(String[] args)
	{
		boolean ejecutar = true;
		Socket sock = null;
		PrintWriter escritor = null;
		BufferedReader lector = null;


		Encriptar encrip = new Encriptar(); 
		try{
			sock = new Socket(HOST, PUERTO);
			escritor = new PrintWriter(sock.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			BufferedReader stdIn = new BufferedReader(
					new InputStreamReader(System.in));
			String respuestaServer;
			String fromUser;
			int estado = 0;
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

						System.out.print("Escriba el mensaje para enviar:");

						//Aqui mandar: ALGORITMOS:Blowfish:RSA:HMACMD5

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

					respuestaServer = lector.readLine(); 

					byte[] llaveByte = DatatypeConverter.parseHexBinary(respuestaServer);

					encrip.encriptarLlaveServer(llaveByte); 	





				}else if(estado == 5)
				{


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
}