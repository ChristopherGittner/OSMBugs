package org.gittner.osmbugs.fragments;

import android.app.Fragment;

import org.greenrobot.eventbus.EventBus;

public class EventBusFragment extends Fragment
{
    @Override
    public void onResume()
    {
        super.onResume();

        EventBus.getDefault().register(this);
    }


    @Override
    public void onPause()
    {
        super.onPause();

        EventBus.getDefault().unregister(this);
    }
}
