import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class Calculator extends UnicastRemoteObject implements CalculatorInterface
{
    protected Calculator() throws RemoteException { }

    @Override
    public int sum(int a, int b)
    {
        return a + b;
    }

    @Override
    public int diff(int a, int b)
    {
        return a - b;
    }
}
