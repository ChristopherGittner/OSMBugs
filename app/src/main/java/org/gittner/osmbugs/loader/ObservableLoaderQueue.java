package org.gittner.osmbugs.loader;

public abstract class ObservableLoaderQueue<T>
{
    private QueueChangedListener mListener = null;


    public void setListener(QueueChangedListener listener)
    {
        mListener = listener;
    }


    public interface QueueChangedListener
    {
        void onChange();
    }


    public abstract void add(T newEntry);

    abstract boolean hasNext();

    public abstract T getNext();


    protected void notifyDataChanged()
    {
        if (mListener != null)
        {
            mListener.onChange();
        }
    }
}
