package org.gittner.osmbugs.activities;

import android.support.v7.app.ActionBarActivity;

import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.EBean;
import org.gittner.osmbugs.statics.OttoBus;

@EBean
public class OttoActionBarActivity extends ActionBarActivity
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
