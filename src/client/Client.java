package client;

import java.io.*;
import java.net.*;

import server.game.DataFileDir;

public class Client implements DataFileDir{

    public static void main(String[] args) throws UnknownHostException, IOException {
        
        int port = 3000;
        String host = "localhost";

        System.out.println("Connecting to server...");
        Socket socket = new Socket(host, port);
        System.out.printf("Connected to server %s on port %d. \n", host, port);

        sendToServer(socket);
    }

    public static void sendToServer(Socket socket) throws IOException{

        OutputStream os = socket.getOutputStream();
        OutputStreamWriter osw = new OutputStreamWriter(os);
        BufferedWriter bw = new BufferedWriter(osw);

        InputStream is = socket.getInputStream();
        InputStreamReader isw = new InputStreamReader(is);
        BufferedReader br = new BufferedReader(isw);

        Console console = System.console();
        boolean close = false;

        while (!close){

            String msgFromGame = "";

            // client will send message, then read the response 
            String msgToServer = console.readLine(">>> ");
            bw.write(msgToServer);
            bw.newLine();
            bw.flush();

            msgFromGame = br.readLine();
            System.out.println(msgFromGame);

            boolean c1 = msgFromGame.contains("lose");
            boolean c2 = msgFromGame.contains("win");
            if (c1 || c2){
                writeHistToCSV(msgFromGame);
            }

            if (msgToServer.equals("close")){
                socket.close();
                close = true;
            } 
        }
    }

    public static void writeHistToCSV(String msgFromGame) throws IOException{
        String filePath = dataFileDir + "game_history.csv";
        File f = new File(filePath);

        if (!f.exists()){
            f.createNewFile();
            FileWriter fw = new FileWriter(f);
            BufferedWriter bw = new BufferedWriter(fw);   
            bw.write("Game History"); 
            bw.newLine();
            bw.flush();

        }
    
        String[] words = msgFromGame.split(" ");
        
        boolean playerWin = true;
        for (String s : words){
            if (s.equals("loses")){ 
                playerWin = false;
                break;
            }
        }
        FileReader fr = new FileReader(f);
        BufferedReader br = new BufferedReader(fr);
        FileWriter fw = new FileWriter(f, true);
        BufferedWriter bw = new BufferedWriter(fw); 
        String lineRead = "";
        boolean endReading = false;
        while (!endReading){
            lineRead = br.readLine();

            if (lineRead!=null){
                if (lineRead.equals("Game History")){
                    continue;
                }
                else{
                    String[] s = lineRead.split(",");
                    if (s.length<6){
                        if (playerWin){
                            bw.write("P,");
                        }
                        else {
                            bw.write("B,");
                        }
                        bw.flush();
                        endReading = true;
                    }
                    else {continue;}
                }
            }
            
            else {
                bw.newLine();
                if (playerWin){
                    bw.write("P,");
                }
                else {
                    bw.write("B,");
                }
                bw.flush();
                endReading = true;
            }
        }
    }
}