import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;

public class DoctorsMonitor
{
    public static Lock doctors[];
    public static Lock[] yellow_lock;
    public static Lock red_lock;
    public static Condition red_cond;
    public static int red_waiting;
    public static int yellow_waiting[];
}
