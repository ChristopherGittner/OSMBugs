package org.gittner.osmbugs.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import com.rey.material.widget.TabPageIndicator;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
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
        extends ActionBarActivity
        implements ActionBar.TabListener,
        BugPlatformFragment.OnFragmentInteractionListener
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
    TabPageIndicator mTabPageIndicator;


    @AfterViews
    void init()
    {
        PlatformPagerAdapter pagerAdapter = new PlatformPagerAdapter(getFragmentManager());

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
        if (Settings.OsmNotes.isEnabled())
        {
            pagerAdapter.add(Platforms.OSM_NOTES);
        }

        pagerAdapter.notifyDataSetChanged();

        mPager.setAdapter(pagerAdapter);

        mTabPageIndicator.setViewPager(mPager);
        mTabPageIndicator.setOnPageChangeListener(new TabPageIndicator(this){});
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
    public void onTabSelected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction)
    {
        mPager.setCurrentItem(tab.getPosition());
    }


    @Override
    public void onTabUnselected(
            final ActionBar.Tab tab,
            final android.support.v4.app.FragmentTransaction fragmentTransaction)
    {
    }


    @Override
    public void onTabReselected(final ActionBar.Tab tab, final FragmentTransaction fragmentTransaction)
    {
    }


    @Override
    public void onBugClicked(final Bug bug)
    {
        Platform platform = bug.getPlatform();

        if (platform == Platforms.KEEPRIGHT)
        {
            startActivityForResult(bug.createEditor(this), REQUEST_CODE_KEEPRIGHT_EDIT_ACTIVITY);
        }
        else if (platform == Platforms.OSMOSE)
        {
            startActivityForResult(bug.createEditor(this), REQUEST_CODE_OSMOSE_EDIT_ACTIVITY);
        }
        else if (platform == Platforms.MAPDUST)
        {
            startActivityForResult(bug.createEditor(this), REQUEST_CODE_MAPDUST_EDIT_ACTIVITY);
        }
        else if (platform == Platforms.OSM_NOTES)
        {
            startActivityForResult(bug.createEditor(this), REQUEST_CODE_OSM_NOTE_EDIT_ACTIVITY);
        }
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
}
