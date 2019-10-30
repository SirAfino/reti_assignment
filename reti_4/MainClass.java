import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

public class MainClass
{
    static String pathname = "./folder1";
    static int k = 3;

    //Shared queue for folders names
    public static ThreadSafeLinkedList<String> pathnameQueue;

    //The 'run' flag is set 'false' by the producer thread when all the folders have been inserted in the queue
    public static boolean run = true;

    public static void parseArguments(String filename)
    {
        try {
            Scanner s = new Scanner(new FileReader(filename));
            pathname = s.next();
            k = s.nextInt();
            s.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return;
    }

    public static void main(String args[])
    {
        /* MainClass pathname k
         * pathname (String) directory to analyze
         * k (int) number of consumer threads to be scheduled
         */

        pathnameQueue = new ThreadSafeLinkedList<String>();

        //Arguments parsing
        if(args.length >= 1)
        {
            parseArguments(args[0]);
        }

        System.out.println("CONTENT OF '" + pathname + "' AND ITS SUBDIRECTORIES\n");

        //Scheduling of the one Producer and k Consumers
        new Thread(new ProducerTask(pathname)).start();
        for(int i=0;i<k;i++)
        {
            new Thread(new ConsumerTask()).start();
        }
    }
}
