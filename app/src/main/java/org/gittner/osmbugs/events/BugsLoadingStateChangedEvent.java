package org.gittner.osmbugs.events;

public class BugsLoadingStateChangedEvent
{
    public static class BugsLoadingStateChangedEventBase
    {
        final boolean mState;


        public BugsLoadingStateChangedEventBase(boolean state)
        {
            mState = state;
        }


        public boolean getState()
        {
            return mState;
        }
    }

    public static class All extends BugsLoadingStateChangedEventBase
    {
        public All(boolean state)
        {
            super(state);
        }
    }

    public static class Keepright extends BugsLoadingStateChangedEventBase
    {
        public Keepright(boolean state)
        {
            super(state);
        }
    }

    public static class Osmose extends BugsLoadingStateChangedEventBase
    {
        public Osmose(boolean state)
        {
            super(state);
        }
    }

    public static class Mapdust extends BugsLoadingStateChangedEventBase
    {
        public Mapdust(boolean state)
        {
            super(state);
        }
    }

    public static class OsmNotes extends BugsLoadingStateChangedEventBase
    {
        public OsmNotes(boolean state)
        {
            super(state);
        }
    }
}
