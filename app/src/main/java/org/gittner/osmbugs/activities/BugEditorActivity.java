package org.gittner.osmbugs.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.fragments.BugEditFragment;
import org.gittner.osmbugs.fragments.KeeprightEditFragment;
import org.gittner.osmbugs.fragments.MapdustEditFragment;
import org.gittner.osmbugs.fragments.OsmNoteEditFragment;
import org.gittner.osmbugs.fragments.OsmoseEditFragment;
import org.gittner.osmbugs.statics.Globals;

public class BugEditorActivity
        extends Activity
        implements BugEditFragment.FragmentInteractionListener {

    public static final int RESULT_SAVED_KEEPRIGHT = 1;
    public static final int RESULT_SAVED_OSMOSE = 2;
    public static final int RESULT_SAVED_MAPDUST = 3;
    public static final int RESULT_SAVED_OSM_NOTES = 4;

    /* Intent Extras Descriptions */
    public static final String EXTRA_BUG = "EXTRA_BUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bug_editor);

        Intent i = getIntent();

        Bug bug = i.getParcelableExtra(EXTRA_BUG);
        BugEditFragment f;

        if(bug instanceof KeeprightBug)
        {
            f = KeeprightEditFragment.newInstance((KeeprightBug) bug);
        }
        else if(bug instanceof OsmoseBug)
        {
            f = OsmoseEditFragment.newInstance((OsmoseBug) bug);
        }
        else if(bug instanceof MapdustBug)
        {
            f = MapdustEditFragment.newInstance((MapdustBug) bug);
        }
        else if(bug instanceof OsmNote)
        {
            f = OsmNoteEditFragment.newInstance((OsmNote) bug);
        }
        else
        {
            setResult(Activity.RESULT_CANCELED);
            finish();
            return;
        }

        getFragmentManager().beginTransaction()
                .add(R.id.container, f, f.getTag())
                .commit();
    }

    @Override
    public void onBugSaved(int platform) {
        switch (platform)
        {
            case Globals.KEEPRIGHT:
                setResult(RESULT_SAVED_KEEPRIGHT);
                break;
            case Globals.OSMOSE:
                setResult(RESULT_SAVED_OSMOSE);
                break;
            case Globals.MAPDUST:
                setResult(RESULT_SAVED_MAPDUST);
                break;
            case Globals.OSM_NOTES:
                setResult(RESULT_SAVED_OSM_NOTES);
                break;
        }

        finish();
    }
}
