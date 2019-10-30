import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class ReaderTask implements Runnable
{
    private String jsonPath;

    public ReaderTask(String jsonPath)
    {
        this.jsonPath = jsonPath;
    }

    public JSONObject readJSON(String path)
    {
        JSONObject json = null;
        try {
            String jsonString = "";
            String buffer;
            BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
            while((buffer = reader.readLine()) != null)
            {
                jsonString = jsonString + buffer;
            }
            JSONParser parser = new JSONParser();
            json = (JSONObject) parser.parse(jsonString);
            reader.close();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return json;
    }

    public void run()
    {
        JSONObject json = readJSON(jsonPath);
        JSONArray clientsJson = (JSONArray) json.get("clients");
        for(int i=0;i<clientsJson.size();i++)
        {
            JSONObject client = (JSONObject) clientsJson.get(i);
            MainClass.threadPoolExecutor.execute(new CounterTask(client));
        }
        System.out.println("Reader thread terminated : " + clientsJson.size() + " clients found");
        MainClass.endLocK.lock();
        MainClass.endFlag = true;
        MainClass.endCondition.signal();
        MainClass.endLocK.unlock();
    }
}
