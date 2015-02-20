package org.gittner.osmbugs.activities;

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
import org.gittner.osmbugs.statics.Settings;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class MapdustEditActivity extends BugEditActivity
{
    private static final String EXTRA_BUG = "EXTRA_BUG";
    private final View.OnClickListener mBtnResolveBugOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (Settings.Mapdust.getUsername().equals(""))
            {
                Toast.makeText(
                        MapdustEditActivity.this,
                        R.string.notification_mapdust_no_username,
                        Toast.LENGTH_LONG).show();
                return;
            }
            final EditText edtxtResolveComment = new EditText(MapdustEditActivity.this);
            new AlertDialog.Builder(MapdustEditActivity.this)
                    .setView(edtxtResolveComment)
                    .setCancelable(true)
                    .setMessage(R.string.enter_comment)
                    .setPositiveButton(
                            R.string.resolve,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    final String message = edtxtResolveComment.getText().toString();
                                    new IndeterminateProgressAsyncTask<Void, Void, Boolean>(
                                            MapdustEditActivity.this,
                                            R.string.saving)
                                    {
                                        @Override
                                        protected Boolean doInBackground(Void... params)
                                        {
                                            return new MapdustApi().changeBugStatus(
                                                    mBug.getId(),
                                                    MapdustBug.STATE.CLOSED,
                                                    message,
                                                    Settings.Mapdust.getUsername()
                                            );
                                        }


                                        @Override
                                        protected void onPostExecute(Boolean result)
                                        {
                                            super.onPostExecute(result);
                                            if (result)
                                            {
                                                setResult(RESULT_SAVED_MAPDUST);
                                                finish();
                                            }
                                            else
                                            {
                                                new AlertDialog.Builder(MapdustEditActivity.this)
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
    private final View.OnClickListener mBtnIgnoreBugOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            if (Settings.Mapdust.getUsername().equals(""))
            {
                Toast.makeText(MapdustEditActivity.this, R.string.notification_mapdust_no_username, Toast.LENGTH_LONG).show();
                return;
            }
            final EditText edtxtResolveComment = new EditText(MapdustEditActivity.this);
            new AlertDialog.Builder(MapdustEditActivity.this)
                    .setView(edtxtResolveComment)
                    .setCancelable(true)
                    .setMessage(R.string.enter_comment)
                    .setPositiveButton(
                            R.string.resolve,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    final String message = edtxtResolveComment.getText().toString();
                                    new IndeterminateProgressAsyncTask<Void, Void, Boolean>(
                                            MapdustEditActivity.this,
                                            R.string.saving)
                                    {
                                        @Override
                                        protected Boolean doInBackground(Void... params)
                                        {
                                            return new MapdustApi().changeBugStatus(
                                                    mBug.getId(),
                                                    MapdustBug.STATE.IGNORED,
                                                    message,
                                                    Settings.Mapdust.getUsername());
                                        }


                                        @Override
                                        protected void onPostExecute(Boolean result)
                                        {
                                            super.onPostExecute(result);
                                            if (result)
                                            {
                                                setResult(RESULT_SAVED_MAPDUST);
                                                finish();
                                            }
                                            else
                                            {
                                                new AlertDialog.Builder(MapdustEditActivity.this)
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
    private final View.OnClickListener mAddCommentOnClickListener = new View.OnClickListener()
    {
        @Override
        public void onClick(View v)
        {
            final EditText edtxtNewComment = new EditText(MapdustEditActivity.this);
            new AlertDialog.Builder(MapdustEditActivity.this)
                    .setView(edtxtNewComment)
                    .setCancelable(true)
                    .setMessage(R.string.enter_comment)
                    .setPositiveButton(
                            R.string.do_comment,
                            new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which)
                                {
                                    final String message = edtxtNewComment.getText().toString();
                                    new IndeterminateProgressAsyncTask<Void, Void, Boolean>(
                                            MapdustEditActivity.this,
                                            R.string.saving)
                                    {
                                        @Override
                                        protected Boolean doInBackground(Void... params)
                                        {
                                            return new MapdustApi().commentBug(
                                                    mBug.getId(),
                                                    message,
                                                    Settings.Mapdust.getUsername());
                                        }


                                        @Override
                                        protected void onPostExecute(Boolean result)
                                        {
                                            super.onPostExecute(result);
                                            if (result)
                                            {
                                                setResult(RESULT_SAVED_MAPDUST);
                                                finish();
                                            }
                                            else
                                            {
                                                new AlertDialog.Builder(MapdustEditActivity.this)
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
    private MapdustBug mBug;
    private CommentAdapter mAdapter;
    private AsyncTask mLoadCommentsTask;


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapdust_edit);
        Bundle args = getIntent().getExtras();
        mBug = args.getParcelable(EXTRA_BUG);
        TextView txtvDescription = (TextView) findViewById(R.id.txtvDescription);
        txtvDescription.setText(mBug.getDescription());
        ListView lstvComments = (ListView) findViewById(R.id.lstvComments);
        mAdapter = new CommentAdapter(this);
        lstvComments.setAdapter(mAdapter);
        final ImageButton imgvAddComment = (ImageButton) findViewById(R.id.imgbtnAddComment);
        if (mBug.getState() == MapdustBug.STATE.CLOSED)
        {
            imgvAddComment.setVisibility(GONE);
        }
        else
        {
            imgvAddComment.setOnClickListener(mAddCommentOnClickListener);
        }
        ImageView imgvIcon = (ImageView) findViewById(R.id.imgvIcon);
        ImageView imgvArrowRight = (ImageView) findViewById(R.id.imgvArrowRight);
        ImageButton btnResolveBug = (ImageButton) findViewById(R.id.btnResolveBug);
        ImageButton btnIgnoreBug = (ImageButton) findViewById(R.id.btnIgnoreBug);
        if (mBug.getState() == MapdustBug.STATE.OPEN)
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
        mLoadCommentsTask = new AsyncTask<Void, Void, List<Comment>>()
        {
            @Override
            protected List<Comment> doInBackground(Void... params)
            {
                return new MapdustApi().retrieveComments(mBug.getId());
            }


            @Override
            protected void onPostExecute(List<Comment> comments)
            {
                mBug.setComments(comments);
                mAdapter.addAll(comments);
                mAdapter.notifyDataSetChanged();
                TextView txtvLoadingComments = (TextView) findViewById(R.id.txtvCommentsTitle);
                txtvLoadingComments.setText(R.string.comments);
                ProgressBar pbarLoadingComments = (ProgressBar) findViewById(R.id.pbarLoadingComments);
                pbarLoadingComments.setVisibility(View.GONE);
            }
        }.execute();
    }


    @Override
    public void onPause()
    {
        super.onPause();
        mLoadCommentsTask.cancel(true);
    }


    public class CommentAdapter extends ArrayAdapter<Comment>
    {
        public CommentAdapter(Context context)
        {
            super(context, R.layout.row_comment);
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View v;
            if (convertView == null)
            {
                v = LayoutInflater.from(getContext()).inflate(R.layout.row_comment, parent, false);
            }
            else
            {
                v = convertView;
            }
            Comment c = getItem(position);
            TextView txtvUsername = (TextView) v.findViewById(R.id.comment_username);
            if (!c.getUsername().equals(""))
            {
                txtvUsername.setVisibility(VISIBLE);
                txtvUsername.setText(c.getUsername());
            }
            else
            {
                txtvUsername.setVisibility(GONE);
            }
            TextView txtvText = (TextView) v.findViewById(R.id.comment_text);
            txtvText.setText(c.getText());
            return v;
        }
    }
}
