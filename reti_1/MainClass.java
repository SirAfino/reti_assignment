public class MainClass
{
	public static void main(String[] args)
	{
		/* MainClass accuracy time_limit
		 * accuracy (double) = accuratezza del calcolo di pi greco
		 * time_limit (int) = tempo limite per il calcolo in millisecondi
		 */
		double accuracy = 0.00001;
		int time_limit = 200;
		if(args.length >= 2)
		{
			accuracy = Double.parseDouble(args[0]);
			time_limit = Integer.parseInt(args[1]);
		}
		Thread t = new Thread(new PiCalculator(accuracy));
		t.start();
		try
		{
			Thread.sleep(time_limit);
		}
		catch (InterruptedException e)
		{
			System.out.println("Sleep error!");
		}
		t.interrupt();
	}
}