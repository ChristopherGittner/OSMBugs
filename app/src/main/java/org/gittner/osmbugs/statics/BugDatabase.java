package org.gittner.osmbugs.statics;

import android.os.AsyncTask;
import android.util.Log;

import org.gittner.osmbugs.api.KeeprightApi;
import org.gittner.osmbugs.api.MapdustApi;
import org.gittner.osmbugs.api.OpenstreetmapNotesApi;
import org.gittner.osmbugs.api.OsmoseApi;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OpenstreetmapNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

public class BugDatabase {

    public interface OnDownloadEndListener {
        public void onSuccess(int platform);

        public void onError(int platform);

        public void onCompletion();

        public void onProgressUpdate(double progress);
    }

    public interface DatabaseWatcher {

        public void onDatabaseUpdated(int platform);

        public void onDatabaseCleared();
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

    public ArrayList<OpenstreetmapNote> getOpenstreetmapNotes() {
        return mOpenstreetmapNotes;
    }

    /* Download a Bounding Box to the Database */


    public void Reload(final OnDownloadEndListener listener) {
        DownloadBBox(Settings.getLastBBox(), listener);
    }

    public void DownloadBBox(final BoundingBoxE6 bBox, final OnDownloadEndListener listener) {
        /* Only start Downloading if all Downloads finished */
        if (mActiveDownloads > 0)
            return;

        Settings.setLastBBox(bBox);

        Log.w("", bBox.toString());

        mActiveDownloads = 0;
        mCompletedDownloads = 0;

        /* Clear all Bugs */
        mKeeprightBugs.clear();
        mOsmoseBugs.clear();
        mMapdustBugs.clear();
        mOpenstreetmapNotes.clear();

        for(DatabaseWatcher watcher : mDatabaseWatchers)
        {
            watcher.onDatabaseCleared();
        }

        if (Settings.Keepright.isEnabled()) {

            ++mActiveDownloads;

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
                    ++mCompletedDownloads;

                    if (keeprightBugs != null) {
                        mKeeprightBugs = keeprightBugs;
                        listener.onSuccess(Globals.KEEPRIGHT);
                    } else {
                        listener.onError(Globals.KEEPRIGHT);
                    }

                    checkProgress(listener);
                }
            }.execute(bBox);
        }

        if (Settings.Osmose.isEnabled()) {

            ++mActiveDownloads;

            new AsyncTask<BoundingBoxE6, Void, ArrayList<OsmoseBug>>() {
                @Override
                protected ArrayList<OsmoseBug> doInBackground(BoundingBoxE6... bBoxes) {
                    return OsmoseApi.downloadBBox(bBoxes[0]);
                }

                @Override
                protected void onPostExecute(ArrayList<OsmoseBug> osmoseBugs) {
                    ++mCompletedDownloads;

                    if (osmoseBugs != null) {
                        mOsmoseBugs = osmoseBugs;
                        listener.onSuccess(Globals.OSMOSE);
                    } else {
                        listener.onError(Globals.OSMOSE);
                    }

                    checkProgress(listener);
                }
            }.execute(bBox);
        }

        if (Settings.Mapdust.isEnabled()) {

            ++mActiveDownloads;

            new AsyncTask<BoundingBoxE6, Void, ArrayList<MapdustBug>>() {
                @Override
                protected ArrayList<MapdustBug> doInBackground(BoundingBoxE6... bBoxes) {
                    return MapdustApi.downloadBBox(bBoxes[0]);
                }

                @Override
                protected void onPostExecute(ArrayList<MapdustBug> mapdustBugs) {
                    ++mCompletedDownloads;

                    if (mapdustBugs != null) {
                        mMapdustBugs = mapdustBugs;
                        listener.onSuccess(Globals.MAPDUST);
                    } else {
                        listener.onError(Globals.MAPDUST);
                    }

                    checkProgress(listener);
                }
            }.execute(bBox);
        }

        if (Settings.OpenstreetmapNotes.isEnabled()) {

            ++mActiveDownloads;

            new AsyncTask<BoundingBoxE6, Void, ArrayList<OpenstreetmapNote>>() {
                @Override
                protected ArrayList<OpenstreetmapNote> doInBackground(BoundingBoxE6... bBoxes) {
                    return OpenstreetmapNotesApi.downloadBBox(
                            bBoxes[0],
                            Settings.OpenstreetmapNotes.getBugLimit(),
                            !Settings.OpenstreetmapNotes.isShowOnlyOpenEnabled());
                }

                @Override
                protected void onPostExecute(ArrayList<OpenstreetmapNote> openstreetmapNotes) {
                    ++mCompletedDownloads;

                    if (openstreetmapNotes != null) {
                        mOpenstreetmapNotes = openstreetmapNotes;
                        listener.onSuccess(Globals.OPENSTREETMAPNOTES);
                    } else {
                        listener.onError(Globals.OPENSTREETMAPNOTES);
                    }

                    checkProgress(listener);
                }
            }.execute(bBox);
        }

        if (mActiveDownloads == 0)
            return;

        listener.onProgressUpdate(0);
    }

    /* Check if all Downloads completed and execute callbacks */
    private void checkProgress(OnDownloadEndListener listener) {
        listener.onProgressUpdate(((double) mCompletedDownloads) / ((double) mActiveDownloads));

        if (mCompletedDownloads == mActiveDownloads) {
            mActiveDownloads = 0;
            mCompletedDownloads = 0;
            listener.onCompletion();
            for(DatabaseWatcher watcher : mDatabaseWatchers)
            {
                watcher.onDatabaseUpdated(Globals.KEEPRIGHT);
                watcher.onDatabaseUpdated(Globals.OSMOSE);
                watcher.onDatabaseUpdated(Globals.MAPDUST);
                watcher.onDatabaseUpdated(Globals.OPENSTREETMAPNOTES);
            }
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
    private static BugDatabase mInstance = new BugDatabase();

    /* Number of currently active Downloads */
    private int mActiveDownloads = 0;

    /* Number of completed Downloads */
    private int mCompletedDownloads = 0;

    /* Holds all Keepright Bugs */
    private ArrayList<KeeprightBug> mKeeprightBugs = new ArrayList<>();

    /* Holds all Osmose Bugs */
    private ArrayList<OsmoseBug> mOsmoseBugs = new ArrayList<>();

    /* Holds all Mapdust Bugs */
    private ArrayList<MapdustBug> mMapdustBugs = new ArrayList<>();

    /* Holds all Openstreetmap Notes */
    private ArrayList<OpenstreetmapNote> mOpenstreetmapNotes = new ArrayList<>();

    /* All Registered Database Watchers */
    private ArrayList<DatabaseWatcher> mDatabaseWatchers = new ArrayList<>();
}
