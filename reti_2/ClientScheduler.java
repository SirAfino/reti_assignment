import java.util.Random;
import java.util.Vector;

public class ClientScheduler implements Runnable
{
	Vector<ClientTask> hall;

	public ClientScheduler(Vector<ClientTask> hall)
	{
		this.hall = hall;
	}

	public void run()
	{
		boolean exit_flag = false;
		Random rndGenerator = new Random();
		int i = 0;
		while(!exit_flag && !Thread.currentThread().isInterrupted())
		{
			try
			{
				Thread.sleep(Math.abs(rndGenerator.nextInt() % 3000));
				int t = Math.abs(rndGenerator.nextInt() % 2000);
				hall.add(new ClientTask(t, i));
				System.out.println("Client " + i + " has entered the post office");
				i++;
			}
			catch(InterruptedException e)
			{
				exit_flag = true;
			}
		}
	}
} 
