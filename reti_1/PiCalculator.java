public class PiCalculator implements Runnable
{
	double accuracy;
	
	public PiCalculator(double accuracy)
	{
		this.accuracy = accuracy;
	}
	
	public void run()
	{
		double pi = 0;
		int den = 1;
		int sign = 1;
		boolean time_flag = false;
		do
		{
			pi += (4.0/den) * sign;
			sign *= -1;
			den += 2;
			time_flag = Thread.currentThread().isInterrupted();
		}while(Math.abs(pi - Math.PI) > accuracy && !time_flag);
		System.out.print("PI : " + pi);
		if(time_flag)
		{
			System.out.println(" (Interrupted)");
		}
		else
		{
			System.out.println(" (Accuracy)");
		}
	}
}