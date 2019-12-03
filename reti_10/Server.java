import java.io.FileNotFoundException;
import java.io.FileReader;
import java.rmi.AlreadyBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Scanner;

public class Server
{
    static int port = 5000;

    public static void parseArguments(String filename)
    {
        try {
            Scanner s = new Scanner(new FileReader(filename));
            int p = s.nextInt();
            if(p > 1024 && p <= 65536)
            {
                port = p;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws RemoteException, AlreadyBoundException
    {
        /* Server port
         * port (int) server port number
         */

        if(args.length > 0)
        {
            parseArguments(args[0]);
        }

        CongressManager manager = new CongressManager();
        LocateRegistry.createRegistry(port);
        Registry registry = LocateRegistry.getRegistry(port);
        registry.bind("CongressManager", manager);
    }
}
