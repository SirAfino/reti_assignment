import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PostOffice implements Runnable
{
	Vector<ClientTask> hall;
	int k;
	int officeShutdownTime;
	
	public PostOffice(Vector<ClientTask> hall, int k, int officeShutdownTime)
	{
		this.hall = hall;
		this.k = k;
		this.officeShutdownTime = officeShutdownTime;
	}
	
	public void run()
	{
		ThreadPoolExecutor officeExecutor = new ThreadPoolExecutor(4, 4, officeShutdownTime, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>());
		officeExecutor.allowCoreThreadTimeOut(true);
		while(!Thread.currentThread().isInterrupted())
		{
			if(officeExecutor.getQueue().size() < k && hall.size() > 0)
			{
				ClientTask task = hall.remove(0);
				System.out.println("Entered in hall client " + task.client_number);
				officeExecutor.execute(task);
			}
		}
		officeExecutor.shutdown();
	}
}
