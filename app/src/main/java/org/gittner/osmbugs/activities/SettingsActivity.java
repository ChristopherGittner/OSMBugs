package org.gittner.osmbugs.activities;

import android.app.Fragment;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.base.BaseActionBarActivity;
import org.gittner.osmbugs.fragments.SettingsFragment_;

@EActivity(R.layout.activity_settings)
public class SettingsActivity extends BaseActionBarActivity
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
