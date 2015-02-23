package org.gittner.osmbugs.statics;

import android.os.AsyncTask;

import com.squareup.otto.Produce;

import org.gittner.osmbugs.api.ApiDownloadTask;
import org.gittner.osmbugs.api.Apis;
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

    private ApiDownloadTask<KeeprightBug> mKeeprightDownloadTask = null;
    private ApiDownloadTask<OsmoseBug> mOsmoseDownloadTask = null;
    private ApiDownloadTask<MapdustBug> mMapdustDownloadTask = null;
    private ApiDownloadTask<OsmNote> mOsmNotesDownloadTask = null;


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


    public void reload(final int platform)
    {
        load(Settings.getLastBBox(), platform);
    }


    public void load(final BoundingBoxE6 bBox, final int platform)
    {
        Settings.setLastBBox(bBox);

        switch (platform)
        {
            case Globals.KEEPRIGHT:
                mKeeprightBugs.clear();

                OttoBus.getInstance().post(new BugsChangedEvents.Keepright(mKeeprightBugs));

                if (mKeeprightDownloadTask != null)
                {
                    mKeeprightDownloadTask.cancel(true);
                }
                mKeeprightDownloadTask = new ApiDownloadTask<>(
                        Apis.KEEPRIGHT,
                        new ApiDownloadTask.CompletionListener<KeeprightBug>()
                        {
                            @Override
                            public void onCompletion(ArrayList<KeeprightBug> bugs)
                            {
                                mKeeprightBugs.clear();
                                mKeeprightBugs.addAll(bugs);

                                OttoBus.getInstance().post(new BugsChangedEvents.Keepright(mKeeprightBugs));
                            }
                        },
                        new ApiDownloadTask.CancelledListener()
                        {
                            @Override
                            public void onCancelled()
                            {
                                OttoBus.getInstance().post(new BugsDownloadCancelledEvent(Globals.KEEPRIGHT));
                            }
                        },
                        new ApiDownloadTask.ErrorListener()
                        {
                            @Override
                            public void onError()
                            {
                                OttoBus.getInstance().post(new BugsDownloadFailedEvent(Globals.KEEPRIGHT));
                            }
                        });
                mKeeprightDownloadTask.execute(bBox);

                break;

            case Globals.OSMOSE:
                mOsmoseBugs.clear();

                OttoBus.getInstance().post(new BugsChangedEvents.Osmose(mOsmoseBugs));

                if (mOsmoseDownloadTask != null)
                {
                    mOsmoseDownloadTask.cancel(true);
                }
                mOsmoseDownloadTask = new ApiDownloadTask<>(
                        Apis.OSMOSE,
                        new ApiDownloadTask.CompletionListener<OsmoseBug>()
                        {
                            @Override
                            public void onCompletion(ArrayList<OsmoseBug> bugs)
                            {
                                mOsmoseBugs.clear();
                                mOsmoseBugs.addAll(bugs);

                                OttoBus.getInstance().post(new BugsChangedEvents.Osmose(mOsmoseBugs));
                            }
                        },
                        new ApiDownloadTask.CancelledListener()
                        {
                            @Override
                            public void onCancelled()
                            {
                                OttoBus.getInstance().post(new BugsDownloadCancelledEvent(Globals.OSMOSE));
                            }
                        },
                        new ApiDownloadTask.ErrorListener()
                        {
                            @Override
                            public void onError()
                            {
                                OttoBus.getInstance().post(new BugsDownloadFailedEvent(Globals.OSMOSE));
                            }
                        });
                mOsmoseDownloadTask.execute(bBox);
                break;

            case Globals.MAPDUST:
                mMapdustBugs.clear();

                OttoBus.getInstance().post(new BugsChangedEvents.Mapdust(mMapdustBugs));

                if (mMapdustDownloadTask != null)
                {
                    mMapdustDownloadTask.cancel(true);
                }
                mMapdustDownloadTask = new ApiDownloadTask<>(
                        Apis.MAPDUST,
                        new ApiDownloadTask.CompletionListener<MapdustBug>()
                        {
                            @Override
                            public void onCompletion(ArrayList<MapdustBug> bugs)
                            {
                                mMapdustBugs.clear();
                                mMapdustBugs.addAll(bugs);

                                OttoBus.getInstance().post(new BugsChangedEvents.Mapdust(mMapdustBugs));
                            }
                        },
                        new ApiDownloadTask.CancelledListener()
                        {
                            @Override
                            public void onCancelled()
                            {
                                OttoBus.getInstance().post(new BugsDownloadCancelledEvent(Globals.MAPDUST));
                            }
                        },
                        new ApiDownloadTask.ErrorListener()
                        {
                            @Override
                            public void onError()
                            {
                                OttoBus.getInstance().post(new BugsDownloadFailedEvent(Globals.MAPDUST));
                            }
                        });
                mMapdustDownloadTask.execute(bBox);
                break;

            case Globals.OSM_NOTES:
                mOsmNotes.clear();

                OttoBus.getInstance().post(new BugsChangedEvents.OsmNotes(mOsmNotes));

                if (mOsmNotesDownloadTask != null)
                {
                    mOsmNotesDownloadTask.cancel(true);
                }
                mOsmNotesDownloadTask = new ApiDownloadTask<>(
                        Apis.OSM_NOTES,
                        new ApiDownloadTask.CompletionListener<OsmNote>()
                        {
                            @Override
                            public void onCompletion(ArrayList<OsmNote> bugs)
                            {
                                mOsmNotes.clear();
                                mOsmNotes.addAll(bugs);

                                OttoBus.getInstance().post(new BugsChangedEvents.OsmNotes(mOsmNotes));
                            }
                        },
                        new ApiDownloadTask.CancelledListener()
                        {
                            @Override
                            public void onCancelled()
                            {
                                OttoBus.getInstance().post(new BugsDownloadCancelledEvent(Globals.OSM_NOTES));
                            }
                        },
                        new ApiDownloadTask.ErrorListener()
                        {
                            @Override
                            public void onError()
                            {
                                OttoBus.getInstance().post(new BugsDownloadFailedEvent(Globals.OSM_NOTES));
                            }
                        });
                mOsmNotesDownloadTask.execute(bBox);
                break;
        }
    }


    public boolean isDownloadRunning()
    {
        return mKeeprightDownloadTask != null && !mKeeprightDownloadTask.isCancelled() && mKeeprightDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mKeeprightDownloadTask.isDownloadFinished()
                || mOsmoseDownloadTask != null && !mOsmoseDownloadTask.isCancelled() && mOsmoseDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mOsmoseDownloadTask.isDownloadFinished()
                || mMapdustDownloadTask != null && !mMapdustDownloadTask.isCancelled() && mMapdustDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mMapdustDownloadTask.isDownloadFinished()
                || mOsmNotesDownloadTask != null && !mOsmNotesDownloadTask.isCancelled() && mOsmNotesDownloadTask.getStatus() != AsyncTask.Status.FINISHED && !mOsmNotesDownloadTask.isDownloadFinished();
    }
}
