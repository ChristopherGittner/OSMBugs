package org.gittner.osmbugs.base;

import android.preference.PreferenceFragment;

import org.gittner.osmbugs.statics.OttoBus;

public class BasePreferenceFragment extends PreferenceFragment
{
    OttoBus mBus;


    @Override
    public void onResume()
    {
        super.onResume();

        mBus = OttoBus.getInstance();
        mBus.register(this);
    }


    @Override
    public void onPause()
    {
        super.onPause();

        mBus.unregister(this);
    }
}
