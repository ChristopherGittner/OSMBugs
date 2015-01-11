package org.gittner.osmbugs.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.OsmoseBug;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();

        OsmoseBug mBug = args.getParcelable(EXTRA_BUG);

        View v = inflater.inflate(R.layout.fragment_osmose_edit, null);

        TextView txtvTitle = (TextView) v.findViewById(R.id.txtvTitle);
        txtvTitle.setText(mBug.getTitle());

        ImageView imgvIcon = (ImageView) v.findViewById(R.id.imgvIcon);
        imgvIcon.setImageDrawable(mBug.getIcon());

        return v;
    }
}
