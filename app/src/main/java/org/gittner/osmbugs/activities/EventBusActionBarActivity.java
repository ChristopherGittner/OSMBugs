package org.gittner.osmbugs.activities;

import android.support.v7.app.ActionBarActivity;

import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;

@EBean
public class EventBusActionBarActivity extends ActionBarActivity
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
