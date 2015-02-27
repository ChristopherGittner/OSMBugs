package org.gittner.osmbugs.loader;

import android.os.AsyncTask;

import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.events.BugsChangedEvent;
import org.gittner.osmbugs.platforms.Platform;
import org.gittner.osmbugs.statics.BackgroundTasks;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class Loader<TBug extends Bug>
{
    public static final int LOADING = 1;
    public static final int STOPPED = 2;
    public static final int FAILED = 3;

    private int mState = STOPPED;

    final Platform<TBug> mPlatform;
    final ArrayList<TBug> mBugs;

    private final ObservableLoaderQueue<BoundingBoxE6> mQueue;

    private LoaderAsyncTask mTask = null;


    public Loader(final ObservableLoaderQueue<BoundingBoxE6> queue, final Platform<TBug> platform)
    {
        mQueue = queue;

        mQueue.setListener(new ObservableLoaderQueue.QueueChangedListener()
        {
            @Override
            public void onChange()
            {
                checkQueue();
            }
        });

        mPlatform = platform;

        mBugs = platform.getBugs();

        EventBus.getDefault().postSticky(new StateChangedEvent<>(mPlatform, STOPPED));
    }


    public ObservableLoaderQueue<BoundingBoxE6> getQueue()
    {
        return mQueue;
    }


    /**
     * Check if another BoundingBox is queued and if so start to load
     */
    private void checkQueue()
    {
        if (mTask == null)
        {
            if (mQueue.hasNext())
            {
                mTask = new LoaderAsyncTask();
                mTask.executeOnExecutor(BackgroundTasks.getInstance(), mQueue.getNext());
            }
        }
    }


    protected void setState(int newState)
    {
        if (mState != newState)
        {
            mState = newState;

            EventBus.getDefault().post(new StateChangedEvent<>(mPlatform, newState));
        }
    }


    public int getState()
    {
        return mState;
    }


    private class LoaderAsyncTask extends AsyncTask<BoundingBoxE6, Void, ArrayList<TBug>>
    {
        @Override
        protected void onPreExecute()
        {
            setState(LOADING);
        }


        @Override
        protected ArrayList<TBug> doInBackground(final BoundingBoxE6... bBox)
        {
            return mPlatform.getApi().downloadBBox(bBox[0]);
        }


        @Override
        protected void onPostExecute(final ArrayList<TBug> bugs)
        {
            mTask = null;

            /* Check if Download successful */
            if (bugs != null)
            {
                /* Replace all Bugs and Notify Everyone */
                mBugs.clear();
                mBugs.addAll(bugs);

                EventBus.getDefault().post(new BugsChangedEvent<>(mPlatform));
            }
            else
            {
                setState(FAILED);
            }

            /* Check if there is still something in the queue */
            if (!mQueue.hasNext())
            {
                setState(STOPPED);
            }

            checkQueue();
        }
    }

    public class StateChangedEvent<TPlatform extends Platform>
    {
        private final TPlatform mPlatform;
        private final int mState;


        public StateChangedEvent(TPlatform platform, final int state)
        {
            mPlatform = platform;
            mState = state;
        }


        public TPlatform getPlatform()
        {
            return mPlatform;
        }


        public int getState()
        {
            return mState;
        }
    }
}
