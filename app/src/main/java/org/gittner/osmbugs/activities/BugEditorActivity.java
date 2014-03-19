package org.gittner.osmbugs.activities;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.tasks.BugUpdateTask;

import java.util.ArrayList;

public class BugEditorActivity extends Activity {

    /* Dialog Ids */
    public static int DIALOGEDITCOMMENT = 1;

    /* Intent Extras Descriptions */
    public static String EXTRABUG = "BUG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Enable the Spinning Wheel for undetermined Progress */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_bug_editor);

        /* Hide the Progress Bars at start */
        setProgressBarIndeterminate(false);
        setProgressBarIndeterminateVisibility(false);
        setProgressBarVisibility(false);

        /* Deparcel the current Bug */
        mBug = getIntent().getParcelableExtra(EXTRABUG);

        /* Setup the Bug Icon */
        mTxtvTitle = (TextView) findViewById(R.id.textvTitle);
        mTxtvTitle.setText(mBug.getTitle());
        Linkify.addLinks(mTxtvTitle, Linkify.WEB_URLS);
        mTxtvTitle.setText(Html.fromHtml(mTxtvTitle.getText().toString()));

        /* Setup the Description EditText */
        mTxtvText = (TextView) findViewById(R.id.txtvText);
        mTxtvText.setText(mBug.getSnippet());
        Linkify.addLinks(mTxtvText, Linkify.WEB_URLS);
        mTxtvText.setText(Html.fromHtml(mTxtvText.getText().toString()));
        mTxtvText.setMovementMethod(ScrollingMovementMethod.getInstance());

        /*
         * Start to Download Extra Data if neccessary through an AsyncTask and set the ListViews
         * adapter Either after download or if no Download needed instantaneous
         */
        if (mBug.willRetrieveExtraData()) {
            new AsyncTask<Bug, Void, Bug>() {
                @Override
                protected void onPreExecute() {
                    BugEditorActivity.this.setProgressBarIndeterminate(true);
                    BugEditorActivity.this.setProgressBarIndeterminateVisibility(true);
                }

                @Override
                protected Bug doInBackground(Bug... bug) {
                    bug[0].retrieveExtraData();
                    return bug[0];
                }

                @Override
                protected void onPostExecute(Bug bug) {
                    BugEditorActivity.this.setProgressBarIndeterminate(false);
                    BugEditorActivity.this.setProgressBarIndeterminateVisibility(false);
                    mCommentAdapter = new CommentAdapter(BugEditorActivity.this, mBug.getComments());
                    mLvComments = (ListView) findViewById(R.id.listView1);
                    mLvComments.setAdapter(mCommentAdapter);
                    mCommentAdapter.notifyDataSetChanged();
                }
            }.execute(mBug);
        } else {
            mCommentAdapter = new CommentAdapter(this, mBug.getComments());
            mLvComments = (ListView) findViewById(R.id.listView1);
            mLvComments.setAdapter(mCommentAdapter);
        }

        /* Setup the new Comment Textviews */
        mTxtvNewCommentHeader = (TextView) findViewById(R.id.txtvNewCommentHeader);
        mEdttxtNewComment = (TextView) findViewById(R.id.edttxtNewComment);
        if (mBug.isCommentable()) {
            mEdttxtNewComment.setVisibility(View.VISIBLE);
            mTxtvNewCommentHeader.setVisibility(View.VISIBLE);
        } else {
            mEdttxtNewComment.setVisibility(View.GONE);
            mTxtvNewCommentHeader.setVisibility(View.GONE);
        }
        mEdttxtNewComment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
                update();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /* Setup the State Spinner */
        mSpnState = (Spinner) findViewById(R.id.spnState);

        mStateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mSpnState.setAdapter(mStateAdapter);

        if (mBug.getState() == Bug.STATE.OPEN || mBug.isReopenable())
            mStateAdapter.add(mBug.getStringFromState(this, Bug.STATE.OPEN));

        if (mBug.getState() == Bug.STATE.CLOSED || mBug.isClosable())
            mStateAdapter.add(mBug.getStringFromState(this, Bug.STATE.CLOSED));

        if (mBug.getState() == Bug.STATE.IGNORED || mBug.isIgnorable())
            mStateAdapter.add(mBug.getStringFromState(this, Bug.STATE.IGNORED));

        mSpnState.setSelection(mStateAdapter.getPosition(mBug.getStringFromState(this,
                mBug.getState())));

        mStateAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bug_editor, menu);

        mMenu = menu;

        update();
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cancel) {
            setResult(Activity.RESULT_CANCELED);
            finish();

            return true;
        } else if (item.getItemId() == R.id.action_save) {
            /* Save the new Bug state */
            mBug.setState(mBug.getStateFromString(this,
                    mStateAdapter.getItem(mSpnState.getSelectedItemPosition())));

            new BugUpdateTask(this).execute(mBug);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void update() {
        /* Update the Bugs Comment */
        mBug.setNewComment(mEdttxtNewComment.getText().toString());

        // TODO: Turn only on when the bug is actually commitable i.e. has a comment and changed
        // State */
        /* View or hide the Save Icon */
        if (mBug.hasNewComment() || mBug.hasNewState())
            mMenu.findItem(R.id.action_save).setVisible(true);
        else
            mMenu.findItem(R.id.action_save).setVisible(false);
    }

    /* The Bug currently being edited */
    private Bug mBug;

    /* All Views on this Activity */
    private TextView mTxtvTitle, mTxtvText, mTxtvNewCommentHeader, mEdttxtNewComment;

    /* Spinner which holds the selectable states for the Bug */
    private Spinner mSpnState;

    /* Adapter for the State Spinner */
    private ArrayAdapter<String> mStateAdapter;

    /* ListView for the Bugs Comments */
    private ListView mLvComments;

    /* Adapter for the Comments ListView */
    private ArrayAdapter<Comment> mCommentAdapter;

    /* The Main Menu */
    Menu mMenu;

    public class CommentAdapter extends ArrayAdapter<Comment> {

        public CommentAdapter(Context context, ArrayList<Comment> data) {
            super(context, R.id.comment_text, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View returnView;

            /* Try to reuse old Views for performance reasons */
            if (convertView == null) {
                returnView = BugEditorActivity.this.getLayoutInflater().inflate(R.layout.comment_icon, null);
            } else {
                returnView = convertView;
            }

            Comment c = this.getItem(position);

            ((TextView) returnView.findViewById(R.id.comment_text)).setText(c.getText());

            return returnView;
        }
    }
}
