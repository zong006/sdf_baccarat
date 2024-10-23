package server.handlers;

import java.io.*;
import java.net.Socket;

import server.game.BaccaratEngine;

public class ClientHandler implements Runnable{
    
    private Socket socket;
    private BaccaratEngine engine;

    public ClientHandler(Socket socket, int numOfDecks) throws IOException {
        this.socket = socket;
        this.engine = new BaccaratEngine(numOfDecks);
    }

    @Override
    public void run() {
        try {
            takeCommandFromClient();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void takeCommandFromClient() throws IOException{

        InputStream is = socket.getInputStream();
        InputStreamReader isr = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isr);

        

        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);

        String clientInput = "";
        boolean close = false;

        while (!close){
            // first takes the input from client, then returns a response from the game engine
            clientInput = br.readLine();
            String statement = engine.takeInput(clientInput);                
            
            if (!(clientInput.equals("close"))){
                bw.write(statement);
                bw.newLine();
                bw.flush();

            }
            else {
                bw.write("game closed.");
                bw.flush();
                bw.close();
                osw.close();
                os.close();
                br.close();
                isr.close(); 
                is.close();

                close = true;
                socket.close();
            }   
        }
    }
}
