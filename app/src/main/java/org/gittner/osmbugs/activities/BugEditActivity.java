package org.gittner.osmbugs.activities;

import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.Helpers.GeoIntentStarter;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.fragments.KeeprightEditFragment_;
import org.gittner.osmbugs.fragments.MapdustEditFragment_;
import org.gittner.osmbugs.fragments.OsmNoteEditFragment_;
import org.gittner.osmbugs.fragments.OsmoseEditFragment_;
import org.gittner.osmbugs.platforms.Platforms;

@EActivity(R.layout.activity_bug_edit)
@OptionsMenu(R.menu.bug_edit)
public class BugEditActivity extends AppCompatActivity
{
    public static final String EXTRA_BUG = "ARG_BUG";

    @Extra(EXTRA_BUG)
    Bug mBug;

    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @AfterViews
    void init()
    {
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(mBug.getIcon());

        Fragment fragment = null;

        if (mBug.getPlatform() == Platforms.KEEPRIGHT)
        {
            fragment = KeeprightEditFragment_.builder()
                    .mBug((KeeprightBug) mBug)
                    .build();
        }
        else if (mBug.getPlatform() == Platforms.OSMOSE)
        {
            fragment = OsmoseEditFragment_.builder()
                    .mBug((OsmoseBug) mBug)
                    .build();
        }
        else if (mBug.getPlatform() == Platforms.MAPDUST)
        {
            fragment = MapdustEditFragment_.builder()
                    .mBug((MapdustBug) mBug)
                    .build();
        }
        else if (mBug.getPlatform() == Platforms.OSM_NOTES)
        {
            fragment = OsmNoteEditFragment_.builder()
                    .mBug((OsmNote) mBug)
                    .build();
        }

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.container, fragment)
                .commit();
    }


    @OptionsItem(R.id.action_share)
    void shareBug()
    {
        GeoIntentStarter.start(this, mBug.getPoint());
    }

    @OptionsItem(android.R.id.home)
    void onHomeBtn()
    {
        setResult(RESULT_CANCELED);
        finish();
    }
}
