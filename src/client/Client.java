package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Client implements Runnable {
	/* The Socket of the Client */
	
	private Socket clientSocket;
	private BufferedReader serverToClientReader;
	private PrintWriter clientToServerWriter;
	private String name;
	public ObservableList<String> chatLog;

	public Client(String name) throws Exception {
		
			// Try to establish a connection to the server 
		
			clientSocket = new Socket("localhost", 8080);
			
			// Instantiate writers and readers to the socket 
			
			serverToClientReader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			clientToServerWriter = new PrintWriter(clientSocket.getOutputStream(), true);
			chatLog = FXCollections.observableArrayList();
			
			/* Send name data to the server */
			this.name = name;
			clientToServerWriter.println(name);

		
	}

	public void writeToServer(String input) {
		clientToServerWriter.println(name + " : " + input);
	}

	public void run() {
		//  loop to update the chat log from the server 
		
		while (true) {
			try {
				final String inputFromServer = serverToClientReader.readLine();
                                if (inputFromServer.split(";")[1].equals(name)) {
                                    System.out.println("Hello");
                                    {
                                    Platform.runLater(new Runnable() { 
					public void run() {
						chatLog.add(inputFromServer.split(";")[0]);}
					});
                                }
                                }
                                
                                else {
                                    System.out.println("Not hello");
                                }
                                System.out.println(inputFromServer.split(";")[1]+";"+name);
//                                {
//				Platform.runLater(new Runnable() { 
//					public void run() {
//						chatLog.add(inputFromServer);}
//					});
//                                }
                                

			}  catch (Exception e) {
				System.out.println("Error occured while reading from server from client");
                                System.out.println(e.getMessage());
			}
		}
	}
}