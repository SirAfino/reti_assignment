import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class CongressManager extends UnicastRemoteObject implements CongressManagerInterface
{
    CongressDay[] congressDays;

    protected CongressManager() throws RemoteException
    {
        congressDays = new CongressDay[3];
        for(int i=0;i<3;i++)
        {
            congressDays[i] = new CongressDay();
        }
    }

    @Override
    public CongressDay getDailySpeakers(int day) throws RemoteException
    {
        CongressDay result = null;
        if(day >= 0 && day < 3)
        {
            result = new CongressDay(congressDays[day]);
        }
        return result;
    }

    @Override
    public boolean addSpeaker(int day, int session, String speakerName) throws RemoteException
    {
        boolean result = false;
        if(day >= 0 && day < 3)
        {
            result = congressDays[day].addSpeaker(session, speakerName);
        }
        return result;
    }
}
