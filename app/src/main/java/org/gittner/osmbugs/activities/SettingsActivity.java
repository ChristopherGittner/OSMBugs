package org.gittner.osmbugs.activities;

import android.support.v7.app.ActionBarActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.fragments.SettingsFragment;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends ActionBarActivity
{
    @AfterViews
    void init()
    {
        getFragmentManager().beginTransaction()
                .add(R.id.container, SettingsFragment.newInstance())
                .commit();
    }
}
