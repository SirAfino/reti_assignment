import java.io.File;

public class ProducerTask implements Runnable
{
    String initialPathname;

    public ProducerTask(String pathname)
    {
        this.initialPathname = pathname;
    }

    private boolean isDirectory(String pathname)
    {
        return (new File(pathname)).isDirectory();
    }

    private void analyzeFolder(String folderPathname)
    {
        File folder = new File(folderPathname);
        for(String filename : folder.list())
        {
            String filePathname = folderPathname + "/" + filename;
            if(isDirectory(filePathname))
            {
                analyzeFolder(filePathname);
            }
        }
        MainClass.pathnameQueue.add(folderPathname);
        return;
    }

    public void run()
    {
        if(isDirectory(initialPathname))
        {
            analyzeFolder(initialPathname);
        }
        MainClass.run = false;
    }
}
