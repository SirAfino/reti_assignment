public class ClientTask implements Runnable
{
	int required_time;
	int client_number;

	public ClientTask(int required_time, int client_number)
	{
		this.required_time = required_time;
		this.client_number = client_number;
	}

	public void run()
	{
		System.out.println("Start client " + client_number);
		try
		{
			Thread.sleep(required_time);
		}
		catch (InterruptedException e)
		{
			e.printStackTrace();
		}
		System.out.println("End client " + client_number + "(" + required_time + " ms)");
	}
}