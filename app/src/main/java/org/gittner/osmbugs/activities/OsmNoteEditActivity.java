package org.gittner.osmbugs.activities;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.OsmNotesApi;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.statics.Settings;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@EActivity(R.layout.activity_osm_note_edit)
public class OsmNoteEditActivity
        extends ActionBarActivity
        implements BugEditActivityConstants
{
    @Extra(EXTRA_BUG)
    OsmNote mBug;

    @ViewById(R.id.txtvDescription)
    TextView mDescription;
    @ViewById(R.id.imgbtnAddComment)
    ImageButton mAddComment;
    @ViewById(R.id.btnCloseBug)
    ImageButton mCloseBug;
    @ViewById(R.id.lstvComments)
    ListView mComments;

    private MaterialDialog mSaveDialog = null;


    @AfterViews
    void init()
    {
        mDescription.setText(mBug.getDescription());

        CommentAdapter adapter = new CommentAdapter(this);

        mComments.setAdapter(adapter);
        adapter.addAll(mBug.getComments());
        adapter.notifyDataSetChanged();

        if (mBug.getState() == OsmNote.STATE.CLOSED)
        {
            mAddComment.setVisibility(GONE);
        }

        if (mBug.getState() == OsmNote.STATE.CLOSED)
        {
            mCloseBug.setVisibility(GONE);
        }

        mSaveDialog = new MaterialDialog.Builder(this)
                .title(R.string.saving)
                .content(R.string.please_wait)
                .cancelable(false)
                .progress(true, 0)
                .build();
    }


    @Click(R.id.btnResolveBug)
    void resolveBug()
    {
        if (Settings.OsmNotes.getUsername().equals(""))
        {
            Toast.makeText(
                    this,
                    R.string.notification_osm_notes_no_username,
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (Settings.OsmNotes.getPassword().equals(""))
        {
            Toast.makeText(
                    this,
                    R.string.notification_osm_notes_no_password,
                    Toast.LENGTH_LONG).show();
            return;
        }

        final EditText closeComment = new EditText(this);
        new MaterialDialog.Builder(this)
                .customView(closeComment, false)
                .cancelable(true)
                .title(R.string.enter_comment)
                .positiveText(R.string.resolve)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        mSaveDialog.show();

                        closeBug(closeComment.getText().toString());
                    }
                }).show();
    }


    @Click(R.id.imgbtnAddComment)
    void addComment()
    {
        final EditText newComment = new EditText(this);
        new MaterialDialog.Builder(this)
                .customView(newComment, false)
                .cancelable(true)
                .title(R.string.enter_comment)
                .positiveText(R.string.ok)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        mSaveDialog.show();

                        uploadComment(newComment.getText().toString());
                    }
                }).show();
    }


    @Background
    void uploadComment(String comment)
    {
        boolean result = new OsmNotesApi().addComment(
                mBug.getId(),
                Settings.OsmNotes.getUsername(),
                Settings.OsmNotes.getPassword(),
                comment);

        uploadDone(result);
    }


    @Background
    void closeBug(String message)
    {
        boolean result = new OsmNotesApi().closeBug(
                mBug.getId(),
                Settings.OsmNotes.getUsername(),
                Settings.OsmNotes.getPassword(),
                message);

        uploadDone(result);
    }


    @UiThread
    void uploadDone(boolean result)
    {
        mSaveDialog.dismiss();

        if (result)
        {
            setResult(RESULT_SAVED_OSM_NOTES);
            finish();
        }
        else
        {
            new MaterialDialog.Builder(this)
                    .title(R.string.error)
                    .content(R.string.failed_to_save_bug)
                    .cancelable(true)
                    .show();
        }
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
            View v = convertView != null ? convertView :  LayoutInflater.from(getContext()).inflate(R.layout.row_comment, parent, false);

            Comment comment = getItem(position);

            TextView username = (TextView) v.findViewById(R.id.username);
            if (!comment.getUsername().equals(""))
            {
                username.setVisibility(VISIBLE);
                username.setText(comment.getUsername());
            }
            else
            {
                username.setVisibility(GONE);
            }

            TextView text = (TextView) v.findViewById(R.id.text);
            text.setText(comment.getText());

            return v;
        }
    }
}