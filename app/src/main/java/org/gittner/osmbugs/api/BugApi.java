package org.gittner.osmbugs.api;

import org.osmdroid.util.BoundingBoxE6;

import java.util.ArrayList;

public interface BugApi<T>
{
    public ArrayList<T> downloadBBox(BoundingBoxE6 bBox);
}
