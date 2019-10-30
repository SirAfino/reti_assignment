import java.util.concurrent.locks.*;

public class MainClass
{
	public static Lock doctors[] = new ReentrantLock[10];
	public static Lock[] yellow_lock = new ReentrantLock[10];
	public static Lock red_lock = new ReentrantLock();
	public static Condition red_cond = red_lock.newCondition();
	public static int red_waiting = 0;
	public static int yellow_waiting[] = new int[10];
	
	public static void main(String args[])
	{
		int white = 5;
		int yellow = 5;
		int red = 5;

		//Arrays initialization
		for(int i=0;i<10;i++)
		{
			doctors[i] = new ReentrantLock();
			yellow_lock[i] = new ReentrantLock();
			yellow_waiting[i] = 0;
		}

		//Arguments parsing
		if(args.length >= 3)
		{
			white = Integer.parseInt(args[0]);
			yellow = Integer.parseInt(args[1]);
			red = Integer.parseInt(args[1]);
		}
		
		//Patient scheduling
		int i=0;
		while(yellow + white + red > 0)
		{
			switch(i%3)
			{
				case 0:
					if(white > 0)
					{
						new Thread(new PatientTask(Code.WHITE, i+1, 3)).start();
						white--;
					}
					break;
				case 1:
					if(yellow > 0)
					{
						new Thread(new PatientTask(Code.YELLOW, i+1, 3)).start();
						yellow--;
					}
					break;
				case 2:
					if(red > 0)
					{
						new Thread(new PatientTask(Code.RED, i+1, 3)).start();
						red--;
					}
					break;
			}
			i++;
		}
	}
}