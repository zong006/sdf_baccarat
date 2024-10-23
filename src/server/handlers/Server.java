package server.handlers;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {

    public static void main(String[] args) throws IOException {
        
        int port = 3000;
        int numOfThreads = 3;
        int numOfDecks = 1;

        ServerSocket server = new ServerSocket(port);
        ExecutorService threadPool = Executors.newFixedThreadPool(numOfThreads);

        System.out.printf("Connection established at port %d. \n", port);

        boolean serverClosed = false;

        while (!serverClosed){

            Socket socket = server.accept();
            System.out.println("Connection established.");

            ClientHandler gameThread = new ClientHandler(socket, numOfDecks);
            threadPool.submit(gameThread);
        }

        
    }
    
}
