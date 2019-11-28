import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class PingServer
{
    //Server socket port
    static int serverPort;
    //Server socket available port range
    static int minServerPort = 1024;
    static int maxServerPort = 65536;
    //Server ping packets drop rate (%)
    public static float dropRate = 25.0f;
    //Server fake ping delay range
    public static int minDelay = 20;
    public static int maxDelay = 150;
    //Thread pool size
    static int poolSize = 5;

    static int parsePort(String portString)
    {
        int port;
        try{ port = Integer.parseInt(portString); }
        catch(NumberFormatException e) { port = -1; }
        return port;
    }

    static int parseArguments(String filename)
    {
        int result = 0;
        try {
            Scanner s = new Scanner(new FileReader(filename));
            serverPort = parsePort(s.next());
            if(serverPort <= minServerPort || serverPort >= maxServerPort)
            {
                result = 1;
            }
        } catch (FileNotFoundException e) { result = 1; }
        return result;
    }

    public static void main(String args[]) throws IOException, InterruptedException
    {
        /* PingServer port
         * port (int) socket port number (range: 1024 - 65536)
         */

        boolean run = true;

        //Parse and check arguments
        int badArgument = args.length > 0 ? parseArguments(args[0]) : 1;
        if(badArgument != 0)
        {
            System.out.println("ERR - arg " + badArgument);
        }
        else
        {
            //Open Datagram Socket
            DatagramSocket serverSocket = new DatagramSocket(serverPort);

            //Allocate receive and send buffers and packets
            byte[] receiveBuffer = new byte[256];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);

            //Allocate threadPool
            ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(poolSize);

            System.out.println("Ping server ready");

            while(run)
            {
                //Read ping packet and add handlerTask to the threadPool
                serverSocket.receive(receivePacket);
                threadPoolExecutor.execute(new PingServerTask(serverSocket, receivePacket));
            }

            serverSocket.close();
        }
    }
}