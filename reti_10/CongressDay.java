import java.io.Serializable;

public class CongressDay implements Cloneable, Serializable
{
    private String[][] speakers;
    //Number of speakers for each session
    private int[] speakersNumber;

    public CongressDay()
    {
        speakers = new String[12][];
        speakersNumber = new int[12];
        for(int i=0;i<12;i++)
        {
            speakers[i] = new String[5];
            speakersNumber[i] = 0;
        }
    }

    public CongressDay(CongressDay d)
    {
        speakers = new String[12][];
        speakersNumber = new int[12];
        for(int i=0;i<12;i++)
        {
            speakers[i] = new String[5];
            String[] dSpeakers = d.getSessionSpeakers(i);
            speakersNumber[i] = d.getSpeakersNumberPerSession(i);
            for(int j=0;j<speakersNumber[i];j++)
            {
                speakers[i][j] = dSpeakers[j];
            }
        }
    }

    boolean addSpeaker(int session, String speakerName)
    {
        boolean result = false;
        int sessionSpeakerNumber = speakersNumber[session];
        if(sessionSpeakerNumber < 5)
        {
            speakers[session][sessionSpeakerNumber] = speakerName;
            speakersNumber[session]++;
            result = true;
        }
        return result;
    }

    int getSpeakersNumberPerSession(int session)
    {
        int result = 0;
        if(session >= 0 && session < 12)
        {
            result = speakersNumber[session];
        }
        return result;
    }

    String[] getSessionSpeakers(int session)
    {
        String[] result = null;
        if(session >= 0 && session < 12)
        {
            result = new String[12];
            for(int i=0;i<5;i++)
            {
                result[i] = speakers[session][i];
            }
        }
        return result;
    }

    @Override
    public String toString()
    {
        String result = "";
        for(int i=0;i<12;i++)
        {
            if(speakersNumber[i] > 0)
            {
                result += "Session " + (i+1) + ": ";
                for(int j=0;j<speakersNumber[i];j++)
                {
                    result += speakers[i][j] + " ";
                }
                result += "\n";
            }
        }
        return result;
    }
}
