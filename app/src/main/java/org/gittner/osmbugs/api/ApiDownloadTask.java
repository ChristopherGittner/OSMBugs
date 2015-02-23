package org.gittner.osmbugs.api;

import android.os.AsyncTask;

import org.gittner.osmbugs.bugs.Bug;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

public class ApiDownloadTask<T extends Bug> extends AsyncTask<BoundingBoxE6, Void, ArrayList<T>>
{
    private final CompletionListener<T> mCompletionListener;
    private final CancelledListener mCancelledListener;
    private final ErrorListener mErrorListener;

    private final BugApi<T> mApi;

    private boolean mDownloadFinished = false;


    public ApiDownloadTask(
            BugApi<T> api,
            CompletionListener<T> listener,
            CancelledListener cancelledListener,
            ErrorListener errorListener)
    {
        mApi = api;
        mCompletionListener = listener;
        mCancelledListener = cancelledListener;
        mErrorListener = errorListener;
    }


    @Override
    protected ArrayList<T> doInBackground(BoundingBoxE6... bBox)
    {
        return mApi.downloadBBox(bBox[0]);
    }


    @Override
    protected void onPostExecute(ArrayList<T> bugs)
    {
        mDownloadFinished = true;
        if (bugs != null)
        {
            mCompletionListener.onCompletion(bugs);
        }
        else if (mErrorListener != null)
        {
            mErrorListener.onError();
        }
    }


    @Override
    protected void onCancelled()
    {
        if (mCancelledListener != null)
        {
            mCancelledListener.onCancelled();
        }
    }


    public boolean isDownloadFinished()
    {
        return mDownloadFinished;
    }


    public interface CompletionListener<T extends Bug>
    {
        void onCompletion(ArrayList<T> bugs);
    }

    public interface CancelledListener
    {
        void onCancelled();
    }

    public interface ErrorListener
    {
        void onError();
    }
}
