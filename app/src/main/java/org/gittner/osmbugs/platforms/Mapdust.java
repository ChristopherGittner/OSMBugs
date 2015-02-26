package org.gittner.osmbugs.platforms;

import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.activities.MapdustEditActivity_;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.api.BugApi;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.statics.Settings;

@EBean(scope = EBean.Scope.Singleton)
public class Mapdust extends Platform<MapdustBug>
{
    @StringRes(R.string.mapdust)
    String PLATFORM_NAME;


    @Override
    public String getName()
    {
        return PLATFORM_NAME;
    }


    @Override
    public boolean isEnabled()
    {
        return Settings.Mapdust.isEnabled();
    }


    @Override
    public BugApi<MapdustBug> getApi()
    {
        return Apis.MAPDUST;
    }


    @Override
    public Intent createEditor(Context context, final MapdustBug bug)
    {
        return MapdustEditActivity_.intent(context)
                .mBug(bug)
                .get();
    }
}
