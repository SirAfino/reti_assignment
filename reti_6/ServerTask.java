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
        if(checkFileAvailability(filePath))
        {
            try {
                ByteBuffer byteBuffer = ByteBuffer.allocate((int) new File(filePath).length());
                FileChannel inChannel = FileChannel.open(Paths.get(filePath), StandardOpenOption.READ);
                inChannel.read(byteBuffer);
                byteBuffer.flip();
                result = byteBuffer.array();
                inChannel.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    boolean checkFileAvailability(String filePath)
    {
        if(filePath == null)
        {
            return false;
        }
        return new File(filePath).exists();
    }

    String getContentType(String fileName)
    {
        String result;
        String extension = fileName.substring(fileName.indexOf(".")+1);
        switch (extension)
        {
            case "jpg" : result = "Content-Type: image/jpeg"; break;
            case "png" : result = "Content-Type: image/png"; break;
            case "gif" : result = "Content-Type: image/gif"; break;
            case "mp4" : result = "Content-Type: video/mp4"; break;
            case "mp3" : result = "Content-Type: audio/mpeg"; break;
            case "pdf" : result = "Content-Type: application/pdf"; break;
            case "js" : result = "Content-Type: text/javascript"; break;
            case "css" : result = "Content-Type: text/css"; break;
            case "txt" : case "html" : default : result = "Content-Type: text/html"; break;
        }
        return result;
    }

    void sendData(BufferedOutputStream writer, String responseCode, byte[] data, String contentType)
    {
        try {
            writer.write(("HTTP/1.1 " + responseCode + "\r\n").getBytes());
            writer.write(getServerTime().getBytes());
            writer.write((contentType + "\r\n\r\n").getBytes());
            writer.write(data);
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRequestedFileName(String request)
    {
        String fileName = null;
        String[] requestPieces = request.split(" ");
        if(requestPieces[0].equals("GET"))
        {
            fileName = requestPieces[1].substring(1);
        }
        return fileName.replace("%20", " ");
    }

    String getRequestType(String request)
    {
        if(request != null)
        {
            return request.split(" ")[0];
        }
        return "";
    }

    String getServerTime()
    {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "EEE, dd MMM yyyy HH:mm:ss z", Locale.ITALY);
        dateFormat.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormat.format(calendar.getTime());
    }

    void handleRequest(BufferedOutputStream writer, String request) throws IOException
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
                contentType = getContentType(requestedFile);
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
