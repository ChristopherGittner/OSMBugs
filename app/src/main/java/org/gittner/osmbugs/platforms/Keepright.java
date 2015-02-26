package org.gittner.osmbugs.platforms;

import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.activities.KeeprightEditActivity_;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.api.BugApi;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.statics.Settings;

@EBean(scope = EBean.Scope.Singleton)
public class Keepright extends Platform<KeeprightBug>
{
    @StringRes(R.string.keepright)
    String PLATFORM_NAME;


    @Override
    public String getName()
    {
        return PLATFORM_NAME;
    }


    @Override
    public boolean isEnabled()
    {
        return Settings.Keepright.isEnabled();
    }


    @Override
    public BugApi<KeeprightBug> getApi()
    {
        return Apis.KEEPRIGHT;
    }


    @Override
    public Intent createEditor(Context context, final KeeprightBug bug)
    {
        return KeeprightEditActivity_.intent(context)
                .mBug(bug)
                .get();
    }
}
