package org.gittner.osmbugs.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.OnActivityResult;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.fragments.BugPlatformListFragment;
import org.gittner.osmbugs.fragments.BugPlatformListFragment_;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Platforms;
import org.gittner.osmbugs.statics.Settings;

import java.util.ArrayList;

@EActivity(R.layout.activity_bug_list)
public class BugListActivity
        extends ActionBarActivity
        implements ActionBar.TabListener,
        BugPlatformListFragment.OnFragmentInteractionListener
{
    public static final int RESULT_BUG_MINI_MAP_CLICKED = 1;

    public static final String RESULT_EXTRA_BUG = "RESULT_EXTRA_BUG";

    private static final int REQUEST_CODE_KEEPRIGHT_EDIT_ACTIVITY = 1;
    private static final int REQUEST_CODE_OSMOSE_EDIT_ACTIVITY = 2;
    private static final int REQUEST_CODE_MAPDUST_EDIT_ACTIVITY = 3;
    private static final int REQUEST_CODE_OSM_NOTE_EDIT_ACTIVITY = 4;

    @ViewById(R.id.pager)
    ViewPager mPager;

    @Bean
    BugDatabase mBugDatabase;


    @AfterViews
    void init()
    {
        final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

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


    @OnActivityResult(REQUEST_CODE_KEEPRIGHT_EDIT_ACTIVITY)
    void onKeeprightEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            mBugDatabase.reload(Platforms.KEEPRIGHT);
        }
    }


    @OnActivityResult(REQUEST_CODE_OSMOSE_EDIT_ACTIVITY)
    void onOsmoseEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            mBugDatabase.reload(Platforms.OSMOSE);
        }
    }


    @OnActivityResult(REQUEST_CODE_MAPDUST_EDIT_ACTIVITY)
    void onMapdustEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            mBugDatabase.reload(Platforms.MAPDUST);
        }
    }


    @OnActivityResult(REQUEST_CODE_OSM_NOTE_EDIT_ACTIVITY)
    void onOsmNoteEditActivityResult(int resultCode)
    {
        if (resultCode == RESULT_OK)
        {
            mBugDatabase.reload(Platforms.OSM_NOTES);
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
    public void onBugClicked(final Bug bug, int platform)
    {
        switch (platform)
        {
            case Platforms.KEEPRIGHT:
                KeeprightEditActivity_
                        .intent(this)
                        .extra(BugEditActivityConstants.EXTRA_BUG, bug)
                        .startForResult(REQUEST_CODE_KEEPRIGHT_EDIT_ACTIVITY);
                break;

            case Platforms.OSMOSE:
                OsmoseEditActivity_
                        .intent(this)
                        .extra(BugEditActivityConstants.EXTRA_BUG, bug)
                        .startForResult(REQUEST_CODE_OSMOSE_EDIT_ACTIVITY);
                break;

            case Platforms.MAPDUST:
                MapdustEditActivity_
                        .intent(this)
                        .extra(BugEditActivityConstants.EXTRA_BUG, bug)
                        .startForResult(REQUEST_CODE_MAPDUST_EDIT_ACTIVITY);
                break;

            case Platforms.OSM_NOTES:
                OsmNoteEditActivity_
                        .intent(this)
                        .extra(BugEditActivityConstants.EXTRA_BUG, bug)
                        .startForResult(REQUEST_CODE_OSM_NOTE_EDIT_ACTIVITY);
                break;
        }
    }


    @Override
    public void onBugMiniMapClicked(final Bug bug, int platform)
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
                case Platforms.KEEPRIGHT:
                    return getString(R.string.keepright);

                case Platforms.OSMOSE:
                    return getString(R.string.osmose);

                case Platforms.MAPDUST:
                    return getString(R.string.mapdust);

                case Platforms.OSM_NOTES:
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
