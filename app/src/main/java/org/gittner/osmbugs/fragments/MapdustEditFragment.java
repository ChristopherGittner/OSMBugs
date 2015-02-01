package org.gittner.osmbugs.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.MapdustApi;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.common.IndeterminateProgressAsyncTask;
import org.gittner.osmbugs.statics.Globals;
import org.gittner.osmbugs.statics.Settings;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MapdustEditFragment extends BugEditFragment {

    private static final String EXTRA_BUG = "EXTRA_BUG";

    private MapdustBug mBug;

    private CommentAdapter mAdapter;

    public static MapdustEditFragment newInstance(MapdustBug bug)
    {
        MapdustEditFragment instance = new MapdustEditFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_BUG, bug);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();

        mBug = args.getParcelable(EXTRA_BUG);

        final View v = inflater.inflate(R.layout.fragment_mapdust_edit, null);

        TextView txtvDescription = (TextView) v.findViewById(R.id.txtvDescription);
        txtvDescription.setText(mBug.getDescription());

        ListView lstvComments = (ListView) v.findViewById(R.id.lstvComments);
        mAdapter = new CommentAdapter(getActivity());
        lstvComments.setAdapter(mAdapter);

        final ImageButton imgvAddComment = (ImageButton) v.findViewById(R.id.imgbtnAddComment);
        if (mBug.getState() == MapdustBug.STATE.CLOSED) {
            imgvAddComment.setVisibility(GONE);
        }
        else {
            imgvAddComment.setOnClickListener(mAddCommentOnClickListener);
        }

        ImageView imgvIcon = (ImageView) v.findViewById(R.id.imgvIcon);
        ImageView imgvArrowRight = (ImageView) v.findViewById(R.id.imgvArrowRight);
        ImageButton btnResolveBug = (ImageButton) v.findViewById(R.id.btnResolveBug);
        ImageButton btnIgnoreBug = (ImageButton) v.findViewById(R.id.btnIgnoreBug);
        if(mBug.getState() == MapdustBug.STATE.OPEN)
        {
            imgvIcon.setImageDrawable(mBug.getIcon());

            btnResolveBug.setOnClickListener(mBtnResolveBugOnClickListener);
            btnIgnoreBug.setOnClickListener(mBtnIgnoreBugOnClickListener);
        }
        else
        {
            imgvIcon.setVisibility(View.GONE);
            imgvArrowRight.setVisibility(View.GONE);
            btnResolveBug.setVisibility(View.GONE);
            btnIgnoreBug.setVisibility(View.GONE);
        }

        mLoadCommentsTask = new AsyncTask<Void, Void, List<Comment>>() {

            @Override
            protected List<Comment> doInBackground(Void... params) {
                return new MapdustApi().retrieveComments(mBug.getId());
            }

            @Override
            protected void onPostExecute(List<Comment> comments) {
                mBug.setComments(comments);

                mAdapter.addAll(comments);
                mAdapter.notifyDataSetChanged();

                TextView txtvLoadingComments = (TextView) v.findViewById(R.id.txtvCommentsTitle);
                txtvLoadingComments.setText(R.string.comments);

                ProgressBar pbarLoadingComments = (ProgressBar) v.findViewById(R.id.pbarLoadingComments);
                pbarLoadingComments.setVisibility(View.GONE);
            }
        }.execute();

        return v;
    }

    @Override
    public void onPause() {
        super.onPause();

        mLoadCommentsTask.cancel(true);
    }

    private AsyncTask mLoadCommentsTask;

    private final View.OnClickListener mBtnResolveBugOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (Settings.Mapdust.getUsername().equals("")) {
                Toast.makeText(getActivity(), R.string.notification_mapdust_no_username, Toast.LENGTH_LONG).show();
                return;
            }

            final EditText edtxtResolveComment = new EditText(getActivity());

            new AlertDialog.Builder(getActivity())
                    .setView(edtxtResolveComment)
                    .setCancelable(true)
                    .setMessage(R.string.enter_comment)
                    .setPositiveButton(
                            R.string.resolve,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String message = edtxtResolveComment.getText().toString();

                                    new IndeterminateProgressAsyncTask<Void, Void, Boolean> (
                                            getActivity(),
                                            R.string.saving) {
                                        @Override
                                        protected Boolean doInBackground (Void...params){
                                            return new MapdustApi().changeBugStatus(
                                                    mBug.getId(),
                                                    MapdustBug.STATE.CLOSED,
                                                    Settings.Mapdust.getUsername(),
                                                    message);
                                        }

                                        @Override
                                        protected void onPostExecute (Boolean result){
                                            super.onPostExecute(result);

                                            if (result) {
                                                mListener.onBugSaved(Globals.MAPDUST);
                                            } else {
                                                new AlertDialog.Builder(getActivity())
                                                        .setMessage(R.string.failed_to_save_bug)
                                                        .setCancelable(true)
                                                        .create().show();
                                            }
                                        }
                                    }.execute();
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create().show();
        }
    };

    private final View.OnClickListener mBtnIgnoreBugOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (Settings.Mapdust.getUsername().equals("")) {
                Toast.makeText(getActivity(), R.string.notification_mapdust_no_username, Toast.LENGTH_LONG).show();
                return;
            }

            final EditText edtxtResolveComment = new EditText(getActivity());

            new AlertDialog.Builder(getActivity())
                    .setView(edtxtResolveComment)
                    .setCancelable(true)
                    .setMessage(R.string.enter_comment)
                    .setPositiveButton(
                            R.string.resolve,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String message = edtxtResolveComment.getText().toString();

                                    new IndeterminateProgressAsyncTask<Void, Void, Boolean> (
                                            getActivity(),
                                            R.string.saving) {
                                        @Override
                                        protected Boolean doInBackground (Void...params){
                                            return new MapdustApi().changeBugStatus(
                                                    mBug.getId(),
                                                    MapdustBug.STATE.IGNORED,
                                                    Settings.Mapdust.getUsername(),
                                                    message);
                                        }

                                        @Override
                                        protected void onPostExecute (Boolean result){
                                            super.onPostExecute(result);

                                            if (result) {
                                                mListener.onBugSaved(Globals.MAPDUST);
                                            } else {
                                                new AlertDialog.Builder(getActivity())
                                                        .setMessage(R.string.failed_to_save_bug)
                                                        .setCancelable(true)
                                                        .create().show();
                                            }
                                        }
                                    }.execute();
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create().show();
        }
    };

    private final View.OnClickListener mAddCommentOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            final EditText edtxtNewComment = new EditText(getActivity());

            new AlertDialog.Builder(getActivity())
                    .setView(edtxtNewComment)
                    .setCancelable(true)
                    .setMessage(R.string.enter_comment)
                    .setPositiveButton(
                            R.string.do_comment,
                            new DialogInterface.OnClickListener() {

                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    final String message = edtxtNewComment.getText().toString();

                                    new IndeterminateProgressAsyncTask<Void, Void, Boolean>(
                                            getActivity(),
                                            R.string.saving) {
                                        @Override
                                        protected Boolean doInBackground(Void... params) {
                                            return new MapdustApi().commentBug(
                                                    mBug.getId(),
                                                    message,
                                                    Settings.Mapdust.getUsername());
                                        }

                                        @Override
                                        protected void onPostExecute(Boolean result) {
                                            super.onPostExecute(result);

                                            if (result) {
                                                mListener.onBugSaved(Globals.MAPDUST);
                                            }
                                            else
                                            {
                                                new AlertDialog.Builder(getActivity())
                                                        .setMessage(R.string.failed_to_save_bug)
                                                        .setCancelable(true)
                                                        .create().show();
                                            }
                                        }
                                    }.execute();
                                }
                            })
                    .setNegativeButton(getString(R.string.cancel), null)
                    .create().show();
        }
    };

    public class CommentAdapter extends ArrayAdapter<Comment> {

        public CommentAdapter(Context context) {
            super(context, R.layout.row_comment);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;

            if (convertView == null) {
                v = LayoutInflater.from(getContext()).inflate(R.layout.row_comment, null);
            } else {
                v = convertView;
            }

            Comment c = getItem(position);

            TextView txtvUsername = (TextView) v.findViewById(R.id.comment_username);
            if(!c.getUsername().equals("")) {
                txtvUsername.setVisibility(VISIBLE);
                txtvUsername.setText(c.getUsername());
            }
            else {
                txtvUsername.setVisibility(GONE);
            }

            TextView txtvText = (TextView) v.findViewById(R.id.comment_text);
            txtvText.setText(c.getText());

            return v;
        }
    }
}
