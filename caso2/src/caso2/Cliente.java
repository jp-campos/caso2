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


	public void start()
	{
		try {
			sock = new Socket(HOST, PUERTO);
			escritor = new PrintWriter(sock.getOutputStream(), true);
			lector = new BufferedReader(new InputStreamReader(sock.getInputStream()));


			BufferedReader stdIn = new BufferedReader(
					new InputStreamReader(System.in));
			String fromServer;
			String fromUser;
			while (ejecutar) {
				
				System.out.print("Escriba el mensaje para enviar:");
				fromUser = stdIn.readLine();
				if (fromUser != null && !fromUser.equals("-1")) {
					System.out.println("Cliente: " + fromUser);
					if (fromUser.equalsIgnoreCase("OK"))
						ejecutar = false;
					escritor.println(fromUser);
				}
				if ((fromServer = lector.readLine()) != null) {
					System.out.println("Servidor: " + fromServer);
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


