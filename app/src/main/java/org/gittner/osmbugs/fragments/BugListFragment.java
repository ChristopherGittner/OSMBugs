package org.gittner.osmbugs.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.views.MapView;

public class BugListFragment extends ListFragment {

    private OnFragmentInteractionListener mListener;

    private BugListAdapter mBugListAdapter;

    public static BugListFragment newInstance() {
         return new BugListFragment();
    }

    public BugListFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mBugListAdapter = new BugListAdapter(getActivity());
        setListAdapter(mBugListAdapter);

        /* Register a DatabaseWatcher for update notification */
        BugDatabase.getInstance().addDatabaseWatcher(mDatabseWatcher);

        reloadBugs();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);

        mListener.onBugClicked(mBugListAdapter.getItem(position));
    }

    private void reloadBugs() {
        mBugListAdapter.clear();

        /* Add all Bugs to the Map */
        if(Settings.Keepright.isEnabled()) {
            mBugListAdapter.addAll(BugDatabase.getInstance().getKeeprightBugs());
        }

        if(Settings.Mapdust.isEnabled()) {
            mBugListAdapter.addAll(BugDatabase.getInstance().getMapdustBugs());
        }

        if(Settings.OpenstreetmapNotes.isEnabled()) {
            mBugListAdapter.addAll(BugDatabase.getInstance().getOpenstreetmapNotes());
        }
    }

    public interface OnFragmentInteractionListener {
        public void onBugClicked(Bug bug);

        public void onBugMiniMapClicked(Bug bug);
    }

    /* Listener for Database Updates */
    private BugDatabase.DatabaseWatcher mDatabseWatcher = new BugDatabase.DatabaseWatcher() {
        @Override
        public void onDatabaseUpdated() {
            reloadBugs();

            mBugListAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDatabaseCleared() {
            mBugListAdapter.clear();
            mBugListAdapter.notifyDataSetChanged();
        }
    };

    private class BugListAdapter extends ArrayAdapter<Bug> {

        public BugListAdapter(Context context) {
            super(context, R.layout.row_bug);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;

            if(v == null)
            {
                v = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.row_bug, null);
            }

            final Bug bug = getItem(position);

            TextView txtvTitle = (TextView) v.findViewById(R.id.txtvTitle);
            txtvTitle.setText(bug.getTitle());
            txtvTitle.setText(Html.fromHtml(txtvTitle.getText().toString()));

            TextView txtvDescription = (TextView) v.findViewById(R.id.txtvDescription);
            txtvDescription.setText(bug.getSnippet());
            txtvDescription.setText(Html.fromHtml(txtvDescription.getText().toString()));

            ((ImageView) v.findViewById(R.id.imgvIcon)).setImageDrawable(bug.getMarker(0));

            final MapView mapView = (MapView) v.findViewById(R.id.mapview);
            mapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if(motionEvent.getAction() == MotionEvent.ACTION_DOWN)
                    {
                        mListener.onBugMiniMapClicked(bug);
                    }
                    return true;
                }
            });
            //TODO: Remove as soon as this is fixed in osmdroid (4.3)
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    mapView.getController().setZoom(17);
                    mapView.getController().setCenter(bug.getPoint());
                }
            });

            return v;
        }
    }
}
