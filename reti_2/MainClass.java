import java.util.Vector;

//Versione facoltativa dell'assignment

public class MainClass
{
	public static void main(String[] args)
	{
		/* MainClass k opTime officeShutdownTime
		 * k (int) = dimensione della sala intermedia dell'ufficio postale
		 * opTime (int) = tempo totale di apertura dell'UP in secondi
		 * officeShutdownTime (int) = tempo massimo di idle di uno sportello prima della sua chiusura in millisecondi
		 */
		int k = 12;
		int opTime = 20;
		int officeShutdownTime = 3000;
		if(args.length >= 3)
		{
			k = Integer.parseInt(args[0]);
			opTime = Integer.parseInt(args[1]);
			officeShutdownTime = Integer.parseInt(args[2]);
		}
		
		Vector<ClientTask> hall = new Vector<ClientTask> (100,10);
		Thread scheduler = new Thread(new ClientScheduler(hall));
		Thread postOffice = new Thread(new PostOffice(hall, k, officeShutdownTime));
		System.out.println("POST OFFICE OPENED");
		scheduler.start();
		postOffice.start();
		try {
			Thread.sleep(opTime * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		scheduler.interrupt();
		postOffice.interrupt();
		System.out.println("POST OFFICE CLOSED");
	}
}