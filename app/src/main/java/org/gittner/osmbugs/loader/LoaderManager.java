package org.gittner.osmbugs.loader;

import java.util.ArrayList;

import de.greenrobot.event.EventBus;

public class LoaderManager
{
    public static final int LOADING = 1;
    public static final int STOPPED = 2;

    private static final LoaderManager instance = new LoaderManager();

    private ArrayList<Loader> mLoaders = new ArrayList<>();

    private int mLastState = STOPPED;


    private LoaderManager()
    {
        EventBus.getDefault().registerSticky(this);

        EventBus.getDefault().postSticky(new LoadersStateChangedEvent(STOPPED));
    }


    public static LoaderManager getInstance()
    {
        return instance;
    }


    public void addLoader(Loader loader)
    {
        mLoaders.add(loader);
    }


    public int getState()
    {
        for(Loader loader : mLoaders)
        {
            if(loader.getState() == Loader.LOADING)
            {
                return LOADING;
            }
        }

        return STOPPED;
    }


    public void onEvent(Loader.StateChangedEvent event)
    {
        int currentState = getState();

        if(currentState != mLastState)
        {
            mLastState = currentState;

            EventBus.getDefault().post(new LoadersStateChangedEvent(currentState));
        }
    }


    public class LoadersStateChangedEvent
    {
        private final int mState;


        public LoadersStateChangedEvent(final int state)
        {
            mState = state;
        }


        public int getState()
        {
            return mState;
        }
    }
}
