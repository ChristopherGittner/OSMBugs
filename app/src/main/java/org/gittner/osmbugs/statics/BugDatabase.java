package org.gittner.osmbugs.statics;

import android.os.AsyncTask;

import com.squareup.otto.Produce;

import org.gittner.osmbugs.BugDownloadTask;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.api.BugApi;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.events.BugsChangedEvents;
import org.gittner.osmbugs.events.BugsDownloadCancelledEvent;
import org.gittner.osmbugs.events.BugsDownloadFailedEvent;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

public class BugDatabase
{
    private static BugDatabase mInstance = null;

    private final ArrayList<KeeprightBug> mKeeprightBugs = new ArrayList<>();
    private final ArrayList<OsmoseBug> mOsmoseBugs = new ArrayList<>();
    private final ArrayList<MapdustBug> mMapdustBugs = new ArrayList<>();
    private final ArrayList<OsmNote> mOsmNotes = new ArrayList<>();

    private BugDownloadTask<KeeprightBug> mKeeprightDownloadTask = null;
    private BugDownloadTask<OsmoseBug> mOsmoseDownloadTask = null;
    private BugDownloadTask<MapdustBug> mMapdustDownloadTask = null;
    private BugDownloadTask<OsmNote> mOsmNotesDownloadTask = null;


    private BugDatabase()
    {
    }


    public static BugDatabase getInstance()
    {
        if (mInstance == null)
        {
            mInstance = new BugDatabase();
            OttoBus.getInstance().register(mInstance);
        }

        return mInstance;
    }


    @Produce
    public BugsChangedEvents.Keepright keeprightProducer()
    {
        return new BugsChangedEvents.Keepright(mKeeprightBugs);
    }


    @Produce
    public BugsChangedEvents.Osmose OsmoseProducer()
    {
        return new BugsChangedEvents.Osmose(mOsmoseBugs);
    }


    @Produce
    public BugsChangedEvents.Mapdust mapdustProducer()
    {
        return new BugsChangedEvents.Mapdust(mMapdustBugs);
    }


    @Produce
    public BugsChangedEvents.OsmNotes OsmNotesProducer()
    {
        return new BugsChangedEvents.OsmNotes(mOsmNotes);
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
                        Apis.KEEPRIGHT,
                        mKeeprightBugs,
                        listener);
                break;

            case Globals.OSMOSE:
                mOsmoseDownloadTask = loadBugs(
                        platform,
                        bBox,
                        mOsmoseDownloadTask,
                        Apis.OSMOSE,
                        mOsmoseBugs,
                        listener);
                break;

            case Globals.MAPDUST:
                mMapdustDownloadTask = loadBugs(
                        platform,
                        bBox,
                        mMapdustDownloadTask,
                        Apis.MAPDUST,
                        mMapdustBugs,
                        listener);
                break;

            case Globals.OSM_NOTES:
                mOsmNotesDownloadTask = loadBugs(
                        platform,
                        bBox,
                        mOsmNotesDownloadTask,
                        Apis.OSM_NOTES,
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

        switch (platform)
        {
            case Globals.KEEPRIGHT:
                OttoBus.getInstance().post(new BugsChangedEvents.Keepright(mKeeprightBugs));
                break;

            case Globals.OSMOSE:
                OttoBus.getInstance().post(new BugsChangedEvents.Osmose(mOsmoseBugs));
                break;

            case Globals.MAPDUST:
                OttoBus.getInstance().post(new BugsChangedEvents.Mapdust(mMapdustBugs));
                break;

            case Globals.OSM_NOTES:
                OttoBus.getInstance().post(new BugsChangedEvents.OsmNotes(mOsmNotes));
                break;
        }

        if (task != null)
        {
            task.cancel(true);
        }
        task = new BugDownloadTask<>(api, destination, listener);
        task.execute(bBox);

        return task;
    }


    public boolean isDownloadRunning()
    {
        return mKeeprightDownloadTask != null && !mKeeprightDownloadTask.isCancelled() && mKeeprightDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mKeeprightDownloadTask.isDownloadFinished()
                || mOsmoseDownloadTask != null && !mOsmoseDownloadTask.isCancelled() && mOsmoseDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mOsmoseDownloadTask.isDownloadFinished()
                || mMapdustDownloadTask != null && !mMapdustDownloadTask.isCancelled() && mMapdustDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mMapdustDownloadTask.isDownloadFinished()
                || mOsmNotesDownloadTask != null && !mOsmNotesDownloadTask.isCancelled() && mOsmNotesDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mOsmNotesDownloadTask.isDownloadFinished();
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
            switch (mPlatform)
            {
                case Globals.KEEPRIGHT:
                    OttoBus.getInstance().post(new BugsChangedEvents.Keepright(mKeeprightBugs));
                    break;

                case Globals.OSMOSE:
                    OttoBus.getInstance().post(new BugsChangedEvents.Osmose(mOsmoseBugs));
                    break;

                case Globals.MAPDUST:
                    OttoBus.getInstance().post(new BugsChangedEvents.Mapdust(mMapdustBugs));
                    break;

                case Globals.OSM_NOTES:
                    OttoBus.getInstance().post(new BugsChangedEvents.OsmNotes(mOsmNotes));
                    break;
            }
        }


        @Override
        public void onCancelled()
        {
            OttoBus.getInstance().post(new BugsDownloadCancelledEvent(mPlatform));
        }


        @Override
        public void onError()
        {
            OttoBus.getInstance().post(new BugsDownloadFailedEvent(mPlatform));
        }
    }
}
