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

    private static final BugDatabase mInstance = new BugDatabase();

    private final ArrayList<KeeprightBug> mKeeprightBugs = new ArrayList<>();
    private final ArrayList<OsmoseBug> mOsmoseBugs = new ArrayList<>();
    private final ArrayList<MapdustBug> mMapdustBugs = new ArrayList<>();
    private final ArrayList<OsmNote> mOsmNotes = new ArrayList<>();

    private final ArrayList<DatabaseWatcher> mDatabaseWatchers = new ArrayList<>();

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

    public void reload(final int platform, final OnDownloadEndListener listener) {
        load(Settings.getLastBBox(), platform, listener);
    }

    public void load(final BoundingBoxE6 bBox, final int platform, final OnDownloadEndListener listener) {

        Settings.setLastBBox(bBox);

        switch (platform)
        {
            case Globals.KEEPRIGHT:
                loadKeepright(bBox, listener);
                break;

            case Globals.OSMOSE:
                loadOsmose(bBox, listener);
                break;

            case Globals.MAPDUST:
                loadMapdust(bBox, listener);
                break;

            case Globals.OSM_NOTES:
                loadOsmNotes(bBox, listener);
                break;
        }
    }

    private void loadKeepright(final BoundingBoxE6 bBox, final OnDownloadEndListener listener)
    {
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

                    notifyAllDatabaseUpdated(Globals.KEEPRIGHT);
                }
            }
        }.execute(bBox);
    }

    private void loadOsmose(final BoundingBoxE6 bBox, final OnDownloadEndListener listener)
    {
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

                    notifyAllDatabaseUpdated(Globals.OSMOSE);
                }
            }
        }.execute(bBox);
    }

    private void loadMapdust(final BoundingBoxE6 bBox, final OnDownloadEndListener listener)
    {
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

                    notifyAllDatabaseUpdated(Globals.MAPDUST);
                }
            }
        }.execute(bBox);
    }

    private void loadOsmNotes(final BoundingBoxE6 bBox, final OnDownloadEndListener listener)
    {
        new AsyncTask<BoundingBoxE6, Void, ArrayList<OsmNote>>() {
            @Override
            protected ArrayList<OsmNote> doInBackground(BoundingBoxE6... bBoxes) {
                return OsmNotesApi.downloadBBox(
                        bBoxes[0],
                        Settings.OsmNotes.getBugLimit(),
                        !Settings.OsmNotes.isShowOnlyOpenEnabled());
            }

            @Override
            protected void onPostExecute(ArrayList<OsmNote> osmNotes) {
                listener.onCompletion();

                if(osmNotes != null) {
                    mOsmNotes.clear();
                    mOsmNotes.addAll(osmNotes);

                    notifyAllDatabaseUpdated(Globals.OSM_NOTES);
                }
            }
        }.execute(bBox);
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
}
