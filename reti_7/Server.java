import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.Iterator;

public class Server
{
    static int socketPort = 5000;
    static boolean run = true;
    static Selector selector;

    static void acceptHandler(SelectionKey key)
    {
        ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
        try {
            SocketChannel client = serverSocketChannel.accept();
            client.configureBlocking(false);
            client.register(selector, SelectionKey.OP_READ);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void readHandler(SelectionKey key)
    {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        try {
            String message = "";
            client.read(byteBuffer);
            byteBuffer.flip();
            while(byteBuffer.hasRemaining())
            {
                message += (char) byteBuffer.get();
            }
            key.interestOps(SelectionKey.OP_WRITE);
            key.attach(message);
            System.out.println("Message received: " + message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void writeHandler(SelectionKey key)
    {
        SocketChannel client = (SocketChannel) key.channel();
        ByteBuffer byteBuffer = ByteBuffer.allocate(2048);
        String message = "echoed by server : " + key.attachment();
        byteBuffer.put(message.getBytes());
        byteBuffer.flip();
        try {
            while(byteBuffer.hasRemaining())
            {
                client.write(byteBuffer);
            }
            client.close();
            key.cancel();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws IOException
    {
        ServerSocketChannel serverSocketChannel = ServerSocketChannel.open();
        serverSocketChannel.bind(new InetSocketAddress(socketPort));
        serverSocketChannel.configureBlocking(false);

        selector = Selector.open();
        serverSocketChannel.register(selector, SelectionKey.OP_ACCEPT);

        while(run)
        {
            selector.select();
            Iterator<SelectionKey> iterator = selector.selectedKeys().iterator();
            while(iterator.hasNext())
            {
                SelectionKey key = iterator.next();
                iterator.remove();
                if(key.isAcceptable()) { acceptHandler(key); }
                else if(key.isReadable()) { readHandler(key); }
                else if(key.isWritable()) { writeHandler(key); }
            }
        }
    }
}
