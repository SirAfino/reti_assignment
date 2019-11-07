import java.io.*;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class ServerTask implements Runnable
{
    Socket socket;

    public ServerTask(Socket socket)
    {
        this.socket = socket;
    }

    byte[] readFromFile(String filePath)
    {
        byte[] result = null;
        try {
            ByteBuffer byteBuffer = ByteBuffer.allocate((int) new File(filePath).length());
            FileChannel inChannel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ);
            inChannel.read(byteBuffer);
            byteBuffer.flip();
            result = byteBuffer.array();
            inChannel.close();
        } catch (IOException e) { e.printStackTrace(); }
        return result;
    }

    boolean checkFileAvailability(String filePath)
    {
        return filePath != null ? new File(filePath).exists() : null;
    }

    String getContentType(String fileName)
    {
        String result;
        switch (fileName.substring(fileName.indexOf(".") + 1))
        {
            case "jpg" : result = "image/jpeg"; break;
            case "png" : result = "image/png"; break;
            case "gif" : result = "image/gif"; break;
            case "mp4" : result = "video/mp4"; break;
            case "mp3" : result = "audio/mpeg"; break;
            case "pdf" : result = "application/pdf"; break;
            case "js" : result = "text/javascript"; break;
            case "css" : result = "text/css"; break;
            case "txt" : case "html" : default : result = "text/html"; break;
        }
        return result;
    }

    void sendData(BufferedOutputStream writer, String responseCode, byte[] data, String contentType)
    {
        try {
            writer.write(("HTTP/1.1 " + responseCode + "\r\n").getBytes());
            writer.write(("Date: " + getServerTime()).getBytes());
            if(data != null)
            {
                writer.write(("Content-Type: " + contentType + "\r\n\r\n").getBytes());
                writer.write(data);
            }
            writer.flush();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public String getRequestedFileName(String request)
    {
        return getRequestType(request).equals("GET") ? request.split(" ")[1].substring(1) : null;
    }

    String getRequestType(String request)
    {
        return request != null ? request.split(" ")[0] : "";
    }

    String getServerTime()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ITALY);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(Calendar.getInstance().getTime());
    }

    void handleRequest(BufferedOutputStream writer, String request)
    {
        String responseCode = "405 Method Not Allowed";
        byte[] data = null;
        String contentType = "Content-Type: text/html";
        if(getRequestType(request).equals("GET"))
        {
            String requestedFile = getRequestedFileName(request);
            if(checkFileAvailability(requestedFile))
            {
                responseCode = "200 OK";
                data = readFromFile(requestedFile);
                contentType = "Content-Type: " + getContentType(requestedFile);
            }
            else
            {
                responseCode = "404 Not Found";
            }
        }
        sendData(writer, responseCode, data, contentType);
    }

    @Override
    public void run()
    {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedOutputStream writer = new BufferedOutputStream(socket.getOutputStream());

            String request = reader.readLine();
            System.out.println("Request : " + request);
            handleRequest(writer, request);

            writer.close();
            reader.close();
            socket.close();
        } catch (IOException e) { e.printStackTrace(); }
    }
}
