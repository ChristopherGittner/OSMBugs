package org.gittner.osmbugs.base;

import android.app.ListFragment;

import org.gittner.osmbugs.statics.OttoBus;

public class BaseListFragment extends ListFragment
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
