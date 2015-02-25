package org.gittner.osmbugs.statics;

import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.api.BackgroundExecutor;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.events.BugsChangedEvents;
import org.gittner.osmbugs.events.BugsDownloadFailedEvent;
import org.gittner.osmbugs.events.BugsDownloadingStateChangedEvent;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

@EBean(scope = EBean.Scope.Singleton)
public class BugDatabase
{
    private static final String TASK_ID_KEEPRIGHT = "TASK_ID_KEEPRIGHT";
    private static final String TASK_ID_OSMOSE = "TASK_ID_OSMOSE";
    private static final String TASK_ID_MAPDUST = "TASK_ID_MAPDUST";
    private static final String TASK_ID_OSM_NOTES = "TASK_ID_OSM_NOTES";

    private final ArrayList<KeeprightBug> mKeeprightBugs = new ArrayList<>();
    private final ArrayList<OsmoseBug> mOsmoseBugs = new ArrayList<>();
    private final ArrayList<MapdustBug> mMapdustBugs = new ArrayList<>();
    private final ArrayList<OsmNote> mOsmNotes = new ArrayList<>();

    private boolean mKeeprightDownloadRunning = false;
    private boolean mOsmoseDownloadRunning = false;
    private boolean mMapdustDownloadRunning = false;
    private boolean mOsmNotesDownloadRunning = false;

    private boolean mLastDownloadState;

    BugDatabase_ ref;


    public BugsChangedEvents.Keepright keeprightProducer()
    {
        return new BugsChangedEvents.Keepright(mKeeprightBugs);
    }


    public BugsChangedEvents.Osmose OsmoseProducer()
    {
        return new BugsChangedEvents.Osmose(mOsmoseBugs);
    }


    public BugsChangedEvents.Mapdust mapdustProducer()
    {
        return new BugsChangedEvents.Mapdust(mMapdustBugs);
    }


    public BugsChangedEvents.OsmNotes OsmNotesProducer()
    {
        return new BugsChangedEvents.OsmNotes(mOsmNotes);
    }


    public BugsDownloadingStateChangedEvent isDownloadRunning()
    {
        return new BugsDownloadingStateChangedEvent(getDownloadState());
    }


    public void load(final BoundingBoxE6 bBox, final int platform)
    {
        Settings.setLastBBox(bBox);

        switch (platform)
        {
            case Platforms.KEEPRIGHT:
                loadKeepright(bBox);
                break;

            case Platforms.OSMOSE:
                loadOsmose(bBox);
                break;

            case Platforms.MAPDUST:
                loadMapdust(bBox);
                break;

            case Platforms.OSM_NOTES:
                loadOsmNotes(bBox);
                break;
        }
    }


    void loadKeepright(final BoundingBoxE6 bBox)
    {
        mKeeprightBugs.clear();
        EventBus.getDefault().postSticky(new BugsChangedEvents.Keepright(mKeeprightBugs));

        BackgroundExecutor.cancelAll(TASK_ID_KEEPRIGHT, true);
        loadKeeprightTask(bBox);

        mKeeprightDownloadRunning = true;
        updateDownloadState();
    }


    @Background(id = TASK_ID_KEEPRIGHT)
    void loadKeeprightTask(final BoundingBoxE6 bBox)
    {
        ArrayList<KeeprightBug> bugs = Apis.KEEPRIGHT.downloadBBox(bBox);
        loadKeeprightDone(bugs);
    }


    @UiThread
    void loadKeeprightDone(final ArrayList<KeeprightBug> bugs)
    {
        mKeeprightDownloadRunning = false;

        if (bugs == null)
        {
            EventBus.getDefault().post(new BugsDownloadFailedEvent(Platforms.KEEPRIGHT));
            return;
        }

        mKeeprightBugs.clear();
        mKeeprightBugs.addAll(bugs);

        EventBus.getDefault().postSticky(new BugsChangedEvents.Keepright(mKeeprightBugs));

        updateDownloadState();
    }


    void loadOsmose(final BoundingBoxE6 bBox)
    {
        mOsmoseBugs.clear();
        EventBus.getDefault().postSticky(new BugsChangedEvents.Osmose(mOsmoseBugs));

        BackgroundExecutor.cancelAll(TASK_ID_OSMOSE, true);
        loadKeeprightTask(bBox);

        mOsmoseDownloadRunning = true;
        updateDownloadState();
    }


    @Background(id = TASK_ID_OSMOSE)
    void loadOsmoseTask(final BoundingBoxE6 bBox)
    {
        ArrayList<OsmoseBug> bugs = Apis.OSMOSE.downloadBBox(bBox);
        loadOsmoseDone(bugs);
    }


    @UiThread
    void loadOsmoseDone(final ArrayList<OsmoseBug> bugs)
    {
        mOsmoseDownloadRunning = false;

        if (bugs == null)
        {
            EventBus.getDefault().post(new BugsDownloadFailedEvent(Platforms.OSMOSE));
            return;
        }

        mOsmoseBugs.clear();
        mOsmoseBugs.addAll(bugs);

        EventBus.getDefault().postSticky(new BugsChangedEvents.Osmose(mOsmoseBugs));

        updateDownloadState();
    }


    void loadMapdust(final BoundingBoxE6 bBox)
    {
        mMapdustBugs.clear();
        EventBus.getDefault().postSticky(new BugsChangedEvents.Mapdust(mMapdustBugs));

        BackgroundExecutor.cancelAll(TASK_ID_MAPDUST, true);
        loadMapdustTask(bBox);

        mMapdustDownloadRunning = true;
        updateDownloadState();
    }


    @Background(id = TASK_ID_MAPDUST)
    void loadMapdustTask(final BoundingBoxE6 bBox)
    {
        ArrayList<MapdustBug> bugs = Apis.MAPDUST.downloadBBox(bBox);
        loadMapdustDone(bugs);
    }


    @UiThread
    void loadMapdustDone(final ArrayList<MapdustBug> bugs)
    {
        mMapdustDownloadRunning = false;

        if (bugs == null)
        {
            EventBus.getDefault().post(new BugsDownloadFailedEvent(Platforms.MAPDUST));
            return;
        }

        mMapdustBugs.clear();
        mMapdustBugs.addAll(bugs);

        EventBus.getDefault().postSticky(new BugsChangedEvents.Mapdust(mMapdustBugs));

        updateDownloadState();
    }


    void loadOsmNotes(final BoundingBoxE6 bBox)
    {
        mOsmNotes.clear();
        EventBus.getDefault().postSticky(new BugsChangedEvents.OsmNotes(mOsmNotes));

        BackgroundExecutor.cancelAll(TASK_ID_OSM_NOTES, true);
        loadOsmNotesTask(bBox);

        mOsmNotesDownloadRunning = true;
        updateDownloadState();
    }


    @Background(id = TASK_ID_OSM_NOTES)
    void loadOsmNotesTask(final BoundingBoxE6 bBox)
    {
        ArrayList<OsmNote> bugs = Apis.OSM_NOTES.downloadBBox(bBox);
        loadOsmNotesDone(bugs);
    }


    @UiThread
    void loadOsmNotesDone(final ArrayList<OsmNote> bugs)
    {
        mOsmNotesDownloadRunning = false;

        if (bugs == null)
        {
            EventBus.getDefault().post(new BugsDownloadFailedEvent(Platforms.OSMOSE));
            return;
        }

        mOsmNotes.clear();
        mOsmNotes.addAll(bugs);

        EventBus.getDefault().postSticky(new BugsChangedEvents.OsmNotes(mOsmNotes));

        updateDownloadState();
    }


    void updateDownloadState()
    {
        if (getDownloadState() != mLastDownloadState)
        {
            EventBus.getDefault().postSticky(new BugsDownloadingStateChangedEvent(getDownloadState()));
        }
        mLastDownloadState = getDownloadState();
    }


    boolean getDownloadState()
    {
        return mKeeprightDownloadRunning
                || mOsmoseDownloadRunning
                || mMapdustDownloadRunning
                || mOsmNotesDownloadRunning;
    }


    public void reload(final int platform)
    {
        load(Settings.getLastBBox(), platform);
    }
}
