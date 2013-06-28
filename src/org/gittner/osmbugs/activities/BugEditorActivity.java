package org.gittner.osmbugs.activities;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.Bug.STATE;
import org.gittner.osmbugs.bugs.Comment;
import org.gittner.osmbugs.bugs.CommentAdapter;
import org.gittner.osmbugs.tasks.BugUpdateTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.text.util.Linkify;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;

public class BugEditorActivity extends SherlockActivity implements OnItemSelectedListener{

    public static int DIALOGEDITCOMMENT = 1;
    /* The Bug currently being edited */
    private Bug bug_;

    /* Used for passing the Bugs Position in the Buglist to this Intent */
    public static String EXTRABUG = "BUG";

    /* All Views on this Activity */
    private TextView txtvTitle_, txtvText_, txtvNewCommentHeader_, txtvNewComment_;
    private Spinner spnState_;
    private ArrayList<String> spinnerItems_;
    private ListView lvComments_;
    private ArrayAdapter<Comment> commentAdapter_;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Enable the Spinning Wheel for undetermined Progress */
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);

        setContentView(R.layout.activity_bug_editor);

        /* For devices that use ActionBarSherlock the Indeterminate State has to be set to false
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

        if(bug_.getComments() != null){
            commentAdapter_ = new CommentAdapter(this, R.layout.comment_icon, bug_.getComments());
            lvComments_ = (ListView) findViewById(R.id.listView1);
            lvComments_.setAdapter(commentAdapter_);
        }

        /* Setup the new Comment Textview */
        txtvNewCommentHeader_ = (TextView) findViewById(R.id.txtvNewCommentHeader);
        txtvNewCommentHeader_.setVisibility(View.GONE);

        txtvNewComment_ = (TextView) findViewById(R.id.txtvNewComment);
        txtvNewComment_.setVisibility(View.GONE);
        txtvNewComment_.setText("");

        /* Setup the State Spinner */
        spnState_ = (Spinner) findViewById(R.id.spnState);

        spinnerItems_ = bug_.getStateNames(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String> (this, android.R.layout.simple_spinner_item, spinnerItems_);
        spnState_.setAdapter(adapter);

        /* Set the current state of the Bug as the selected Spinner State */
        if(bug_.getState() == Bug.STATE.OPEN)
            spnState_.setSelection(0);
        else if(bug_.getState() == Bug.STATE.CLOSED)
            spnState_.setSelection(1);
        else if(bug_.getState() == Bug.STATE.IGNORED && bug_.isIgnorable())
            spnState_.setSelection(2);
        else
            spnState_.setSelection(0);

        if(bug_.getState() == Bug.STATE.CLOSED && !bug_.isReopenable())
            spnState_.setEnabled(false);

        spnState_.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.bug_editor, menu);
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_cancel){
            setResult(SherlockActivity.RESULT_CANCELED);
            finish();

            return true;
        }
        else if(item.getItemId() == R.id.action_save){
            /* Save the new Bug state */
            switch (spnState_.getSelectedItemPosition()){
                case 0:
                    bug_.setState(STATE.OPEN);
                    break;
                case 1:
                    bug_.setState(STATE.CLOSED);
                    break;
                case 2:
                    bug_.setState(STATE.IGNORED);
                    break;
                default:
                    break;
            }

            new BugUpdateTask(this).execute(bug_);

            return true;
        }
        else if(item.getItemId() == R.id.action_edit){
            showDialog(DIALOGEDITCOMMENT);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    @SuppressWarnings("deprecation")
    public Dialog onCreateDialog(int id) {

        if(id == DIALOGEDITCOMMENT){
            /* Create a simple Dialog where the Comment can be changed */
            AlertDialog.Builder builder = new AlertDialog.Builder(this);

            final EditText commentEditText = new EditText(this);
            commentEditText.setText(bug_.getNewComment());

            builder.setView(commentEditText);

            builder.setMessage(getString(R.string.comment));
            builder.setPositiveButton(getString(R.string.ok), new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    bug_.setNewComment(commentEditText.getText().toString());

                    txtvNewComment_.setText(commentEditText.getText().toString());

                    /* View or hide the New Comment TextViews */
                    if(!commentEditText.getText().toString().equals("")) {
                        txtvNewComment_.setVisibility(View.VISIBLE);
                        txtvNewCommentHeader_.setVisibility(View.VISIBLE);
                    }
                    else{
                        txtvNewComment_.setVisibility(View.GONE);
                        txtvNewCommentHeader_.setVisibility(View.GONE);
                    }

                    dialog.dismiss();
                }});
            builder.setNegativeButton(getString(R.string.cancel), new android.content.DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }});

            return builder.create();
        }

        return super.onCreateDialog(id);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        /* Set the new State to the Bug */
        if(parent.getId() == R.id.spnState) {
            switch (position){
                case 0:
                    bug_.setState(STATE.OPEN);
                    break;
                case 1:
                    bug_.setState(STATE.CLOSED);
                    break;
                case 2:
                    bug_.setState(STATE.IGNORED);
                    break;
                default:
                    break;
            }
        }

        /* Read the new State from the Bug to get to know if it is a valid State */
        if(bug_.getNewState() == STATE.IGNORED)
            spnState_.setSelection(2);
        else if(bug_.getNewState() == STATE.CLOSED)
            spnState_.setSelection(1);
        else
            spnState_.setSelection(0);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
