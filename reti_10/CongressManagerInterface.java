import java.rmi.Remote;
import java.rmi.RemoteException;

public interface CongressManagerInterface extends Remote
{
    CongressDay getDailySpeakers(int day) throws RemoteException;
    boolean addSpeaker(int day, int session, String speakerName) throws RemoteException;
}
