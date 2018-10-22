package caso2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class Servidor extends Thread{
	final static int SERVER_PORT = 8001; // puerto de escucha de nuestro servidor


	String clientRequest;
	Socket socket;
	BufferedReader reader;
	PrintWriter writer;

	// creamos el servidor y los canales de entrada y salida


	public Servidor(Socket socketcliente)
	{

		socket = socketcliente; 

	}

	// En cuanto se establece una conexión por parte del cliente, enviamos un saludo




	public void start()
	{

		try {
			writer = new PrintWriter (socket.getOutputStream(), true);
			reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));



			String inputLine; 
			String outputLine; 

			inputLine= reader.readLine();

			while (true) 
			{


				int estado = 0;
				// leemos del canal de entrada la petición del cliente
				

				switch (estado) {
				case 0:
					if (inputLine.equalsIgnoreCase("HOLA")) {
						outputLine = "OK";
						estado++;
					} else {
						outputLine = "ERROR-EsperabaHola";
						estado = 0;
					}
					break;
				case 1:

					if (inputLine.startsWith("ALGORITMOS")) {
						
						outputLine = "OK";
						estado++;
					} else {
						outputLine = "ERROR-EsperabaHola";
						estado = 0;
					}



					break;
				case 2:
					if (inputLine.equalsIgnoreCase("OK")) {
						outputLine = "ADIOS";
						estado++;
					} else {
						outputLine = "ERROR-EsperabaOK";
						estado = 0;
					}
					break;
				default:
					outputLine = "ERROR";
					estado = 0;
					break;
				}



				inputLine= reader.readLine(); 


			}
		} 
		catch (IOException e) 
		{
			System.out.println ("Excepción en el servidor " + e);
			System.exit(0);
		}
	}


}
