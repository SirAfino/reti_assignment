import java.io.*;
import java.net.ServerSocket;
import java.util.Scanner;

public class MainClass
{
    private static int port = 5000;

    public static void parseArguments(String filename)
    {
        try {
            Scanner s = new Scanner(new FileReader(filename));
            port = s.nextInt();
            port = port > 1023 ? port : 5000;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException
    {
        /* MainClass portNumber
         * portNumber (int) server socket port (portNumber > 1023)
         */

        if(args.length >= 1) { parseArguments(args[0]); }
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Server ready, waiting for connections...");
        while(true) { new Thread(new ServerTask(serverSocket.accept())).start(); }
    }
}