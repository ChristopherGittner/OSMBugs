package org.gittner.osmbugs.loader;

import java.util.LinkedList;

/**
 * A LoadingQueue implementation that will rotate the first Element out if capacity limit is reached
 *
 * @param <T>
 */
public class FixedSizeLoaderQueue<T> extends ObservableLoaderQueue<T>
{
    private final int mMaxSize;

    private LinkedList<T> mData = new LinkedList<>();


    public FixedSizeLoaderQueue(final int maxSize)
    {
        if (maxSize == 0)
        {
            throw new IllegalArgumentException("Illegal Max Size: " + maxSize);
        }

        mMaxSize = maxSize;
    }


    @Override
    public void add(final T newEntry)
    {
        if (mData.size() == mMaxSize)
        {
            mData.removeFirst();
        }

        mData.add(newEntry);

        notifyDataChanged();
    }


    @Override
    public boolean hasNext()
    {
        return mData.size() > 0;
    }


    @Override
    public T getNext()
    {
        T next = mData.getFirst();

        mData.removeFirst();
        notifyDataChanged();

        return next;
    }
}
