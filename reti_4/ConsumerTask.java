import java.io.File;

public class ConsumerTask implements Runnable
{
    private boolean isDirectory(String pathname)
    {
        return (new File(pathname)).isDirectory();
    }

    private void printFolderContent(String folderPathname)
    {
        File folder = new File(folderPathname);
        for(String filename : folder.list())
        {
            String filePathname = folderPathname + "/" + filename;
            if(!isDirectory(filePathname))
            {
                System.out.println(filePathname);
            }
        }
        return;
    }

    public void run()
    {
        while(MainClass.run || MainClass.pathnameQueue.size()>0)
        {
            String folderPathname = MainClass.pathnameQueue.pollBlocking(100);
            if(folderPathname != null)
            {
                printFolderContent(folderPathname);
            }
        }
    }
}
