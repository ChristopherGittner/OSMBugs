package org.gittner.osmbugs.statics;

import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OpenstreetmapNote;
import org.gittner.osmbugs.tasks.DownloadKeeprightBugsTask;
import org.gittner.osmbugs.tasks.DownloadMapdustBugsTask;
import org.gittner.osmbugs.tasks.DownloadOpenstreetmapNotesTask;
import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

/**
 * Created by christopher on 3/20/14.
 */
public class BugDatabase {

    public interface OnDownloadEndListener {
        public void onSuccess(int platform);

        public void onError(int platform);

        public void onCompletion();

        public void onProgressUpdate(double progress);
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

    public ArrayList<MapdustBug> getMapdustBugs() {
        return mMapdustBugs;
    }

    public ArrayList<OpenstreetmapNote> getOpenstreetmapNotes() {
        return mOpenstreetmapNotes;
    }

    /* Download a Bounding Box to the Database */
    public void DownloadBBox(BoundingBoxE6 bBox, final OnDownloadEndListener listener) {

        /* Only start Downloading if all Downloads finished */
        if(mActiveDownloads > 0)
            return;

        mActiveDownloads = 0;
        mCompletedDownloads = 0;

        /* Clear all Bugs */
        mKeeprightBugs.clear();
        mMapdustBugs.clear();
        mOpenstreetmapNotes.clear();

        if(Settings.Keepright.isEnabled()) {

            ++mActiveDownloads;

            new DownloadKeeprightBugsTask(){
                @Override
                protected void onPostExecute(ArrayList<KeeprightBug> bugs) {
                    ++mCompletedDownloads;

                    if(bugs != null) {
                        mKeeprightBugs = bugs;
                        listener.onSuccess(Globals.KEEPRIGHT);
                    }
                    else {
                        listener.onError(Globals.KEEPRIGHT);
                    }

                    checkProgress(listener);
                }
            }.execute(bBox);
        }

        if(Settings.Mapdust.isEnabled()) {

            ++mActiveDownloads;

            new DownloadMapdustBugsTask(){
                @Override
                protected void onPostExecute(ArrayList<MapdustBug> bugs) {
                    ++mCompletedDownloads;

                    if(bugs != null) {
                        mMapdustBugs = bugs;
                        listener.onSuccess(Globals.MAPDUST);
                    }
                    else {
                        listener.onError(Globals.MAPDUST);
                    }

                    checkProgress(listener);
                }
            }.execute(bBox);
        }

        if(Settings.OpenstreetmapNotes.isEnabled()) {

            ++mActiveDownloads;

            new DownloadOpenstreetmapNotesTask(){
                @Override
                protected void onPostExecute(ArrayList<OpenstreetmapNote> notes) {
                    ++mCompletedDownloads;

                    if(notes != null) {
                        mOpenstreetmapNotes = notes;
                        listener.onSuccess(Globals.OPENSTREETMAPNOTES);
                    }
                    else {
                        listener.onError(Globals.OPENSTREETMAPNOTES);
                    }

                    checkProgress(listener);
                }
            }.execute(bBox);
        }

        if(mActiveDownloads == 0)
            return;

        listener.onProgressUpdate(0);
    }

    /* Check if all Downloads completed and execute callbacks */
    private void checkProgress(OnDownloadEndListener listener) {
        listener.onProgressUpdate(((double)mCompletedDownloads) / ((double)mActiveDownloads));

        if(mCompletedDownloads == mActiveDownloads) {
            mActiveDownloads = 0;
            mCompletedDownloads = 0;
            listener.onCompletion();
        }
    }

    /* The Singletons Instance */
    private static BugDatabase mInstance = new BugDatabase();

    /* Number of currently active Downloads */
    private int mActiveDownloads = 0;

    /* Number of completed Downloads */
    private int mCompletedDownloads = 0;

    /* Holds all Keepright Bugs */
    private ArrayList<KeeprightBug> mKeeprightBugs = new ArrayList<KeeprightBug>();

    /* Holds all Mapdust Bugs */
    private ArrayList<MapdustBug> mMapdustBugs = new ArrayList<MapdustBug>();

    /* Holds all Openstreetmap Notes */
    private ArrayList<OpenstreetmapNote> mOpenstreetmapNotes = new ArrayList<OpenstreetmapNote>();
}
