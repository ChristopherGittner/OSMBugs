package org.gittner.osmbugs.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Globals;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class BugListFragment extends Fragment {

    private BugExpandableListAdapter mAdapter;

    private OnFragmentInteractionListener mListener;

    public static BugListFragment newInstance() {
        return new BugListFragment();
    }

    public interface OnFragmentInteractionListener {
        public void onBugClicked(Bug bug);

        public void onBugMiniMapClicked(Bug bug);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bug_list, null);

        mAdapter = new BugExpandableListAdapter(getActivity());

        final ExpandableListView elstvBugs = (ExpandableListView) v.findViewById(R.id.elstvBugs);
        elstvBugs.setAdapter(mAdapter);

        mAdapter.addAllKeeprightBugs(BugDatabase.getInstance().getKeeprightBugs());
        mAdapter.addAllOsmoseBugs(BugDatabase.getInstance().getOsmoseBugs());
        mAdapter.addAllMapdustBugs(BugDatabase.getInstance().getMapdustBugs());
        mAdapter.addAllOsmNotes(BugDatabase.getInstance().getOpenstreetmapNotes());

        return v;
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
    public void onResume() {
        super.onResume();

        mAdapter.clear();

        if (Settings.Keepright.isEnabled()) {
            mAdapter.addAllKeeprightBugs(BugDatabase.getInstance().getKeeprightBugs());
        }
        if (Settings.Osmose.isEnabled()) {
            mAdapter.addAllOsmoseBugs(BugDatabase.getInstance().getOsmoseBugs());
        }
        if (Settings.Mapdust.isEnabled()) {
            mAdapter.addAllMapdustBugs(BugDatabase.getInstance().getMapdustBugs());
        }
        if (Settings.OsmNotes.isEnabled()) {
            mAdapter.addAllOsmNotes(BugDatabase.getInstance().getOpenstreetmapNotes());
        }

        mAdapter.notifyDataSetChanged();

        /* Register a DatabaseWatcher for update notification */
        BugDatabase.getInstance().addDatabaseWatcher(mDatabaseWatcher);
    }

    @Override
    public void onPause() {
        super.onPause();

        /* Stop listening to update notifications */
        BugDatabase.getInstance().removeDatabaseWatcher(mDatabaseWatcher);
    }

    private class BugExpandableListAdapter extends BaseExpandableListAdapter {

        private final Context mContext;

        private final List<KeeprightBug> mKeeprightBugs = new ArrayList<>();
        private final List<OsmoseBug> mOsmoseBugs = new ArrayList<>();
        private final List<MapdustBug> mMapdustBugs = new ArrayList<>();
        private final List<OsmNote> mOsmNotes = new ArrayList<>();

        private final List<String> mPlatforms = new ArrayList<>();

        public BugExpandableListAdapter(Context context) {
            mContext = context;

            mPlatforms.add(context.getString(R.string.keepright));
            mPlatforms.add(context.getString(R.string.osmose));
            mPlatforms.add(context.getString(R.string.mapdust));
            mPlatforms.add(context.getString(R.string.openstreetmap_notes));
        }

        public void addAllKeeprightBugs(Collection<? extends KeeprightBug> collection) {
            mKeeprightBugs.addAll(collection);
        }

        public void addAllOsmoseBugs(Collection<? extends OsmoseBug> collection) {
            mOsmoseBugs.addAll(collection);
        }

        public void addAllMapdustBugs(Collection<? extends MapdustBug> collection) {
            mMapdustBugs.addAll(collection);
        }

        public void addAllOsmNotes(Collection<? extends OsmNote> collection) {
            mOsmNotes.addAll(collection);
        }

        public void clear() {
            mKeeprightBugs.clear();
            mOsmoseBugs.clear();
            mMapdustBugs.clear();
            mOsmNotes.clear();
        }

        public void clearKeepright()
        {
            mKeeprightBugs.clear();
        }

        public void clearOsmose()
        {
            mOsmoseBugs.clear();
        }

        public void clearMapdust()
        {
            mMapdustBugs.clear();
        }

        public void clearOsmNotes()
        {
            mOsmNotes.clear();
        }

        @Override
        public int getGroupCount() {
            return 4;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            switch (groupPosition) {
                case 0:
                    return mKeeprightBugs.size();
                case 1:
                    return mOsmoseBugs.size();
                case 2:
                    return mMapdustBugs.size();
                case 3:
                    return mOsmNotes.size();
            }
            return 0;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return mPlatforms.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition) {
                case 0:
                    return mKeeprightBugs.get(childPosition);
                case 1:
                    return mOsmoseBugs.get(childPosition);
                case 2:
                    return mMapdustBugs.get(childPosition);
                case 3:
                    return mOsmNotes.get(childPosition);
            }
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

            View v = convertView;

            if (v == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.header_bug, null);
            }

            TextView txtvTitle = (TextView) v.findViewById(R.id.txtvTitle);

            txtvTitle.setText((String) getGroup(groupPosition));

            return v;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View v = convertView;

            if (convertView == null) {
                v = LayoutInflater.from(mContext).inflate(R.layout.row_bug, null);
            }

            TextView txtvTitle = (TextView) v.findViewById(R.id.txtvTitle);
            TextView txtvDescription = (TextView) v.findViewById(R.id.txtvDescription);
            ImageView imgvIcon = (ImageView) v.findViewById(R.id.imgvIcon);
            final MapView mapView = (MapView) v.findViewById(R.id.mapview);
            View layoutInfo = v.findViewById(R.id.layoutInfo);

            if (groupPosition == 0) {
                final KeeprightBug bug = mKeeprightBugs.get(childPosition);

                txtvTitle.setText(bug.getTitle());
                txtvDescription.setText(bug.getDescription());
                imgvIcon.setImageDrawable(bug.getIcon());

                //TODO: Remove as soon as this is fixed in osmdroid (4.3)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mapView.getController().setZoom(17);
                        mapView.getController().setCenter(bug.getPoint());
                    }
                });
                mapView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            mListener.onBugMiniMapClicked(bug);
                        }
                        return true;
                    }
                });
                layoutInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onBugClicked(bug);
                    }
                });
            } else if (groupPosition == 1) {
                final OsmoseBug bug = mOsmoseBugs.get(childPosition);

                txtvTitle.setText(bug.getTitle());
                txtvDescription.setText(bug.getTitle());
                imgvIcon.setImageDrawable(bug.getIcon());

                //TODO: Remove as soon as this is fixed in osmdroid (4.3)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mapView.getController().setZoom(17);
                        mapView.getController().setCenter(bug.getPoint());
                    }
                });
                mapView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            mListener.onBugMiniMapClicked(bug);
                        }
                        return true;
                    }
                });
                layoutInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onBugClicked(bug);
                    }
                });
            } else if (groupPosition == 2) {
                final MapdustBug bug = mMapdustBugs.get(childPosition);

                txtvTitle.setText("");
                txtvDescription.setText(bug.getDescription());
                imgvIcon.setImageDrawable(bug.getIcon());

                //TODO: Remove as soon as this is fixed in osmdroid (4.3)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mapView.getController().setZoom(17);
                        mapView.getController().setCenter(bug.getPoint());
                    }
                });
                mapView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            mListener.onBugMiniMapClicked(bug);
                        }
                        return true;
                    }
                });
                layoutInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onBugClicked(bug);
                    }
                });
            } else if (groupPosition == 3) {
                final OsmNote bug = mOsmNotes.get(childPosition);

                txtvTitle.setText("");
                txtvDescription.setText(bug.getDescription());
                imgvIcon.setImageDrawable(bug.getIcon());

                //TODO: Remove as soon as this is fixed in osmdroid (4.3)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mapView.getController().setZoom(17);
                        mapView.getController().setCenter(bug.getPoint());
                    }
                });
                mapView.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN)
                        {
                            mListener.onBugMiniMapClicked(bug);
                        }
                        return true;
                    }
                });
                layoutInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mListener.onBugClicked(bug);
                    }
                });
            }

            txtvTitle.setText(Html.fromHtml(txtvTitle.getText().toString()));
            txtvDescription.setText(Html.fromHtml(txtvDescription.getText().toString()));

            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }

    /* Listener for Database Updates */
    private final BugDatabase.DatabaseWatcher mDatabaseWatcher = new BugDatabase.DatabaseWatcher() {
        @Override
        public void onDatabaseUpdated(int platform) {

            switch (platform) {
                case Globals.KEEPRIGHT:
                    mAdapter.clearKeepright();
                        mAdapter.addAllKeeprightBugs(BugDatabase.getInstance().getKeeprightBugs());
                    break;

                case Globals.OSMOSE:
                    mAdapter.clearOsmose();
                    mAdapter.addAllOsmoseBugs(BugDatabase.getInstance().getOsmoseBugs());
                    break;

                case Globals.MAPDUST:
                    mAdapter.clearMapdust();
                    mAdapter.addAllMapdustBugs(BugDatabase.getInstance().getMapdustBugs());
                    break;

                case Globals.OSM_NOTES:
                    mAdapter.clearOsmNotes();
                    mAdapter.addAllOsmNotes(BugDatabase.getInstance().getOpenstreetmapNotes());
                    break;
            }
            mAdapter.notifyDataSetChanged();
        }
    };
}
