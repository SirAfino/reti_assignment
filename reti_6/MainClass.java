import java.io.*;
import java.net.ServerSocket;

public class MainClass
{
    public static void main(String args[]) throws IOException
    {
        ServerSocket serverSocket = new ServerSocket(5000);
        System.out.println("Waiting for connection...");
        while(true)
        {
            new Thread(new ServerTask(serverSocket.accept())).start();
        }
    }
}
