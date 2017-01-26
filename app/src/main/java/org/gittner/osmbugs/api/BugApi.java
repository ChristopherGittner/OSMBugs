package org.gittner.osmbugs.api;

import org.osmdroid.util.BoundingBox;

import java.util.ArrayList;

public interface BugApi<TBug>
{
    public ArrayList<TBug> downloadBBox(BoundingBox bBox);
}
