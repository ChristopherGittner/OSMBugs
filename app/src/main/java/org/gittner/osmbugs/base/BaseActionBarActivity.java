package org.gittner.osmbugs.base;

import android.support.v7.app.ActionBarActivity;

import org.gittner.osmbugs.statics.OttoBus;

public class BaseActionBarActivity extends ActionBarActivity
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
