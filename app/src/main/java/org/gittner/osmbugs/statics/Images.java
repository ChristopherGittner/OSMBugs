package org.gittner.osmbugs.statics;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

public class Images
{
    private static Context mContext;
    private static Resources mResources;
    private static String mPackageName;


    public static void init(Context context)
    {
        mContext = context;
        mResources = context.getResources();
        mPackageName = context.getPackageName();
    }


    public static Drawable getByName(String name, int defaultDrawableId)
    {
        int id = mResources.getIdentifier(name, "drawable", mPackageName);

        try
        {
            return ContextCompat.getDrawable(mContext, id);
        }
        catch (android.content.res.Resources.NotFoundException e)
        {
            return get(defaultDrawableId);
        }
    }


    public static Drawable get(int id)
    {
        return ContextCompat.getDrawable(mContext, id);
    }
}
