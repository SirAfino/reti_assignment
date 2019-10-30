import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class MainClass
{
    public static ThreadPoolExecutor threadPoolExecutor;

    //This flag is set true when the ReaderThread finishes to read the JSON file
    public static boolean endFlag = false;
    public static Lock endLocK = new ReentrantLock();
    public static Condition endCondition = endLocK.newCondition();

    //Array of counters per transaction type, and associated Lock
    public static int[] transactionCounters = new int[Causal.values().length];
    public static Lock countersLock = new ReentrantLock();

    static int clientsNumber = 8;
    static int minTransactionPerClient = 10;
    static int maxTransactionPerClient = 15;
    static int threadsNumber = 5;

    public static String prettyJSON(JSONObject json)
    {
        String result = json.toString();
        result = result.replace("{", "{\n");
        result = result.replace("[", "[\n");
        result = result.replace(",", ",\n");
        result = result.replace("}", "}\n");
        result = result.replace("]", "]\n");
        return result;
    }

    public static void writeJSON(JSONObject json, String path)
    {
        try {
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
            writer.write(prettyJSON(json));
            writer.flush();
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Client[] randomClients(int n, int minT, int maxT)
    {
        String[] names = {"Gabriele", "Alessandra", "Francesco", "Mario", "Antonio", "Giulia", "Marco", "Giorgia"};
        n = Math.min(n, names.length);
        Client[] clients = new Client[n];
        for(int i=0;i<n;i++)
        {
            clients[i] = new Client(names[i]);
            Random random = new Random();
            int k = minT + random.nextInt(maxT - minT + 1);
            for(int j=0;j<k;j++)
            {
                int year = 2017 + random.nextInt(3);
                int month = 1 + random.nextInt(12);
                int day = 1 + random.nextInt(28);
                String date  = day + "-" + month + "-" + year;
                Causal causal = Causal.values()[random.nextInt(Causal.values().length)];
                clients[i].addTransaction(new Transaction(date, causal));
            }
        }
        return clients;
    }

    public static void generateRandomJSONFile(String path, int n, int minT, int maxT)
    {
        JSONObject clientsJson = new JSONObject();
        JSONArray clientsArrayJson = new JSONArray();
        Client[] clients = randomClients(n, minT, maxT);
        for(Client client : clients)
        {
            clientsArrayJson.add(client.toJSON());
        }
        clientsJson.put("clients", clientsArrayJson);
        writeJSON(clientsJson, path);
    }

    public static void waitReaderTermination()
    {
        try {
            endLocK.lock();
            while(!endFlag)
            {
                endCondition.await();
            }
            endLocK.unlock();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void parseArguments(String filename)
    {
        try {
            Scanner s = new Scanner(new FileReader(filename));
            clientsNumber = s.nextInt();
            minTransactionPerClient = s.nextInt();
            maxTransactionPerClient = s.nextInt();
            threadsNumber = s.nextInt();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) throws InterruptedException
    {
        /* MainClass clientsNumber minTransactionPerClient maxTransactionPerClient threadsNumber
         * clientsNumber (int) number of clients to be generated randomly
         * minTransactionPerClient (int) minimum number of transaction per client to be generated randomly
         * maxTransactionPerClient (int) maximum number of transaction per client to be generated randomly
         * threadsNumber (int) number of CounterThreads to be scheduled
         */
        if(args.length >= 1)
        {
            parseArguments(args[0]);
        }
        for(int i=0;i<Causal.values().length;i++)
        {
            transactionCounters[i] = 0;
        }
        generateRandomJSONFile("clients.json", clientsNumber, minTransactionPerClient, maxTransactionPerClient);
        threadPoolExecutor = new ThreadPoolExecutor(threadsNumber, threadsNumber, 0, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
        new Thread(new ReaderTask("clients.json")).start();
        System.out.println("Reader thread started");
        waitReaderTermination();
        threadPoolExecutor.shutdown();
        while(!threadPoolExecutor.isTerminated())
        {
            threadPoolExecutor.awaitTermination(10, TimeUnit.SECONDS);
        }
        System.out.println("Counter threads terminated");
        for(int i=0;i<transactionCounters.length;i++)
        {
            System.out.println("\t" + Causal.values()[i] + " : " + transactionCounters[i] + " transactions");
        }
    }
}
