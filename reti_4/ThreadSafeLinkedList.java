import java.util.LinkedList;

public class ThreadSafeLinkedList<E>
{
    //Represents a synchronized LinkedList(only needed operations have been implemented)

    private LinkedList<E> list;

    public ThreadSafeLinkedList()
    {
        list = new LinkedList<E>();
    }

    //Adds 'element' on the first position of the linked list and notifies notEmpty
    public synchronized void add(E element)
    {
        list.add(element);
        if(list.size() == 1)
        {
            notifyAll();
        }
        return;
    }

    //Returns and deletes(from the queue) the first element of the queue,
    //if the queue is empty waits a maximum of 'timeLimit' milliseconds for a new element
    public synchronized E pollBlocking(long timeLimit)
    {
        if(list.size() == 0)
        {
            try { wait(timeLimit); }
            catch (InterruptedException e) { e.printStackTrace(); }
        }
        return list.poll();
    }

    public synchronized int size()
    {
        return list.size();
    }
}
