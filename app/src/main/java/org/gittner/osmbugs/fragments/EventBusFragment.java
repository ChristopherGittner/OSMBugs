package org.gittner.osmbugs.fragments;

import android.app.Fragment;

import de.greenrobot.event.EventBus;

public class EventBusFragment extends Fragment
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
