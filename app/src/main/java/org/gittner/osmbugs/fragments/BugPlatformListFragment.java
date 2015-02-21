package org.gittner.osmbugs.fragments;

import android.app.Activity;
import android.app.ListFragment;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Globals;
import org.gittner.osmbugs.statics.TileSources;
import org.osmdroid.views.MapView;

@EFragment
public class BugPlatformListFragment extends ListFragment
{
    private static final String ARG_PLATFORM = "ARG_PLATFORM";
    @FragmentArg(ARG_PLATFORM)
    int mPlatform;
    private final BugDatabase.DatabaseWatcher mDatabaseWatcher = new BugDatabase.DatabaseWatcher()
    {
        @Override
        public void onDatabaseUpdated(final int platform)
        {
            if (platform == mPlatform)
            {
                reloadBugs();
            }
        }


        @Override
        public void onDownloadCancelled(final int platform)
        {
        }


        @Override
        public void onDownloadError(final int platform)
        {
        }
    };
    private BugAdapter mAdapter = null;

    private OnFragmentInteractionListener mListener = null;


    public BugPlatformListFragment()
    {
    }


    public static BugPlatformListFragment_ newInstance(int platform)
    {
        BugPlatformListFragment_ fragment = new BugPlatformListFragment_();

        Bundle args = new Bundle();
        args.putInt(ARG_PLATFORM, platform);
        fragment.setArguments(args);

        return fragment;
    }


    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        try
        {
            mListener = (OnFragmentInteractionListener) activity;
        }
        catch (ClassCastException e)
        {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }


    @Override
    public void onResume()
    {
        super.onResume();
        BugDatabase.getInstance().addDatabaseWatcher(mDatabaseWatcher);
    }


    @Override
    public void onPause()
    {
        super.onPause();
        BugDatabase.getInstance().removeDatabaseWatcher(mDatabaseWatcher);
    }


    @AfterViews
    void init()
    {
        switch (mPlatform)
        {
            case Globals.KEEPRIGHT:
                mAdapter = new KeeprightBugAdapter(getActivity());
                break;

            case Globals.OSMOSE:
                mAdapter = new OsmoseBugAdapter(getActivity());
                break;

            case Globals.MAPDUST:
                mAdapter = new MapdustBugAdapter(getActivity());
                break;

            case Globals.OSM_NOTES:
                mAdapter = new OsmNoteAdapter(getActivity());
                break;
        }
        reloadBugs();
        setListAdapter(mAdapter);
    }


    private void reloadBugs()
    {
        mAdapter.clear();
        switch (mPlatform)
        {
            case Globals.KEEPRIGHT:
                mAdapter.addAll(BugDatabase.getInstance().getKeeprightBugs());
                break;

            case Globals.OSMOSE:
                mAdapter.addAll(BugDatabase.getInstance().getOsmoseBugs());
                break;

            case Globals.MAPDUST:
                mAdapter.addAll(BugDatabase.getInstance().getMapdustBugs());
                break;

            case Globals.OSM_NOTES:
                mAdapter.addAll(BugDatabase.getInstance().getOsmNotes());
                break;
        }
        mAdapter.notifyDataSetChanged();
    }


    public interface OnFragmentInteractionListener
    {
        public void onBugClicked(Bug bug);

        public void onBugMiniMapClicked(Bug bug);
    }

    private abstract class BugAdapter<T extends Bug> extends ArrayAdapter<T>
    {
        TextView title = null;
        TextView description = null;


        public BugAdapter(final Context context)
        {
            super(context, R.layout.row_bug);
        }


        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent)
        {
            View v = convertView != null ? convertView : LayoutInflater.from(getContext()).inflate(R.layout.row_bug, parent, false);

            final Bug bug = getItem(position);

            title = (TextView) v.findViewById(R.id.txtvTitle);
            description = (TextView) v.findViewById(R.id.txtvDescription);

            ImageView imgvIcon = (ImageView) v.findViewById(R.id.imgvIcon);
            imgvIcon.setImageDrawable(bug.getIcon());

            View layoutInfo = v.findViewById(R.id.layoutInfo);

            final MapView mapView = (MapView) v.findViewById(R.id.mapview);
            mapView.getController().setZoom(17);
            mapView.getController().setCenter(bug.getPoint());
            mapView.setOnTouchListener(new View.OnTouchListener()
            {
                @Override
                public boolean onTouch(View v, MotionEvent event)
                {
                    if (event.getAction() == MotionEvent.ACTION_UP)
                    {
                        mListener.onBugMiniMapClicked(bug);
                    }
                    return true;
                }
            });
            mapView.setTileSource(TileSources.getInstance().getPreferredTileSource());
            layoutInfo.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View v)
                {
                    mListener.onBugClicked(bug);
                }
            });

            return v;
        }
    }

    private class KeeprightBugAdapter extends BugAdapter<KeeprightBug>
    {
        public KeeprightBugAdapter(final Context context)
        {
            super(context);
        }


        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent)
        {
            View v = super.getView(position, convertView, parent);

            final KeeprightBug bug = getItem(position);

            title.setText(bug.getTitle());
            description.setText(bug.getDescription());
            title.setText(Html.fromHtml(title.getText().toString()));
            description.setText(Html.fromHtml(description.getText().toString()));

            return v;
        }
    }

    private class OsmoseBugAdapter extends BugAdapter<OsmoseBug>
    {
        public OsmoseBugAdapter(final Context context)
        {
            super(context);
        }


        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent)
        {
            View v = super.getView(position, convertView, parent);

            final OsmoseBug bug = getItem(position);

            title.setVisibility(View.GONE);
            description.setText(bug.getTitle());
            title.setText(Html.fromHtml(title.getText().toString()));
            description.setText(Html.fromHtml(description.getText().toString()));

            return v;
        }
    }

    private class MapdustBugAdapter extends BugAdapter<MapdustBug>
    {
        public MapdustBugAdapter(final Context context)
        {
            super(context);
        }


        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent)
        {
            View v = super.getView(position, convertView, parent);

            final MapdustBug bug = getItem(position);

            title.setVisibility(View.GONE);
            description.setText(bug.getDescription());

            return v;
        }
    }

    private class OsmNoteAdapter extends BugAdapter<OsmNote>
    {
        public OsmNoteAdapter(final Context context)
        {
            super(context);
        }


        @Override
        public View getView(final int position, final View convertView, final ViewGroup parent)
        {
            View v = super.getView(position, convertView, parent);

            final OsmNote bug = getItem(position);

            title.setVisibility(View.GONE);
            description.setText(bug.getDescription());

            return v;
        }
    }
}
