import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Random;

import static java.lang.Thread.sleep;

public class PingServerTask implements Runnable
{
    DatagramSocket socket;
    InetAddress clientAddress;
    int clientPort;
    byte[] data;
    int dataLength;
    Random random;

    public PingServerTask(DatagramSocket socket, DatagramPacket packet)
    {
        this.socket = socket;
        clientAddress = packet.getAddress();
        clientPort = packet.getPort();
        dataLength = packet.getLength();
        data = new byte[dataLength];
        System.arraycopy(packet.getData(), 0, data, 0, dataLength);
        random = new Random();
    }

    int sendResponse(DatagramSocket socket, DatagramPacket packet, InetAddress address, int port, byte[] data, int length)
    {
        int delay = random.nextInt(PingServer.maxDelay - PingServer.minDelay) + PingServer.minDelay;
        try { sleep(delay); } catch (InterruptedException e) { e.printStackTrace(); }

        packet.setAddress(address);
        packet.setPort(port);
        packet.setData(data, 0, length);
        packet.setLength(length);

        try { socket.send(packet); } catch (IOException e) { e.printStackTrace(); }

        return delay;
    }

    @Override
    public void run()
    {
        byte[] sendBuffer = new byte[256];
        DatagramPacket sendPacket = new DatagramPacket(sendBuffer, sendBuffer.length);

        String log = clientAddress.toString() + ":" + clientPort + "> ";
        log += (new String(data, 0, dataLength)) + " ACTION: ";
        if((random.nextFloat() * 100.0f) >= PingServer.dropRate)
        {
            int delay = sendResponse(socket, sendPacket, clientAddress, clientPort, data, dataLength);
            log += "delayed " + delay + " ms";
        }
        else { log += "not sent"; }

        System.out.println(log);
    }
}
