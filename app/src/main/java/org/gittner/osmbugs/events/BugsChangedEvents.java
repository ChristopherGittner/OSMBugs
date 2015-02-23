package org.gittner.osmbugs.events;

import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;

import java.util.List;

public class BugsChangedEvents
{
    public static abstract class BugsChangedEventsBase<T extends Bug>
    {
        private final List<T> mBugs;


        public BugsChangedEventsBase(List<T> bugs)
        {
            mBugs = bugs;
        }


        public List<T> getBugs()
        {
            return mBugs;
        }
    }

    public static class Keepright extends BugsChangedEventsBase<KeeprightBug>
    {
        public Keepright(List<KeeprightBug> bugs)
        {
            super(bugs);
        }
    }

    public static class Osmose extends BugsChangedEventsBase<OsmoseBug>
    {
        public Osmose(List<OsmoseBug> bugs)
        {
            super(bugs);
        }
    }

    public static class Mapdust extends BugsChangedEventsBase<MapdustBug>
    {
        public Mapdust(List<MapdustBug> bugs)
        {
            super(bugs);
        }
    }

    public static class OsmNotes extends BugsChangedEventsBase<OsmNote>
    {
        public OsmNotes(List<OsmNote> bugs)
        {
            super(bugs);
        }
    }
}
