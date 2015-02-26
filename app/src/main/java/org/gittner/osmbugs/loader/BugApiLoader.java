package org.gittner.osmbugs.loader;

import android.os.AsyncTask;

import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.events.BugsChangedEvent;
import org.gittner.osmbugs.platforms.Platform;
import org.gittner.osmbugs.statics.BackgroundTasks;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class BugApiLoader<TBug extends Bug> extends Loader<TBug>
{
    private LoaderAsyncTask mTask = null;


    public BugApiLoader(final Platform<TBug> platform)
    {
        super(platform);

        EventBus.getDefault().postSticky(new StateChangedEvent<>(mPlatform, STOPPED));
    }


    @Override
    public void load(final BoundingBoxE6 bBox)
    {
        if (mTask != null && !mTask.isComplete())
        {
            mTask.cancel(true);
            setState(CANCELLED);
        }

        mTask = new LoaderAsyncTask();
        mTask.executeOnExecutor(BackgroundTasks.getInstance(), bBox);
    }


    private class LoaderAsyncTask extends AsyncTask<BoundingBoxE6, Void, ArrayList<TBug>>
    {
        private boolean mComplete = false;


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
            mComplete = true;

            /* Create Event if the Download failed */
            if (bugs == null)
            {
                setState(FAILED);
                return;
            }

            /* Replace all Bugs and Notify Everyone */
            mBugs.clear();
            mBugs.addAll(bugs);

            EventBus.getDefault().post(new BugsChangedEvent<>(mPlatform));
            setState(STOPPED);
        }


        public boolean isComplete()
        {
            return mComplete;
        }
    }
}
