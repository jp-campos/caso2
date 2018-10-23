package caso2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

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
						
						//Aqui mandar: ALGORITMOS:AES:RSA:HMACSHA1
						
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
					//Aqui mand
					respuestaServer = lector.readLine(); 
					if(respuestaServer.equalsIgnoreCase("OK"))
					{

						
					}else
					{
						ejecutar= false;
					}
					System.out.println(respuestaServer);

				}else if (estado == 3)
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


