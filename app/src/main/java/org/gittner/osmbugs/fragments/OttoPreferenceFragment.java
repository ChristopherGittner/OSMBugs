package org.gittner.osmbugs.fragments;

import android.preference.PreferenceFragment;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.gittner.osmbugs.statics.OttoBus;

@EBean
public class OttoPreferenceFragment extends PreferenceFragment
{
    @Bean
    OttoBus mBus;


    @Override
    public void onResume()
    {
        super.onResume();

        mBus.register(this);
    }


    @Override
    public void onPause()
    {
        super.onPause();

        mBus.unregister(this);
    }
}
