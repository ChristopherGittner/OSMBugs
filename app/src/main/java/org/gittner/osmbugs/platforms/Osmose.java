package org.gittner.osmbugs.platforms;

import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.activities.OsmoseEditActivity_;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.api.BugApi;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.statics.Settings;

@EBean(scope = EBean.Scope.Singleton)
public class Osmose extends Platform<OsmoseBug>
{
    @StringRes(R.string.osmose)
    String PLATFORM_NAME;


    @Override
    public String getName()
    {
        return PLATFORM_NAME;
    }


    @Override
    public boolean isEnabled()
    {
        return Settings.Osmose.isEnabled();
    }


    @Override
    public BugApi<OsmoseBug> getApi()
    {
        return Apis.OSMOSE;
    }


    @Override
    public Intent createEditor(Context context, final OsmoseBug bug)
    {
        return OsmoseEditActivity_.intent(context)
                .mBug(bug)
                .get();
    }
}
