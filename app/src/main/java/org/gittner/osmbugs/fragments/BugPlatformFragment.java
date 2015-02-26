package org.gittner.osmbugs.fragments;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.events.BugsChangedEvent;
import org.gittner.osmbugs.loader.Loader;
import org.gittner.osmbugs.platforms.Platform;
import org.gittner.osmbugs.platforms.Platforms;
import org.gittner.osmbugs.statics.TileSources;
import org.osmdroid.views.MapView;

@EFragment(R.layout.fragment_bug_list)
public class BugPlatformFragment extends EventBusFragment
{
    private static final String ARG_PLATFORM = "ARG_PLATFORM";

    @FragmentArg(ARG_PLATFORM)
    String mArgPlatform;

    private Platform mPlatform = null;

    @ViewById(R.id.progressBar)
    ProgressBar mProgressBar;
    @ViewById(R.id.list)
    ListView mList;

    private BugAdapter mAdapter = null;

    private OnFragmentInteractionListener mListener = null;


    public BugPlatformFragment()
    {
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


    @AfterViews
    void init()
    {
        mPlatform = Platforms.byName(mArgPlatform);

        if (mPlatform == Platforms.KEEPRIGHT)
        {
            mAdapter = new KeeprightBugAdapter(getActivity());
        }
        else if (mPlatform == Platforms.OSMOSE)
        {
            mAdapter = new OsmoseBugAdapter(getActivity());
        }
        else if (mPlatform == Platforms.MAPDUST)
        {
            mAdapter = new MapdustBugAdapter(getActivity());
        }
        else if (mPlatform == Platforms.OSM_NOTES)
        {
            mAdapter = new OsmNoteAdapter(getActivity());
        }

        mAdapter.addAll(mPlatform.getBugs());
        mAdapter.notifyDataSetChanged();

        mList.setAdapter(mAdapter);

        if (mPlatform.getLoader().getState() == Loader.LOADING)
        {
            mProgressBar.setVisibility(View.VISIBLE);
        }
        else
        {
            mProgressBar.setVisibility(View.GONE);
        }
    }


    public void onEventMainThread(BugsChangedEvent event)
    {
        if (event.getPlatform() == mPlatform)
        {
            mAdapter.clear();
            mAdapter.addAll(event.getPlatform().getBugs());
            mAdapter.notifyDataSetChanged();
        }
    }


    public void onEventMainThread(Loader.StateChangedEvent event)
    {
        if (event.getPlatform() == mPlatform)
        {
            if (event.getState() == Loader.LOADING)
            {
                mProgressBar.setVisibility(View.VISIBLE);
            }
            else
            {
                mProgressBar.setVisibility(View.GONE);
            }
        }
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
