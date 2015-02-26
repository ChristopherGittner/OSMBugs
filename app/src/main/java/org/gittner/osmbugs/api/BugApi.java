package org.gittner.osmbugs.api;

import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

public interface BugApi<TBug>
{
    public ArrayList<TBug> downloadBBox(BoundingBoxE6 bBox);
}
