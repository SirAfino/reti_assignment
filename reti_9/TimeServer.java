import java.io.FileNotFoundException;
import java.io.FileReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.Scanner;

import static java.lang.Thread.sleep;

public class TimeServer
{
    static InetAddress multicastAddress;
    static int multicastPort;
    static int timeGap = 3000;

    static boolean run = true;
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

    public static void main(String args[]) throws Exception
    {
        /* TimeServer address port
         * address (String) multicast address in decimal dotted notation
         * port (int) multicast port
         */

        multicastAddress = InetAddress.getByName("239.0.17.1");
        multicastPort = 5000;
        if(args.length > 0) { parseArguments(args[0]); }

        MulticastSocket multicastSocket = new MulticastSocket(5000);

        byte[] sendBuffer = new byte[64];
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length);
        sendPacket.setAddress(multicastAddress);
        sendPacket.setPort(multicastPort);

        while(run)
        {
            String timestamp = new Timestamp(System.currentTimeMillis()).toString();
            sendPacket.setData(timestamp.getBytes());
            sendPacket.setLength(timestamp.length());
            multicastSocket.send(sendPacket);
            System.out.println("Sent timestamp: " + timestamp);
            sleep(timeGap);
        }

        multicastSocket.close();
    }
}
