package org.gittner.osmbugs.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.OsmoseApi;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.common.OsmoseElement;

import java.util.List;

public class OsmoseEditFragment extends BugEditFragment {

    private static final String EXTRA_BUG = "EXTRA_BUG";

    public static OsmoseEditFragment newInstance(OsmoseBug bug)
    {
        OsmoseEditFragment instance = new OsmoseEditFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_BUG, bug);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onPause() {
        super.onPause();

        if(mLoadDetailsTask != null)
        {
            mLoadDetailsTask.cancel(true);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();

        final OsmoseBug mBug = args.getParcelable(EXTRA_BUG);

        final View v = inflater.inflate(R.layout.fragment_osmose_edit, null);

        TextView txtvTitle = (TextView) v.findViewById(R.id.txtvTitle);
        txtvTitle.setText(mBug.getTitle());

        ImageView imgvIcon = (ImageView) v.findViewById(R.id.imgvIcon);
        imgvIcon.setImageDrawable(mBug.getIcon());

        mLoadDetailsTask = new AsyncTask<Void, Void, List<OsmoseElement>>() {
            @Override
            protected List<OsmoseElement> doInBackground(Void... params) {
                return OsmoseApi.loadElements(mBug.getId());
            }

            @Override
            protected void onPostExecute(List<OsmoseElement> osmoseElements) {
                v.findViewById(R.id.pbarLoadingDetails).setVisibility(View.GONE);

                TextView txtvDetailsTitle = (TextView) v.findViewById(R.id.txtvDetailsTitle);
                txtvDetailsTitle.setText(R.string.details);

                String details = "";
                for(OsmoseElement osmoseElement : osmoseElements)
                {
                    details += osmoseElement.toString();
                }

                TextView txtvDetails = (TextView) v.findViewById(R.id.txtvDetails);
                txtvDetails.setVisibility(View.VISIBLE);
                txtvDetails.setText(details);

                mLoadDetailsTask = null;
            }
        };
        mLoadDetailsTask.execute();

        return v;
    }

    AsyncTask<Void, Void, List<OsmoseElement>> mLoadDetailsTask = null;
}
