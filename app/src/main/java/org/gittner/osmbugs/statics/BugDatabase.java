package org.gittner.osmbugs.statics;

import android.os.AsyncTask;

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

    public interface OnDownloadEndListener {
        public void onCompletion();
    }

    public interface DatabaseWatcher {

        public void onDatabaseUpdated(int platform);
    }

    /* Retrieve an Instance to the Bug Database */
    public static BugDatabase getInstance() {
        return mInstance;
    }

    /* Private Constructor since this is a Singleton */
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

    /* Download a Bounding Box to the Database */


    public void reload(final int platform, final OnDownloadEndListener listener) {
        reload(Settings.getLastBBox(), platform, listener);
    }

    public void reload(final BoundingBoxE6 bBox, final int platform, final OnDownloadEndListener listener) {

        Settings.setLastBBox(bBox);

        switch (platform)
        {
            case Globals.KEEPRIGHT:
                new AsyncTask<BoundingBoxE6, Void, ArrayList<KeeprightBug>>() {
                    @Override
                    protected ArrayList<KeeprightBug> doInBackground(BoundingBoxE6... bBoxes) {
                        return KeeprightApi.downloadBBox(bBoxes[0],
                                Settings.Keepright.isShowIgnoredEnabled(),
                                Settings.Keepright.isShowTempIgnoredEnabled(),
                                Settings.isLanguageGerman());
                    }

                    @Override
                    protected void onPostExecute(ArrayList<KeeprightBug> keeprightBugs) {
                        listener.onCompletion();

                        if(keeprightBugs != null) {
                            mKeeprightBugs.clear();
                            mKeeprightBugs.addAll(keeprightBugs);

                            notifyAllDatabaseUpdated(platform);
                        }
                    }
                }.execute(bBox);
                break;

            case Globals.OSMOSE:
                new AsyncTask<BoundingBoxE6, Void, ArrayList<OsmoseBug>>() {
                    @Override
                    protected ArrayList<OsmoseBug> doInBackground(BoundingBoxE6... bBoxes) {
                        return OsmoseApi.downloadBBox(bBoxes[0]);
                    }

                    @Override
                    protected void onPostExecute(ArrayList<OsmoseBug> osmoseBugs) {
                        listener.onCompletion();

                        if(osmoseBugs != null)
                        {
                            mOsmoseBugs.clear();
                            mOsmoseBugs.addAll(osmoseBugs);

                            notifyAllDatabaseUpdated(platform);
                        }
                    }
                }.execute(bBox);
                break;

            case Globals.MAPDUST:
                new AsyncTask<BoundingBoxE6, Void, ArrayList<MapdustBug>>() {
                    @Override
                    protected ArrayList<MapdustBug> doInBackground(BoundingBoxE6... bBoxes) {
                        return MapdustApi.downloadBBox(bBoxes[0]);
                    }

                    @Override
                    protected void onPostExecute(ArrayList<MapdustBug> mapdustBugs) {
                        listener.onCompletion();

                        if(mapdustBugs != null)
                        {
                            mMapdustBugs.clear();
                            mMapdustBugs.addAll(mapdustBugs);

                            notifyAllDatabaseUpdated(platform);
                        }
                    }
                }.execute(bBox);
                break;

            case Globals.OSM_NOTES:
                new AsyncTask<BoundingBoxE6, Void, ArrayList<OsmNote>>() {
                    @Override
                    protected ArrayList<OsmNote> doInBackground(BoundingBoxE6... bBoxes) {
                        return OsmNotesApi.downloadBBox(
                                bBoxes[0],
                                Settings.OpenstreetmapNotes.getBugLimit(),
                                !Settings.OpenstreetmapNotes.isShowOnlyOpenEnabled());
                    }

                    @Override
                    protected void onPostExecute(ArrayList<OsmNote> osmNotes) {
                        listener.onCompletion();

                        if(osmNotes != null) {
                            mOsmNotes.clear();
                            mOsmNotes.addAll(osmNotes);

                            notifyAllDatabaseUpdated(platform);
                        }
                    }
                }.execute(bBox);
                break;
        }
    }

    private void notifyAllDatabaseUpdated(int platform)
    {
        for(DatabaseWatcher listener : mDatabaseWatchers)
        {
            listener.onDatabaseUpdated(platform);
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

    /* The Singletons Instance */
    private static final BugDatabase mInstance = new BugDatabase();

    /* Holds all Keepright Bugs */
    private final ArrayList<KeeprightBug> mKeeprightBugs = new ArrayList<>();

    /* Holds all Osmose Bugs */
    private final ArrayList<OsmoseBug> mOsmoseBugs = new ArrayList<>();

    /* Holds all Mapdust Bugs */
    private final ArrayList<MapdustBug> mMapdustBugs = new ArrayList<>();

    /* Holds all Openstreetmap Notes */
    private final ArrayList<OsmNote> mOsmNotes = new ArrayList<>();

    /* All Registered Database Watchers */
    private final ArrayList<DatabaseWatcher> mDatabaseWatchers = new ArrayList<>();
}
