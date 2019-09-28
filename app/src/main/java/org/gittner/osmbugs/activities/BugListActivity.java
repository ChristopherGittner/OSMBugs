package org.gittner.osmbugs.activities;

import android.content.Intent;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.fragments.BugPlatformFragment;
import org.gittner.osmbugs.fragments.BugPlatformFragment_;
import org.gittner.osmbugs.platforms.Platform;
import org.gittner.osmbugs.platforms.Platforms;
import org.gittner.osmbugs.statics.Settings;

import java.util.ArrayList;

@EActivity(R.layout.activity_bug_list)
public class BugListActivity
        extends AppCompatActivity
        implements BugPlatformFragment.OnFragmentInteractionListener
{
    public static final int RESULT_BUG_MINI_MAP_CLICKED = 1;

    public static final String RESULT_EXTRA_BUG = "RESULT_EXTRA_BUG";

    private static final int REQUEST_CODE_KEEPRIGHT_EDIT_ACTIVITY = 1;
    private static final int REQUEST_CODE_OSMOSE_EDIT_ACTIVITY = 2;
    private static final int REQUEST_CODE_MAPDUST_EDIT_ACTIVITY = 3;
    private static final int REQUEST_CODE_OSM_NOTE_EDIT_ACTIVITY = 4;

    @ViewById(R.id.pager)
    ViewPager mPager;
    @ViewById(R.id.tabPageIndicator)
    PagerTabStrip mPagerTabStrip;
    @ViewById(R.id.toolbar)
    Toolbar mToolbar;

    @AfterViews
    void init()
    {
        setSupportActionBar(mToolbar);

        PlatformPagerAdapter pagerAdapter = new PlatformPagerAdapter(getSupportFragmentManager());

        if (Settings.OsmNotes.isEnabled())
        {
            pagerAdapter.add(Platforms.OSM_NOTES);
        }
        if (Settings.Keepright.isEnabled())
        {
            pagerAdapter.add(Platforms.KEEPRIGHT);
        }
        if (Settings.Osmose.isEnabled())
        {
            pagerAdapter.add(Platforms.OSMOSE);
        }
        if (Settings.Mapdust.isEnabled())
        {
            pagerAdapter.add(Platforms.MAPDUST);
        }

        pagerAdapter.notifyDataSetChanged();

        mPager.setAdapter(pagerAdapter);
    }


    @OnActivityResult(REQUEST_CODE_KEEPRIGHT_EDIT_ACTIVITY)
    void onKeeprightEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            Platforms.KEEPRIGHT.getLoader().getQueue().add(Settings.getLastBBox());
        }
    }


    @OnActivityResult(REQUEST_CODE_OSMOSE_EDIT_ACTIVITY)
    void onOsmoseEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            Platforms.OSMOSE.getLoader().getQueue().add(Settings.getLastBBox());
        }
    }


    @OnActivityResult(REQUEST_CODE_MAPDUST_EDIT_ACTIVITY)
    void onMapdustEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            Platforms.MAPDUST.getLoader().getQueue().add(Settings.getLastBBox());
        }
    }


    @OnActivityResult(REQUEST_CODE_OSM_NOTE_EDIT_ACTIVITY)
    void onOsmNoteEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            Platforms.OSM_NOTES.getLoader().getQueue().add(Settings.getLastBBox());
        }
    }


    @Override
    public void onBugClicked(final Bug bug)
    {
        BugEditActivity_.intent(this)
                .mBug(bug)
                .startForResult(REQUEST_CODE_KEEPRIGHT_EDIT_ACTIVITY);
    }


    @Override
    public void onBugMiniMapClicked(final Bug bug)
    {
        Intent data = new Intent();
        data.putExtra(RESULT_EXTRA_BUG, bug);
        setResult(RESULT_BUG_MINI_MAP_CLICKED, data);
        finish();
    }


    private class PlatformPagerAdapter extends FragmentPagerAdapter
    {
        private final ArrayList<Platform> mPlatforms = new ArrayList<>();


        public PlatformPagerAdapter(final FragmentManager fm)
        {
            super(fm);
        }


        @Override
        public int getCount()
        {
            return mPlatforms.size();
        }


        @Override
        public CharSequence getPageTitle(final int position)
        {
            return mPlatforms.get(position).getName();
        }


        @Override
        public Fragment getItem(final int position)
        {
            return BugPlatformFragment_.builder()
                    .mArgPlatform(mPlatforms.get(position).getName())
                    .build();
        }


        public void add(Platform platform)
        {
            mPlatforms.add(platform);
        }
    }

    @OptionsItem(android.R.id.home)
    void onHomeBtn()
    {
        setResult(RESULT_CANCELED);
        finish();
    }
}
