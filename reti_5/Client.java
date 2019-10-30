import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

public class Client
{
    private String name;
    private ArrayList<Transaction> transactions;

    public Client(String name)
    {
        this.name = name;
        this.transactions = new ArrayList<>();
    }

    public void addTransaction(Transaction t)
    {
        transactions.add(t);
    }

    public JSONObject toJSON()
    {
        JSONObject clientJson = new JSONObject();
        JSONArray transactionsJson = new JSONArray();
        for(Transaction t : transactions)
        {
            transactionsJson.add(t.toJSON());
        }
        clientJson.put("name", name);
        clientJson.put("transactions", transactionsJson);
        return clientJson;
    }
}
