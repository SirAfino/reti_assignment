import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class Client
{
    static String message = "hello";

    public static void parseArguments(String filename)
    {
        try {
            Scanner s = new Scanner(new FileReader(filename));
            s.useDelimiter("\\Z");
            message = s.next();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException
    {
        /* Client message
         * message (String) message to be sent to the echo server
         */

        //Parsing arguments if present
        if(args.length >= 1)
        {
            parseArguments(args[0]);
        }

        //Setting up the connection to the server and allocating buffer
        SocketAddress socketAddress = new InetSocketAddress("127.0.0.1", 5000);
        SocketChannel socketChannel = SocketChannel.open(socketAddress);
        socketChannel.configureBlocking(true);
        ByteBuffer byteBuffer = ByteBuffer.allocate(message.length() + 50);

        //Sending the message to the echo server
        byteBuffer.put(message.getBytes());
        byteBuffer.flip();
        socketChannel.write(byteBuffer);
        byteBuffer.clear();

        //Reading the response from the echo server
        socketChannel.read(byteBuffer);
        byteBuffer.flip();
        String response = "";
        while(byteBuffer.hasRemaining())
        {
            response += (char) byteBuffer.get();
        }
        System.out.println(response);

        socketChannel.close();
    }
}
