package org.gittner.osmbugs.activities;

import android.support.v7.app.AppCompatActivity;

import org.androidannotations.annotations.EBean;
import org.greenrobot.eventbus.EventBus;

@EBean
public class EventBusActionBarActivity extends AppCompatActivity
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
