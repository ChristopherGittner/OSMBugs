package org.gittner.osmbugs.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OpenstreetmapNote;
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

    public static BugListFragment newInstance() {
        return new BugListFragment();
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
        if (Settings.OpenstreetmapNotes.isEnabled()) {
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

        private Context mContext;

        private List<KeeprightBug> mKeeprightBugs = new ArrayList<>();
        private List<OsmoseBug> mOsmoseBugs = new ArrayList<>();
        private List<MapdustBug> mMapdustBugs = new ArrayList<>();
        private List<OpenstreetmapNote> mOsmNotes = new ArrayList<>();

        private List<String> mPlatforms = new ArrayList<>();

        public BugExpandableListAdapter(Context context) {
            mContext = context;

            mPlatforms.add(context.getString(R.string.keepright));
            mPlatforms.add(context.getString(R.string.osmose));
            mPlatforms.add(context.getString(R.string.mapdust));
            mPlatforms.add(context.getString(R.string.openstreetmap_notes));
        }

        public void add(KeeprightBug bug) {
            mKeeprightBugs.add(bug);
        }

        public void addAllKeeprightBugs(Collection<? extends KeeprightBug> collection) {
            mKeeprightBugs.addAll(collection);
        }

        public void add(OsmoseBug bug) {
            mOsmoseBugs.add(bug);
        }

        public void addAllOsmoseBugs(Collection<? extends OsmoseBug> collection) {
            mOsmoseBugs.addAll(collection);
        }

        public void add(MapdustBug bug) {
            mMapdustBugs.add(bug);
        }

        public void addAllMapdustBugs(Collection<? extends MapdustBug> collection) {
            mMapdustBugs.addAll(collection);
        }

        public void add(OpenstreetmapNote bug) {
            mOsmNotes.add(bug);
        }

        public void addAllOsmNotes(Collection<? extends OpenstreetmapNote> collection) {
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

            if (groupPosition == 0) {
                final KeeprightBug bug = mKeeprightBugs.get(childPosition);

                txtvTitle.setText(bug.getTitle());
                txtvDescription.setText(bug.getSnippet());
                imgvIcon.setImageDrawable(bug.getMarker(0));
                //TODO: Remove as soon as this is fixed in osmdroid (4.3)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mapView.getController().setZoom(17);
                        mapView.getController().setCenter(bug.getPoint());
                    }
                });
            } else if (groupPosition == 1) {
                final OsmoseBug bug = mOsmoseBugs.get(childPosition);

                txtvTitle.setText(bug.getTitle());
                txtvDescription.setText(bug.getSnippet());
                imgvIcon.setImageDrawable(bug.getMarker(0));
                //TODO: Remove as soon as this is fixed in osmdroid (4.3)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mapView.getController().setZoom(17);
                        mapView.getController().setCenter(bug.getPoint());
                    }
                });
            } else if (groupPosition == 2) {
                final MapdustBug bug = mMapdustBugs.get(childPosition);

                txtvTitle.setText(bug.getTitle());
                txtvDescription.setText(bug.getSnippet());
                imgvIcon.setImageDrawable(bug.getMarker(0));
                //TODO: Remove as soon as this is fixed in osmdroid (4.3)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mapView.getController().setZoom(17);
                        mapView.getController().setCenter(bug.getPoint());
                    }
                });
            } else if (groupPosition == 3) {
                final OpenstreetmapNote bug = mOsmNotes.get(childPosition);

                txtvTitle.setText(bug.getTitle());
                txtvDescription.setText(bug.getSnippet());
                imgvIcon.setImageDrawable(bug.getMarker(0));
                //TODO: Remove as soon as this is fixed in osmdroid (4.3)
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        mapView.getController().setZoom(17);
                        mapView.getController().setCenter(bug.getPoint());
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
    };

    /* Listener for Database Updates */
    private BugDatabase.DatabaseWatcher mDatabaseWatcher = new BugDatabase.DatabaseWatcher() {
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

                case Globals.OPENSTREETMAPNOTES:
                    mAdapter.clearOsmNotes();
                    mAdapter.addAllOsmNotes(BugDatabase.getInstance().getOpenstreetmapNotes());
                    break;
            }
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onDatabaseCleared() {
            mAdapter.clear();
            mAdapter.notifyDataSetChanged();
        }
    };
}