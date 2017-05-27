package org.gittner.osmbugs.api;

import org.osmdroid.util.BoundingBox;

import java.util.ArrayList;

public interface BugApi<TBug>
{
    ArrayList<TBug> downloadBBox(BoundingBox bBox);
}
