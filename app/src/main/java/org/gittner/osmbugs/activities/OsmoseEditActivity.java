package org.gittner.osmbugs.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.OsmoseApi;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.common.OsmoseElement;
import org.gittner.osmbugs.common.OsmoseElementView;

import java.util.List;

public class OsmoseEditActivity extends BugEditActivity
{
    private AsyncTask<Long, Void, List<OsmoseElement>> mLoadDetailsTask = null;


    @Override
    public void onPause()
    {
        super.onPause();
        if (mLoadDetailsTask != null)
        {
            mLoadDetailsTask.cancel(true);
        }
    }


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_osmose_edit);
        Bundle args = getIntent().getExtras();
        final OsmoseBug mBug = args.getParcelable(EXTRA_BUG);
        TextView txtvTitle = (TextView) findViewById(R.id.txtvTitle);
        txtvTitle.setText(mBug.getTitle());
        ImageView imgvIcon = (ImageView) findViewById(R.id.imgvIcon);
        imgvIcon.setImageDrawable(mBug.getIcon());
        mLoadDetailsTask = new AsyncTask<Long, Void, List<OsmoseElement>>()
        {
            @Override
            protected List<OsmoseElement> doInBackground(Long... id)
            {
                return new OsmoseApi().loadElements(id[0]);
            }


            @Override
            protected void onPostExecute(List<OsmoseElement> osmoseElements)
            {
                findViewById(R.id.pbarLoadingDetails).setVisibility(View.GONE);
                TextView txtvDetailsTitle = (TextView) findViewById(R.id.txtvDetailsTitle);
                if (osmoseElements == null || osmoseElements.isEmpty())
                {
                    txtvDetailsTitle.setVisibility(View.GONE);
                    return;
                }
                txtvDetailsTitle.setText(R.string.details);
                for (OsmoseElement element : osmoseElements)
                {
                    OsmoseElementView elementView = new OsmoseElementView(OsmoseEditActivity.this);
                    elementView.set(element);
                    LinearLayout layoutDetails = (LinearLayout) findViewById(R.id.layoutDetails);
                    layoutDetails.addView(elementView);
                }
                mLoadDetailsTask = null;
            }
        };
        mLoadDetailsTask.execute(mBug.getId());
    }
}
