package org.gittner.osmbugs.statics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;

public class Images
{
    private static Resources mResources;
    private static String mPackageName;


    public static void init(Context context)
    {
        mResources = context.getResources();
        mPackageName = context.getPackageName();
    }


    public static Drawable getByName(String name, int defaultDrawableId)
    {
        int id = mResources.getIdentifier(name, "drawable", mPackageName);

        try
        {
            return mResources.getDrawable(id);
        }
        catch (android.content.res.Resources.NotFoundException e)
        {
            return get(defaultDrawableId);
        }
    }


    public static Drawable get(int id)
    {
        return mResources.getDrawable(id);
    }
}
