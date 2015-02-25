package org.gittner.osmbugs.activities;

import android.support.v7.app.ActionBarActivity;

import org.androidannotations.annotations.EBean;

import de.greenrobot.event.EventBus;

@EBean
public class EventBusActionBarActivity extends ActionBarActivity
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
