import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Transaction
{
    private Date date;
    private Causal causal;

    public Transaction(String date, Causal causal)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        this.date = new Date();
        try {
            this.date = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.causal = causal;
    }

    public Transaction(JSONObject transaction)
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        this.date = new Date();
        try {
            this.date = formatter.parse((String) transaction.get("date"));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.causal = Causal.valueOf((String) transaction.get("causal"));
    }

    public Date getDate()
    {
        return date;
    }

    public Causal getCausal()
    {
        return causal;
    }

    public JSONObject toJSON()
    {
        SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy");
        JSONObject json = new JSONObject();
        json.put("date", formatter.format(date));
        json.put("causal", causal.toString());
        return json;
    }
}
