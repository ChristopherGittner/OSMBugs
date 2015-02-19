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

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.statics.TileSources;
import org.gittner.osmbugs.statics.BugDatabase;
import org.gittner.osmbugs.statics.Globals;
import org.osmdroid.views.MapView;

public class BugPlatformListFragment extends ListFragment
{
	private static final String ARG_PLATFORM = "ARG_PLATFORM";

	private BugAdapter mAdapter = null;

	public interface OnFragmentInteractionListener {
		public void onBugClicked(Bug bug);

		public void onBugMiniMapClicked(Bug bug);
	}

	private OnFragmentInteractionListener mListener = null;

	private int mPlatform = 0;

	public static BugPlatformListFragment newInstance(int platform)
	{
		BugPlatformListFragment fragment = new BugPlatformListFragment();

		Bundle args = new Bundle();
		args.putInt(ARG_PLATFORM, platform);
		fragment.setArguments(args);

		return fragment;
	}

	public BugPlatformListFragment()
	{

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
	public void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		Bundle args = getArguments();

		mPlatform = args.getInt(ARG_PLATFORM);
	}

	@Override
	public void onViewCreated(final View view, final Bundle savedInstanceState)
	{
		super.onViewCreated(view, savedInstanceState);

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

	private abstract class BugAdapter<T extends Bug> extends ArrayAdapter<T>
	{
		TextView mTxtvTitle = null;
		TextView mTxtvDescription = null;

		public BugAdapter(final Context context)
		{
			super(context, R.layout.row_bug);
		}

		@Override
		public View getView(final int position, final View convertView, final ViewGroup parent)
		{
			View v = convertView != null ? convertView : LayoutInflater.from(getContext()).inflate(R.layout.row_bug, parent, false);

			mTxtvTitle = (TextView) v.findViewById(R.id.txtvTitle);
			mTxtvDescription = (TextView) v.findViewById(R.id.txtvDescription);
			ImageView imgvIcon = (ImageView) v.findViewById(R.id.imgvIcon);
			final MapView mapView = (MapView) v.findViewById(R.id.mapview);
			View layoutInfo = v.findViewById(R.id.layoutInfo);

			final Bug bug = getItem(position);

			imgvIcon.setImageDrawable(bug.getIcon());

			mapView.getController().setZoom(17);
			mapView.getController().setCenter(bug.getPoint());
			mapView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (event.getAction() == MotionEvent.ACTION_UP) {
                        mListener.onBugMiniMapClicked(bug);
                    }
                    return true;
                }
            });
            mapView.setTileSource(TileSources.getInstance().getPreferredTileSource());
			layoutInfo.setOnClickListener(new View.OnClickListener() {
											  @Override
											  public void onClick(View v) {
												  mListener.onBugClicked(bug);
											  }
										  });

			return v;
		}
	}

	private final BugDatabase.DatabaseWatcher mDatabaseWatcher = new BugDatabase.DatabaseWatcher()
	{
		@Override
		public void onDatabaseUpdated(final int platform)
		{
			if(platform == mPlatform)
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

			mTxtvTitle.setText(bug.getTitle());
			mTxtvDescription.setText(bug.getDescription());

			mTxtvTitle.setText(Html.fromHtml(mTxtvTitle.getText().toString()));
			mTxtvDescription.setText(Html.fromHtml(mTxtvDescription.getText().toString()));

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

			mTxtvTitle.setVisibility(View.GONE);
			mTxtvDescription.setText(bug.getTitle());

			mTxtvTitle.setText(Html.fromHtml(mTxtvTitle.getText().toString()));
			mTxtvDescription.setText(Html.fromHtml(mTxtvDescription.getText().toString()));

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

			mTxtvTitle.setVisibility(View.GONE);
			mTxtvDescription.setText(bug.getDescription());

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

			mTxtvTitle.setVisibility(View.GONE);
			mTxtvDescription.setText(bug.getDescription());

			return v;
		}
	}
}
