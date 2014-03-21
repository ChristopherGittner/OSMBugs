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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.gittner.osmbugs.App;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.common.Comment;

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
                invalidateOptionsMenu();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        /* Setup the State Spinner */
        mSpnState = (Spinner) findViewById(R.id.spnState);

        mStateAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        mSpnState.setAdapter(mStateAdapter);

        mSpnState.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                invalidateOptionsMenu();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                invalidateOptionsMenu();
            }
        });

        mStateAdapter.addAll(mBug.getSStates());

        mStateAdapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bug_editor, menu);

        EditText edttxtNewComment = (EditText) findViewById(R.id.edttxtNewComment);
        String newComment = edttxtNewComment.getText().toString();

        String newSState = (String) mSpnState.getSelectedItem();
        if(newSState == null)
            newSState = "";

        /* Set the Save Buttons Visibility based on the Commitable State of the Bug */
        if(mBug.isCommitable(newSState, newComment)) {
            menu.findItem(R.id.action_save).setVisible(true);
        }
        else {
            menu.findItem(R.id.action_save).setVisible(false);
        }

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

            new AsyncTask<Bug, Void, Boolean>(){
                @Override
                protected void onPreExecute() {
                    EditText edttxtNewComment = (EditText) findViewById(R.id.edttxtNewComment);
                    mNewComment = edttxtNewComment.getText().toString();

                    mNewSState = (String) mSpnState.getSelectedItem();
                    if(mNewSState == null)
                        mNewSState = "";
                }

                @Override
                protected Boolean doInBackground(Bug... bugs) {
                    return bugs[0].commit(mNewSState, mNewComment);
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    if(result) {
                        BugEditorActivity.this.finish();
                    }
                    else {
                        Toast.makeText(BugEditorActivity.this, App.getContext().getString(R.string.failed_to_save_bug), Toast.LENGTH_LONG).show();
                    }
                }

                /* The Comment to be commited */
                private String mNewComment;

                /* The new State to be commited */
                private String mNewSState;
            }.execute(mBug);

            return true;
        }

        return super.onOptionsItemSelected(item);
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
