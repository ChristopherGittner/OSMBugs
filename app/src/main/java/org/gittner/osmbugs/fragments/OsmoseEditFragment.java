package org.gittner.osmbugs.fragments;

import android.app.AlertDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.OsmoseBug;

public class OsmoseEditFragment extends BugEditFragment {

    private static String EXTRA_BUG = "EXTRA_BUG";

    private OsmoseBug mBug;

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

        mBug = args.getParcelable(EXTRA_BUG);

        View v = inflater.inflate(R.layout.fragment_osmose_edit, null);

        TextView txtvTitle = (TextView) v.findViewById(R.id.txtvTitle);
        txtvTitle.setText(mBug.getTitle());

        return v;
    }
}
