package org.gittner.osmbugs.activities;

import android.app.Fragment;
import android.support.v7.app.ActionBarActivity;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.fragments.SettingsFragment_;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends ActionBarActivity
{
    @AfterViews
    void init()
    {
        Fragment fragment = SettingsFragment_.builder().build();

        getFragmentManager().beginTransaction()
                .add(R.id.container, fragment)
                .commit();
    }
}
