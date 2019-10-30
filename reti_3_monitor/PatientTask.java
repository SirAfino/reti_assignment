import com.sun.tools.javac.Main;

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
        synchronized (MainClass.red_lock)
        {
            MainClass.red_waiting++;
        }
    }

    public void incrementOthersUsing()
    {
        synchronized (MainClass.others_lock)
        {
            MainClass.others_using++;
        }
    }

    public void decrementOthersUsing()
    {
        synchronized (MainClass.others_lock)
        {
            MainClass.others_using--;
            if(MainClass.others_using == 0)
            {
                MainClass.others_lock.notifyAll();
            }
        }
    }

    public void decrementRedWaiting()
    {
        synchronized (MainClass.red_lock)
        {
            MainClass.red_waiting--;
            if(MainClass.red_waiting == 0)
            {
                MainClass.red_lock.notifyAll();
            }
        }
    }

    public void incrementYellowWaiting(int d)
    {
        synchronized (MainClass.yellow_lock[d])
        {
            MainClass.yellow_waiting[d]++;
        }
    }

    public void decrementYellowWaiting(int d)
    {
        synchronized (MainClass.yellow_lock[d])
        {
            MainClass.yellow_waiting[d]--;
            if(MainClass.yellow_waiting[d] == 0)
            {
                MainClass.yellow_lock[d].notifyAll();
            }
        }
    }

    public void whitePatient(int requiredTime)
    {
        synchronized (MainClass.red_lock)
        {
            while(MainClass.red_waiting > 0)
            {
                try { MainClass.red_lock.wait(); }
                catch (InterruptedException e) {  e.printStackTrace(); }
            }
            incrementOthersUsing();
        }

        //while()

        decrementOthersUsing();
    }

    public void yellowPatient(int requiredTime)
    {
        int requiredDoctor = Math.abs(rndGenerator.nextInt()) % 10;
        incrementYellowWaiting(requiredDoctor);
        synchronized (MainClass.red_lock)
        {
            while(MainClass.red_waiting > 0)
            {
                try { MainClass.red_lock.wait(); }
                catch (InterruptedException e) {  e.printStackTrace(); }
            }
            incrementOthersUsing();
        }
        synchronized (MainClass.doctors[requiredDoctor])
        {
            //Simulating patient intervention
            printMessage("Lock doctor " + (requiredDoctor+1) + "(" + requiredTime + "ms)");
            Sleep(requiredTime);
            printMessage("Unlock doctor " + (requiredDoctor+1));
        }
        decrementOthersUsing();
        decrementYellowWaiting(requiredDoctor);
        return;
    }

    public void redPatient(int requiredTime)
    {
        incrementRedWaiting();
        synchronized (MainClass.others_lock)
        {
            while(MainClass.others_using > 0)
            {
                try { MainClass.others_lock.wait(); }
                catch (InterruptedException e) { e.printStackTrace(); }
            }
            synchronized (MainClass.doctors)
            {
                //Simulating patient intervention
                printMessage("Lock all doctors (" + requiredTime + "ms)");
                Sleep(requiredTime);
                printMessage("Unlock all doctors");
            }
        }
        decrementRedWaiting();
    }

    public void run()
    {
        for(int i=0;i<k;i++)
        {
            int requiredTime = (Math.abs(rndGenerator.nextInt()) % 3000);
            switch(code)
            {
                case WHITE: /*whitePatient(requiredTime);*/ break;
                case YELLOW: yellowPatient(requiredTime); break;
                case RED: redPatient(requiredTime); break;
            }
            Sleep((Math.abs(rndGenerator.nextInt()) % 3000) + 2000);
        }
    }
}
