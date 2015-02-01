package org.gittner.osmbugs.fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.OsmoseApi;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.common.OsmoseElement;
import org.gittner.osmbugs.common.OsmoseElementView;

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

        mLoadDetailsTask = new AsyncTask<Long, Void, List<OsmoseElement>>() {
            @Override
            protected List<OsmoseElement> doInBackground(Long... id) {
                return new OsmoseApi().loadElements(id[0]);
            }

            @Override
            protected void onPostExecute(List<OsmoseElement> osmoseElements) {
                v.findViewById(R.id.pbarLoadingDetails).setVisibility(View.GONE);

                TextView txtvDetailsTitle = (TextView) v.findViewById(R.id.txtvDetailsTitle);
                if (osmoseElements == null || osmoseElements.isEmpty()) {
                    txtvDetailsTitle.setVisibility(View.GONE);
                    return;
                }

                txtvDetailsTitle.setText(R.string.details);

                for(OsmoseElement element : osmoseElements)
                {
                    OsmoseElementView elementView = new OsmoseElementView(getActivity());
                    elementView.set(element);

                    LinearLayout layoutDetails = (LinearLayout) v.findViewById(R.id.layoutDetails);
                    layoutDetails.addView(elementView);
                }

                mLoadDetailsTask = null;
            }
        };
        mLoadDetailsTask.execute(mBug.getId());

        return v;
    }

    private AsyncTask<Long, Void, List<OsmoseElement>> mLoadDetailsTask = null;
}
