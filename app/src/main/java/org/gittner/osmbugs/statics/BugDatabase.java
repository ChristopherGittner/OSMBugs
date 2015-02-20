package org.gittner.osmbugs.statics;

import android.os.AsyncTask;

import org.gittner.osmbugs.BugDownloadTask;
import org.gittner.osmbugs.api.BugApi;
import org.gittner.osmbugs.api.KeeprightApi;
import org.gittner.osmbugs.api.MapdustApi;
import org.gittner.osmbugs.api.OsmNotesApi;
import org.gittner.osmbugs.api.OsmoseApi;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

public class BugDatabase
{
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


    private BugDatabase()
    {
    }


    public static BugDatabase getInstance()
    {
        return mInstance;
    }


    public ArrayList<KeeprightBug> getKeeprightBugs()
    {
        return mKeeprightBugs;
    }


    public ArrayList<OsmoseBug> getOsmoseBugs()
    {
        return mOsmoseBugs;
    }


    public ArrayList<MapdustBug> getMapdustBugs()
    {
        return mMapdustBugs;
    }


    public ArrayList<OsmNote> getOsmNotes()
    {
        return mOsmNotes;
    }


    public void reload(final int platform)
    {
        load(Settings.getLastBBox(), platform);
    }


    public void load(final BoundingBoxE6 bBox, final int platform)
    {
        Settings.setLastBBox(bBox);
        DownloadStatusListener listener = new DownloadStatusListener(platform);
        switch (platform)
        {
            case Globals.KEEPRIGHT:
                mKeeprightDownloadTask = loadBugs(
                        platform,
                        bBox,
                        mKeeprightDownloadTask,
                        new KeeprightApi(),
                        mKeeprightBugs,
                        listener);
                break;

            case Globals.OSMOSE:
                mOsmoseDownloadTask = loadBugs(
                        platform,
                        bBox,
                        mOsmoseDownloadTask,
                        new OsmoseApi(),
                        mOsmoseBugs,
                        listener);
                break;

            case Globals.MAPDUST:
                mMapdustDownloadTask = loadBugs(
                        platform,
                        bBox,
                        mMapdustDownloadTask,
                        new MapdustApi(),
                        mMapdustBugs,
                        listener);
                break;

            case Globals.OSM_NOTES:
                mOsmNotesDownloadTask = loadBugs(
                        platform,
                        bBox,
                        mOsmNotesDownloadTask,
                        new OsmNotesApi(),
                        mOsmNotes,
                        listener);
                break;
        }
    }


    private <T extends Bug> BugDownloadTask<T> loadBugs(
            final int platform,
            final BoundingBoxE6 bBox,
            BugDownloadTask<T> task,
            final BugApi<T> api,
            final ArrayList<T> destination,
            final BugDownloadTask.StatusListener listener)
    {
        destination.clear();

        notifyAllDatabaseUpdated(platform);

        if (task != null)
        {
            task.cancel(true);
        }
        task = new BugDownloadTask<>(api, destination, listener);
        task.execute(bBox);

        return task;
    }


    private void notifyAllDatabaseUpdated(int platform)
    {
        for (DatabaseWatcher listener : mDatabaseWatchers)
        {
            listener.onDatabaseUpdated(platform);
        }
    }


    private void notifyAllDownloadCancelled(int platform)
    {
        for (DatabaseWatcher listener : mDatabaseWatchers)
        {
            listener.onDownloadCancelled(platform);
        }
    }


    private void notifyAllDownloadError(int platform)
    {
        for (DatabaseWatcher listener : mDatabaseWatchers)
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
        return mKeeprightDownloadTask != null && !mKeeprightDownloadTask.isCancelled() && mKeeprightDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mKeeprightDownloadTask.isDownloadFinished()
                || mOsmoseDownloadTask != null && !mOsmoseDownloadTask.isCancelled() && mOsmoseDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mOsmoseDownloadTask.isDownloadFinished()
                || mMapdustDownloadTask != null && !mMapdustDownloadTask.isCancelled() && mMapdustDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mMapdustDownloadTask.isDownloadFinished()
                || mOsmNotesDownloadTask != null && !mOsmNotesDownloadTask.isCancelled() && mOsmNotesDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mOsmNotesDownloadTask.isDownloadFinished();
    }


    public interface DatabaseWatcher
    {
        void onDatabaseUpdated(int platform);

        void onDownloadCancelled(int platform);

        void onDownloadError(int platform);
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
