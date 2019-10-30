import java.util.Random;

public class PatientTask implements Runnable
{
	Code code;
	int id;
	int k;
	Random rndGenerator;
	
	public PatientTask(Code code, int id, int k)
	{
		this.code = code;
		this.id = id;
		this.k = k;
		rndGenerator = new Random();
	}
	
	public void Sleep(int time)
	{
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	public void printMessage(String message)
	{
		System.out.println("[PATIENT " + id + "][" + code.toString() + "] " + message);
	}
	
	public void incrementRedWaiting()
	{
		MainClass.red_lock.lock();
		MainClass.red_waiting++;
		printMessage("Waiting for doctors");
		MainClass.red_lock.unlock();
	}

	public void decrementRedWaiting()
	{
		MainClass.red_lock.lock();
		MainClass.red_waiting--;
		if(MainClass.red_waiting == 0)
		{
			MainClass.red_cond.signalAll();
		}
		MainClass.red_lock.unlock();
	}
	
	public void incrementYellowWaiting(int i)
	{
		MainClass.yellow_lock[i].lock();
		MainClass.yellow_waiting[i]++;
		printMessage("Waiting for doctor " + (i+1));
		MainClass.yellow_lock[i].unlock();
	}
	
	public void decrementYellowWaiting(int i)
	{
		MainClass.yellow_lock[i].lock();
		MainClass.yellow_waiting[i]--;
		MainClass.yellow_lock[i].unlock();
	}
	
	public boolean tryLockWhitePatient(int i)
	{
		boolean flag = false;
		MainClass.yellow_lock[i].lock();
		if(MainClass.yellow_waiting[i] == 0)
		{
		    MainClass.doctors[i].lock();
		    flag = true;
		}
		MainClass.yellow_lock[i].unlock();
		return flag;
	}
	
	public void whitePatient(int requiredTime)
	{
		//Acquiring lock on any doctor
		int d=0;
		MainClass.red_lock.lock();
		do{
			d = (d+1) % 10;
			while(MainClass.red_waiting > 0)
			{
				try { MainClass.red_cond.await(); }
				catch (InterruptedException e) { e.printStackTrace(); }
			}
		}while(!tryLockWhitePatient(d));
		MainClass.red_lock.unlock();
		
		//Simulating patient intervention
		printMessage("Lock doctor " + (d+1) + "(" + requiredTime + "ms)");
		Sleep(requiredTime);
		printMessage("Unlock doctor " + (d+1));
		
		//Releasing lock on doctor
		MainClass.doctors[d].unlock();
		return;
	}
	
	public void yellowPatient(int requiredTime)
	{
		int requiredDoctor = Math.abs(rndGenerator.nextInt()) % 10;
		
		//Acquiring lock on "i" doctor if no red patient is waiting for it
		incrementYellowWaiting(requiredDoctor);
		MainClass.red_lock.lock();
		while(MainClass.red_waiting > 0)
		{
			try { MainClass.red_cond.await(); }
			catch (InterruptedException e) { e.printStackTrace(); }
		}
		MainClass.doctors[requiredDoctor].lock();
		MainClass.red_lock.unlock();
		decrementYellowWaiting(requiredDoctor);
		
		//Simulating patient intervention
		printMessage("Lock doctor " + (requiredDoctor+1) + "(" + requiredTime + "ms)");
		Sleep(requiredTime);
		printMessage("Unlock doctor " + (requiredDoctor+1));
		
		//Release locks on "i" doctors
		MainClass.doctors[requiredDoctor].unlock();
		return;
	}
	
	public void redPatient(int requiredTime)
	{
		//Acquire locks on all doctors;
		incrementRedWaiting();
		for(int j=0;j<10;j++)
		{
			MainClass.doctors[j].lock();
		}
		decrementRedWaiting();
		
		//Simulating patient intervention
		printMessage("Lock all doctors (" + requiredTime + "ms)");
		Sleep(requiredTime);
		printMessage("Unlock all doctor");
		
		//Release locks on all doctors
		for(int j=0;j<10;j++)
		{
			MainClass.doctors[j].unlock();
		}
		return;
	}
	
	public void run()
	{
		for(int i=0;i<k;i++)
		{
			int requiredTime = (Math.abs(rndGenerator.nextInt()) % 1000);
			switch(code)
			{
				case WHITE: whitePatient(requiredTime); break;
				case YELLOW: yellowPatient(requiredTime); break;
				case RED: redPatient(requiredTime); break;
			}
			Sleep((Math.abs(rndGenerator.nextInt()) % 3000) + 2000);
		}
	}
}
