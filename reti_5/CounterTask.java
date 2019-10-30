import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class CounterTask implements Runnable
{
    JSONObject client;

    public CounterTask(JSONObject client)
    {
        this.client = client;
    }

    public void run()
    {
        int[] counters = new int[Causal.values().length];
        for(int i=0;i<Causal.values().length;i++)
        {
            counters[i] = 0;
        }
        JSONArray transactions = (JSONArray) client.get("transactions");
        for(int i=0;i<transactions.size();i++)
        {
            Transaction t = new Transaction((JSONObject) transactions.get(i));
            counters[t.getCausal().ordinal()]++;
        }
        MainClass.countersLock.lock();
        for(int i=0;i<counters.length;i++)
        {
            MainClass.transactionCounters[i] += counters[i];
        }
        MainClass.countersLock.unlock();
    }
}
