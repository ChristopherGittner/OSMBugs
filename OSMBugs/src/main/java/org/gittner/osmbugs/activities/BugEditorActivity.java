
package org.gittner.osmbugs.activities;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
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

public class BugEditorActivity extends ActionBarActivity {

    public static int DIALOGEDITCOMMENT = 1;

    /* The Bug currently being edited */
    private Bug bug_;

    /* Used for passing the Bugs Position in the Buglist to this Intent */
    public static String EXTRABUG = "BUG";

    /* All Views on this Activity */
    private TextView txtvTitle_, txtvText_, txtvNewCommentHeader_, edttxtNewComment_;

    private Spinner spnState_;

    private ListView lvComments_;

    private ArrayAdapter<String> stateAdapter_;

    private ArrayAdapter<Comment> commentAdapter_;

    /* The Main Menu */
    Menu menu_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Enable the Spinning Wheel for undetermined Progress */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_bug_editor);

        /*
         * For devices that use ActionBarSherlock the Indeterminate State has to be set to false
         * otherwise it will be displayed at start
         */
        setSupportProgressBarIndeterminate(false);
        setSupportProgressBarIndeterminateVisibility(false);
        setSupportProgressBarVisibility(false);

        /* Deparcel the current Bug */
        bug_ = getIntent().getParcelableExtra(EXTRABUG);

        /* Setup the Bug Icon */
        txtvTitle_ = (TextView) findViewById(R.id.textvTitle);
        txtvTitle_.setText(bug_.getTitle());
        Linkify.addLinks(txtvTitle_, Linkify.WEB_URLS);
        txtvTitle_.setText(Html.fromHtml(txtvTitle_.getText().toString()));

        /* Setup the Description EditText */
        txtvText_ = (TextView) findViewById(R.id.txtvText);
        txtvText_.setText(bug_.getSnippet());
        Linkify.addLinks(txtvText_, Linkify.WEB_URLS);
        txtvText_.setText(Html.fromHtml(txtvText_.getText().toString()));
        txtvText_.setMovementMethod(ScrollingMovementMethod.getInstance());

        /*
         * Start to Download Extra Data if neccessary through an AsyncTask and set the ListViews
         * adapter Either after download or if no Download needed instantaneous
         */
        if (bug_.willRetrieveExtraData()) {
            new AsyncTask<Bug, Void, Bug>() {

                ActionBarActivity activity_;

                public AsyncTask<Bug, Void, Bug> init(ActionBarActivity activity) {
                    activity_ = activity;
                    return this;
                }

                @Override
                protected void onPreExecute() {
                    activity_.setSupportProgressBarIndeterminate(true);
                    activity_.setSupportProgressBarIndeterminateVisibility(true);
                }

                @Override
                protected Bug doInBackground(Bug... bug) {
                    bug[0].retrieveExtraData();
                    return bug[0];
                }

                @Override
                protected void onPostExecute(Bug bug) {
                    activity_.setSupportProgressBarIndeterminate(false);
                    activity_.setSupportProgressBarIndeterminateVisibility(false);
                    commentAdapter_ = new CommentAdapter(activity_, bug_.getComments());
                    lvComments_ = (ListView) findViewById(R.id.listView1);
                    lvComments_.setAdapter(commentAdapter_);
                    commentAdapter_.notifyDataSetChanged();
                }
            }.init(this).execute(bug_);
        } else {
            commentAdapter_ = new CommentAdapter(this, bug_.getComments());
            lvComments_ = (ListView) findViewById(R.id.listView1);
            lvComments_.setAdapter(commentAdapter_);
        }

        /* Setup the new Comment Textviews */
        txtvNewCommentHeader_ = (TextView) findViewById(R.id.txtvNewCommentHeader);
        edttxtNewComment_ = (TextView) findViewById(R.id.edttxtNewComment);
        if (bug_.isCommentable()) {
            edttxtNewComment_.setVisibility(View.VISIBLE);
            txtvNewCommentHeader_.setVisibility(View.VISIBLE);
        } else {
            edttxtNewComment_.setVisibility(View.GONE);
            txtvNewCommentHeader_.setVisibility(View.GONE);
        }
        edttxtNewComment_.addTextChangedListener(new TextWatcher() {
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
        spnState_ = (Spinner) findViewById(R.id.spnState);

        stateAdapter_ = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        spnState_.setAdapter(stateAdapter_);

        if (bug_.getState() == Bug.STATE.OPEN || bug_.isReopenable())
            stateAdapter_.add(bug_.getStringFromState(this, Bug.STATE.OPEN));

        if (bug_.getState() == Bug.STATE.CLOSED || bug_.isClosable())
            stateAdapter_.add(bug_.getStringFromState(this, Bug.STATE.CLOSED));

        if (bug_.getState() == Bug.STATE.IGNORED || bug_.isIgnorable())
            stateAdapter_.add(bug_.getStringFromState(this, Bug.STATE.IGNORED));

        spnState_.setSelection(stateAdapter_.getPosition(bug_.getStringFromState(this,
                bug_.getState())));

        stateAdapter_.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bug_editor, menu);

        update();
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_cancel) {
            setResult(ActionBarActivity.RESULT_CANCELED);
            finish();

            return true;
        } else if (item.getItemId() == R.id.action_save) {
            /* Save the new Bug state */
            bug_.setState(bug_.getStateFromString(this,
                    stateAdapter_.getItem(spnState_.getSelectedItemPosition())));

            new BugUpdateTask(this).execute(bug_);

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void update() {
        /* Update the Bugs Comment */
        bug_.setNewComment(edttxtNewComment_.getText().toString());

        // TODO: Turn only on when the bug is actually commitable i.e. has a comment and changed
        // State */
        /* View or hide the Save Icon */
        if (bug_.hasNewComment() || bug_.hasNewState())
            menu_.findItem(R.id.action_save).setVisible(true);
        else
            menu_.findItem(R.id.action_save).setVisible(false);
    }

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
