package org.gittner.osmbugs.activities;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.fragments.SettingsFragment;

public class SettingsActivity extends ActionBarActivity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_settings);

        getFragmentManager().beginTransaction()
                .add(R.id.container, SettingsFragment.newInstance())
                .commit();
    }
}
