package org.gittner.osmbugs;

import android.os.AsyncTask;

import org.gittner.osmbugs.api.BugApi;
import org.gittner.osmbugs.bugs.Bug;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

public class BugDownloadTask<T extends Bug> extends AsyncTask<BoundingBoxE6, Void, ArrayList<T>>
{
	private BugApi<T> mApi = null;

	private ArrayList<T> mDestination = null;

	private final StatusListener mListener;
	private boolean mDownloadFinished = false;

	public interface StatusListener
	{
		void onCompletion();
		void onCancelled();
		void onError();
	}

	public BugDownloadTask(BugApi<T> api, ArrayList<T> destination, StatusListener listener)
	{
		mDestination = destination;
		mApi = api;
		mListener = listener;
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
		if(bugs != null)
		{
			mDestination.addAll(bugs);
			mListener.onCompletion();
		}
		else
		{
			mListener.onError();
		}
	}

	@Override
	protected void onCancelled()
	{
		mListener.onCancelled();
	}

	public boolean isDownloadFinished()
	{
		return mDownloadFinished;
	}
}
