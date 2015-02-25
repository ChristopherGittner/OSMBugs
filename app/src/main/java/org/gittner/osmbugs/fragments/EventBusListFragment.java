package org.gittner.osmbugs.fragments;

import android.app.ListFragment;

import org.androidannotations.annotations.EBean;

import de.greenrobot.event.EventBus;

@EBean
public class EventBusListFragment extends ListFragment
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
