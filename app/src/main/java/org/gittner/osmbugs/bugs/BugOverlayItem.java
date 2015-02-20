package org.gittner.osmbugs.bugs;

import android.graphics.drawable.Drawable;

import org.osmdroid.views.overlay.OverlayItem;

public class BugOverlayItem extends OverlayItem
{
    private final Bug mBug;
    private final Drawable mDrawable;


    public BugOverlayItem(final Bug bug)
    {
        super("", "", bug.getPoint());
        mBug = bug;
        mDrawable = bug.getIcon();
    }


    @Override
    public Drawable getMarker(int stateBitset)
    {
        return mDrawable;
    }


    public Bug getBug()
    {
        return mBug;
    }
}
