import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.*;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.Scanner;

public class PingClient
{
    static InetAddress serverAddress;
    static int serverPort;
    static int minServerPort = 1024;
    static int maxServerPort = 65536;
    static int pingTimeout = 2000;
    static int totalPingNumber = 10;

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
            serverAddress = InetAddress.getByName(s.next());
            serverPort = parsePort(s.next());
            if(serverPort <= minServerPort || serverPort >= maxServerPort)
            {
                result = 2;
            }
        }
        catch (UnknownHostException e) { result = 1; }
        catch (FileNotFoundException e) { result = 2; }
        return result;
    }

    //Checks if the received echo data corresponds to the sent pingNumber
    static boolean checkPingEcho(byte[] data, int length, int pingNumber)
    {
        boolean result = false;
        String ping = new String(data, 0, length);
        int firstSpaceIndex = ping.indexOf(" ");
        int secondSpaceIndex = ping.indexOf(" ", firstSpaceIndex + 1);
        if(ping.substring(0, firstSpaceIndex).equals("PING"))
        {
            int n = Integer.parseInt(ping.substring(firstSpaceIndex + 1, secondSpaceIndex));
            result = n == pingNumber;
        }
        return result;
    }

    static void printPingResult(byte[] data, long delay)
    {
        String log = new String(data) + " RTT: ";
        log += delay != -1 ? delay + "ms" : "*";
        System.out.println(log);
    }

    //Sends a ping packet and wait for its echo response
    //Returns the delay of the ping message or -1 if a timeout occurs
    static long sendPing(DatagramSocket socket, DatagramPacket sendPacket, DatagramPacket receivePacket,
                        InetAddress address, int port, int pingNumber) throws IOException
    {
        long timestamp = new Date().getTime();
        byte[] data = ("PING " + pingNumber + " " + timestamp).getBytes();

        sendPacket.setAddress(address);
        sendPacket.setPort(port);
        sendPacket.setData(data, 0, data.length);
        sendPacket.setLength(data.length);

        socket.send(sendPacket);
        try{ socket.receive(receivePacket); }
        catch(SocketTimeoutException e) { receivePacket = null; }

        long delay = -1;

        if(receivePacket != null && checkPingEcho(receivePacket.getData(), receivePacket.getLength(), pingNumber))
        {
            delay = new Date().getTime() - timestamp;
        }

        printPingResult(sendPacket.getData(), delay);
        return delay;
    }

    public static void main(String args[]) throws IOException
    {
        /* PingClient serverAddress serverPort
         * serverAddress (String) ping server address in decimal dotted format
         * serverPort (int) ping server port number
         */

        int badArgument = args.length > 0 ? parseArguments(args[0]) : 1;
        if(badArgument != 0) { System.out.println("ERR - arg " + badArgument); }

        DatagramSocket socket = new DatagramSocket();
        socket.setSoTimeout(pingTimeout);

        byte[] receiveBuffer = new byte[256];
        DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        byte[] sendBuffer = new byte[256];
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length);

        int receivedPackets = 0;
        float minRTT = Float.MAX_VALUE;
        float maxRTT = -1;
        float avgRTT = 0;
        int packetLossRate;

        long delay;
        for(int i=0;i<totalPingNumber;i++)
        {
            delay = sendPing(socket, sendPacket, receivePacket, serverAddress, serverPort, i);
            if(delay != -1)
            {
                minRTT = Float.min(delay, minRTT);
                maxRTT = Float.max(delay, maxRTT);
                avgRTT += delay;
                receivedPackets++;
            }
        }
        avgRTT /= receivedPackets;
        packetLossRate = (int) ((1 - ((float)receivedPackets / totalPingNumber)) * 100);

        DecimalFormat df = new DecimalFormat("#.00");

        System.out.println("---- PING Statistics ----");
        System.out.println(totalPingNumber + " packets transmitted, " + receivedPackets + " packets received, " + packetLossRate + "% packet loss");
        System.out.println("round-trip (ms) min/avg/max = " + df.format(minRTT) + "/" + df.format(avgRTT) + "/" + df.format(maxRTT));
    }
}
