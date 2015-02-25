package org.gittner.osmbugs.fragments;

import android.preference.PreferenceFragment;

import org.androidannotations.annotations.EBean;

import de.greenrobot.event.EventBus;

@EBean
public class EventBusPreferenceFragment extends PreferenceFragment
{
    @Override
    public void onResume()
    {
        super.onResume();

        EventBus.getDefault().registerSticky(this);
    }


    @Override
    public void onPause()
    {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }
}
