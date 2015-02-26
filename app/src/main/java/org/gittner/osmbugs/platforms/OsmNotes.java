package org.gittner.osmbugs.platforms;

import android.content.Context;
import android.content.Intent;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.res.StringRes;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.activities.OsmNoteEditActivity_;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.api.BugApi;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.statics.Settings;

@EBean(scope = EBean.Scope.Singleton)
public class OsmNotes extends Platform<OsmNote>
{
    @StringRes(R.string.openstreetmap_notes)
    String PLATFORM_NAME;


    @Override
    public String getName()
    {
        return PLATFORM_NAME;
    }


    @Override
    public boolean isEnabled()
    {
        return Settings.OsmNotes.isEnabled();
    }


    @Override
    public BugApi<OsmNote> getApi()
    {
        return Apis.OSM_NOTES;
    }


    @Override
    public Intent createEditor(Context context, final OsmNote bug)
    {
        return OsmNoteEditActivity_.intent(context)
                .mBug(bug)
                .get();
    }
}
