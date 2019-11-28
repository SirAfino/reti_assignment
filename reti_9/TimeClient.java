import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class TimeClient
{
    static InetAddress multicastAddress;
    static int multicastPort;
    static int expectedMessageNumber = 10;

    static int minServerPort = 1024;
    static int maxServerPort = 65536;

    static void parseArguments(String filename)
    {
        try {
            Scanner s = new Scanner(new FileReader(filename));
            multicastAddress = InetAddress.getByName(s.next());
            int port = s.nextInt();
            if(port >= minServerPort && port <= maxServerPort)
            {
                multicastPort = port;
            }
        } catch (FileNotFoundException | UnknownHostException e) { e.printStackTrace(); }
    }

    public static void main(String args[]) throws IOException
    {
        /* TimeClient address port
         * address (String) multicast address in decimal dotted notation
         * port (int) multicast port
         */

        multicastAddress = InetAddress.getByName("239.0.17.1");
        multicastPort = 5000;
        if(args.length > 0) { parseArguments(args[0]); }

        MulticastSocket multicastSocket = new MulticastSocket(multicastPort);

        byte[] receiveBuffer = new byte[64];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        multicastSocket.joinGroup(multicastAddress);

        for(int i=0;i<expectedMessageNumber;i++)
        {
            multicastSocket.receive(receivePacket);
            String data = new String(receivePacket.getData(), 0, receivePacket.getLength());
            System.out.println("Received timestamp: " + data);
        }

        multicastSocket.close();
    }
}
