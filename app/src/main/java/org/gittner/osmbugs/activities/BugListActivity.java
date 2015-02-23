package org.gittner.osmbugs.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.base.BaseActionBarActivity;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.fragments.BugPlatformListFragment;
import org.gittner.osmbugs.fragments.BugPlatformListFragment_;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Globals;
import org.gittner.osmbugs.statics.Settings;

import java.util.ArrayList;

@EActivity(R.layout.activity_bug_list)
public class BugListActivity
        extends BaseActionBarActivity
        implements ActionBar.TabListener,
        BugPlatformListFragment.OnFragmentInteractionListener
{
    public static final int RESULT_BUG_MINI_MAP_CLICKED = 1;

    public static final String RESULT_EXTRA_BUG = "RESULT_EXTRA_BUG";

    private static final int REQUEST_CODE_BUG_EDITOR_ACTIVITY = 1;

    @ViewById(R.id.pager)
    ViewPager mPager;


    @AfterViews
    void init()
    {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        PlatformPagerAdapter pagerAdapter = new PlatformPagerAdapter(getFragmentManager());

        if (Settings.Keepright.isEnabled())
        {
            pagerAdapter.add(Globals.KEEPRIGHT);
        }
        if (Settings.Osmose.isEnabled())
        {
            pagerAdapter.add(Globals.OSMOSE);
        }
        if (Settings.Mapdust.isEnabled())
        {
            pagerAdapter.add(Globals.MAPDUST);
        }
        if (Settings.OsmNotes.isEnabled())
        {
            pagerAdapter.add(Globals.OSM_NOTES);
        }

        pagerAdapter.notifyDataSetChanged();

        mPager.setAdapter(pagerAdapter);
        mPager.setOnPageChangeListener(
                new ViewPager.SimpleOnPageChangeListener()
                {
                    @Override
                    public void onPageSelected(final int position)
                    {
                        actionBar.setSelectedNavigationItem(position);
                    }
                });

        for (int i = 0; i < mPager.getAdapter().getCount(); i++)
        {
            actionBar.addTab(
                    actionBar.newTab().setText(mPager.getAdapter().getPageTitle(i)).setTabListener(this));
        }
    }


    @OnActivityResult(REQUEST_CODE_BUG_EDITOR_ACTIVITY)
    void onBugEditorActivityResult(int resultCode)
    {
        switch (resultCode)
        {
            case BugEditActivityConstants.RESULT_SAVED_KEEPRIGHT:
                BugDatabase.getInstance().reload(Globals.KEEPRIGHT);
                break;

            case BugEditActivityConstants.RESULT_SAVED_OSMOSE:
                BugDatabase.getInstance().reload(Globals.OSMOSE);
                break;

            case BugEditActivityConstants.RESULT_SAVED_MAPDUST:
                BugDatabase.getInstance().reload(Globals.MAPDUST);
                break;

            case BugEditActivityConstants.RESULT_SAVED_OSM_NOTES:
                BugDatabase.getInstance().reload(Globals.OSM_NOTES);
                break;
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
        /* Open the selected Bug in the Bug Editor */
        Intent i = new Intent(BugListActivity.this, bug.getEditorClass());
        i.putExtra(BugEditActivityConstants.EXTRA_BUG, bug);
        startActivityForResult(i, REQUEST_CODE_BUG_EDITOR_ACTIVITY);
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
        private final ArrayList<Integer> mPlatforms = new ArrayList<>();


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
            switch (mPlatforms.get(position))
            {
                case Globals.KEEPRIGHT:
                    return getString(R.string.keepright);

                case Globals.OSMOSE:
                    return getString(R.string.osmose);

                case Globals.MAPDUST:
                    return getString(R.string.mapdust);

                case Globals.OSM_NOTES:
                    return getString(R.string.openstreetmap_notes);
            }
            return null;
        }


        @Override
        public Fragment getItem(final int position)
        {
            return BugPlatformListFragment_.builder()
                    .mPlatform(mPlatforms.get(position))
                    .build();
        }


        public void add(int platform)
        {
            mPlatforms.add(platform);
        }
    }
}
