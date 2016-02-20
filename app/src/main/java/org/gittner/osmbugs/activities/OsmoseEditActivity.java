package org.gittner.osmbugs.activities;

import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.common.OsmoseElement;
import org.gittner.osmbugs.common.OsmoseElementView;
import org.gittner.osmbugs.statics.GeoIntentStarter;

import java.util.List;

@EActivity(R.layout.activity_osmose_edit)
@OptionsMenu(R.menu.osmose_edit)
public class OsmoseEditActivity
        extends ActionBarActivity
        implements BugEditActivityConstants
{
    @Extra(EXTRA_BUG)
    OsmoseBug mBug;

    @ViewById(R.id.txtvTitle)
    TextView mTitle;
    @ViewById(R.id.imgvIcon)
    ImageView mIcon;
    @ViewById(R.id.txtvDetailsTitle)
    TextView mDetailsTitle;
    @ViewById(R.id.layoutDetails)
    LinearLayout mDetails;


    @AfterViews
    void init()
    {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(mBug.getIcon());

        mTitle.setText(mBug.getTitle());

        mIcon.setImageDrawable(mBug.getIcon());

        loadDetails();
    }


    @Background
    void loadDetails()
    {
        List<OsmoseElement> elements = Apis.OSMOSE.loadElements(mBug.getId());

        detailsLoaded(elements);
    }


    @UiThread
    void detailsLoaded(List<OsmoseElement> elements)
    {
        findViewById(R.id.pbarLoadingDetails).setVisibility(View.GONE);

        if (elements == null || elements.isEmpty())
        {
            mDetailsTitle.setVisibility(View.GONE);
            return;
        }

        mDetailsTitle.setText(R.string.details);

        for (OsmoseElement element : elements)
        {
            OsmoseElementView elementView = new OsmoseElementView(this);
            elementView.set(element);
            mDetails.addView(elementView);
        }
    }


    @OptionsItem(R.id.action_share)
    void shareBug()
    {
        GeoIntentStarter.start(this, mBug.getPoint());
    }
}