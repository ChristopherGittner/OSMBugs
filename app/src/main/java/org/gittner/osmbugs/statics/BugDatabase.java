package org.gittner.osmbugs.statics;

import android.os.AsyncTask;

import org.gittner.osmbugs.BugDownloadTask;
import org.gittner.osmbugs.api.KeeprightApi;
import org.gittner.osmbugs.api.MapdustApi;
import org.gittner.osmbugs.api.OsmNotesApi;
import org.gittner.osmbugs.api.OsmoseApi;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

public class BugDatabase {

	public interface DatabaseWatcher
	{
        void onDatabaseUpdated(int platform);
		void onDownloadCancelled(int platform);
		void onDownloadError(int platform);
	}

    private static final BugDatabase mInstance = new BugDatabase();

    private final ArrayList<KeeprightBug> mKeeprightBugs = new ArrayList<>();
    private final ArrayList<OsmoseBug> mOsmoseBugs = new ArrayList<>();
    private final ArrayList<MapdustBug> mMapdustBugs = new ArrayList<>();
    private final ArrayList<OsmNote> mOsmNotes = new ArrayList<>();

    private final ArrayList<DatabaseWatcher> mDatabaseWatchers = new ArrayList<>();

	private BugDownloadTask<KeeprightBug> mKeeprightDownloadTask = null;
	private BugDownloadTask<OsmoseBug> mOsmoseDownloadTask = null;
	private BugDownloadTask<MapdustBug> mMapdustDownloadTask = null;
	private BugDownloadTask<OsmNote> mOsmNotesDownloadTask = null;

    public static BugDatabase getInstance() {
        return mInstance;
    }

    private BugDatabase() {
    }

    public ArrayList<KeeprightBug> getKeeprightBugs() {
        return mKeeprightBugs;
    }

    public ArrayList<OsmoseBug> getOsmoseBugs() {
        return mOsmoseBugs;
    }

    public ArrayList<MapdustBug> getMapdustBugs() {
        return mMapdustBugs;
    }

    public ArrayList<OsmNote> getOpenstreetmapNotes() {
        return mOsmNotes;
    }

    public void reload(final int platform) {
        load(Settings.getLastBBox(), platform);
    }

    public void load(final BoundingBoxE6 bBox, final int platform) {

        Settings.setLastBBox(bBox);

        switch (platform)
        {
            case Globals.KEEPRIGHT:
                loadKeepright(bBox);
                break;

            case Globals.OSMOSE:
                loadOsmose(bBox);
                break;

            case Globals.MAPDUST:
                loadMapdust(bBox);
                break;

            case Globals.OSM_NOTES:
                loadOsmNotes(bBox);
                break;
        }
    }

    private void loadKeepright(final BoundingBoxE6 bBox)
    {
		mKeeprightBugs.clear();
		notifyAllDatabaseUpdated(Globals.KEEPRIGHT);

		if(mKeeprightDownloadTask != null)
		{
			mKeeprightDownloadTask.cancel(true);
		}

		mKeeprightDownloadTask = new BugDownloadTask<>(
				new KeeprightApi(),
				mKeeprightBugs,
				new DownloadStatusListener(Globals.KEEPRIGHT));
		mKeeprightDownloadTask.execute(bBox);
    }

	private void loadOsmose(final BoundingBoxE6 bBox)
    {
		mOsmoseBugs.clear();
		notifyAllDatabaseUpdated(Globals.OSMOSE);

		if(mOsmoseDownloadTask != null)
		{
			mOsmoseDownloadTask.cancel(true);
		}

		mOsmoseDownloadTask = new BugDownloadTask<>(
				new OsmoseApi(),
				mOsmoseBugs,
				new DownloadStatusListener(Globals.OSMOSE));
		mOsmoseDownloadTask.execute(bBox);
    }

    private void loadMapdust(final BoundingBoxE6 bBox)
    {
		mMapdustBugs.clear();
		notifyAllDatabaseUpdated(Globals.MAPDUST);

		if(mMapdustDownloadTask != null)
		{
			mMapdustDownloadTask.cancel(true);
		}

		mMapdustDownloadTask = new BugDownloadTask<>(
				new MapdustApi(),
				mMapdustBugs,
				new DownloadStatusListener(Globals.MAPDUST));
		mMapdustDownloadTask.execute(bBox);
	}

    private void loadOsmNotes(final BoundingBoxE6 bBox)
    {
		mOsmNotes.clear();
		notifyAllDatabaseUpdated(Globals.OSM_NOTES);

		if(mOsmNotesDownloadTask != null)
		{
			mOsmNotesDownloadTask.cancel(true);
			mOsmNotesDownloadTask = null;
		}

		mOsmNotesDownloadTask = new BugDownloadTask<>(
				new OsmNotesApi(),
				mOsmNotes,
				new DownloadStatusListener(Globals.OSM_NOTES));
		mOsmNotesDownloadTask.execute(bBox);
    }

    private void notifyAllDatabaseUpdated(int platform)
    {
        for(DatabaseWatcher listener : mDatabaseWatchers)
        {
            listener.onDatabaseUpdated(platform);
        }
    }

	private void notifyAllDownloadCancelled(int platform)
	{
		for(DatabaseWatcher listener : mDatabaseWatchers)
		{
			listener.onDownloadCancelled(platform);
		}
	}

	private void notifyAllDownloadError(int platform)
	{
		for(DatabaseWatcher listener : mDatabaseWatchers)
		{
			listener.onDownloadError(platform);
		}
	}

    public void addDatabaseWatcher(DatabaseWatcher watcher)
    {
        mDatabaseWatchers.add(watcher);
    }

    public void removeDatabaseWatcher(DatabaseWatcher watcher)
    {
        mDatabaseWatchers.remove(watcher);
    }

	public boolean isDownloadRunning()
	{
		return mKeeprightDownloadTask != null && !mKeeprightDownloadTask.isCancelled() && mKeeprightDownloadTask.getStatus() != AsyncTask.Status.FINISHED
				|| mOsmoseDownloadTask != null && !mOsmoseDownloadTask.isCancelled() && mOsmoseDownloadTask.getStatus() != AsyncTask.Status.FINISHED
				|| mMapdustDownloadTask != null && !mMapdustDownloadTask.isCancelled() && mMapdustDownloadTask.getStatus() != AsyncTask.Status.FINISHED
				|| mOsmNotesDownloadTask != null && !mOsmNotesDownloadTask.isCancelled() && mOsmNotesDownloadTask.getStatus() != AsyncTask.Status.FINISHED;
	}

	private class DownloadStatusListener implements BugDownloadTask.StatusListener
	{
		private final int mPlatform;

		DownloadStatusListener(int platform)
		{
			mPlatform = platform;
		}

		@Override
		public void onCompletion()
		{
			notifyAllDatabaseUpdated(mPlatform);
		}

		@Override
		public void onCancelled()
		{
			notifyAllDownloadCancelled(mPlatform);
		}

		@Override
		public void onError()
		{
			notifyAllDownloadError(mPlatform);
		}
	}
}
