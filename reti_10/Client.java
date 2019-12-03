import java.io.FileNotFoundException;
import java.io.FileReader;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.Random;
import java.util.Scanner;

public class Client
{
    static int port = 5000;
    static int speakersNumber = 10;

    public static void parseArguments(String filename)
    {
        try {
            Scanner s = new Scanner(new FileReader(filename));
            int p = s.nextInt();
            if(p > 1024 && p <= 65536)
            {
                port = p;
            }
            int n = s.nextInt();
            if(n > 0)
            {
                speakersNumber = n;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static boolean addRandomSpeaker(CongressManagerInterface manager) throws RemoteException
    {
        Random random = new Random();
        String[] names = {"Gabriele", "Alessandra", "Francesco", "Mario", "Antonio", "Giulia", "Marco", "Giorgia"};
        String name = names[random.nextInt(names.length)];
        int day = random.nextInt(3);
        int session = random.nextInt(12);
        return manager.addSpeaker(day, session, name);
    }

    public static void main(String args[]) throws RemoteException, NotBoundException
    {
        /* Client port speakersNumber
         * port (int) server port number
         * speakersNumber (int) number of random speakers to add
         */

        if(args.length > 0)
        {
            parseArguments(args[0]);
        }

        Registry registry = LocateRegistry.getRegistry(port);
        CongressManagerInterface manager = (CongressManagerInterface) registry.lookup("CongressManager");

        for(int i=0;i<speakersNumber;i++)
        {
            addRandomSpeaker(manager);
        }

        for(int i=0;i<3;i++)
        {
            CongressDay d = manager.getDailySpeakers(i);
            System.out.println("Day " + (i+1) + "\n" + d.toString() + "\n");
        }
    }
}
