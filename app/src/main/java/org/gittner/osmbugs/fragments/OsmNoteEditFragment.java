package org.gittner.osmbugs.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.api.OsmNotesApi;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.statics.Settings;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@EFragment(R.layout.fragment_osm_note_edit)
@OptionsMenu(R.menu.osm_note_edit)
public class OsmNoteEditFragment extends Fragment
{
    public static final String ARG_BUG = "ARG_BUG";

    @FragmentArg(ARG_BUG)
    OsmNote mBug;

    @ViewById(R.id.txtvDescription)
    TextView mDescription;
    @ViewById(R.id.imgbtnAddComment)
    ImageButton mAddComment;
    @ViewById(R.id.lstvComments)
    ListView mComments;

    @OptionsMenuItem(R.id.action_close)
    MenuItem mMenuClose;

    private ProgressDialog mSaveDialog = null;


    @AfterViews
    void init()
    {
        mDescription.setText(mBug.getDescription());

        CommentAdapter adapter = new CommentAdapter(getActivity());

        mComments.setAdapter(adapter);
        adapter.addAll(mBug.getComments());
        adapter.notifyDataSetChanged();

        if (mBug.getState() == OsmNote.STATE.CLOSED)
        {
            mAddComment.setVisibility(GONE);
        }

        mSaveDialog = new ProgressDialog(getActivity());
        mSaveDialog.setTitle(R.string.saving);
        mSaveDialog.setMessage(getString(R.string.please_wait));
        mSaveDialog.setCancelable(false);
        mSaveDialog.setIndeterminate(true);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        mMenuClose.setVisible(mBug.getState() == OsmNote.STATE.OPEN);
    }


    @OptionsItem(R.id.action_close)
    void closeBug()
    {
        if (Settings.OsmNotes.getUsername().equals(""))
        {
            Toast.makeText(
                    getActivity(),
                    R.string.notification_osm_notes_no_username,
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (Settings.OsmNotes.getPassword().equals(""))
        {
            Toast.makeText(
                    getActivity(),
                    R.string.notification_osm_notes_no_password,
                    Toast.LENGTH_LONG).show();
            return;
        }

        final EditText closeComment = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
                .setView(closeComment)
                .setCancelable(true)
                .setTitle(R.string.enter_comment)
                .setPositiveButton(R.string.close, (dialogInterface, i) -> {
                    mSaveDialog.show();

                    closeBug(closeComment.getText().toString());
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    @Background
    void closeBug(String message)
    {
        boolean result = false;
        try
        {
            result = Apis.OSM_NOTES.closeBug(
                    mBug.getId(),
                    Settings.OsmNotes.getUsername(),
                    Settings.OsmNotes.getPassword(),
                    message);

            uploadDone(result);
        } catch (OsmNotesApi.AuthenticationRequiredException e)
        {
            uploadDone(false, getString(R.string.failed_to_close_bug_invalid_username_or_password));
        }
    }

    @UiThread
    void uploadDone(boolean result)
    {
        uploadDone(result, getString(R.string.failed_to_save_bug));
    }

    @UiThread
    void uploadDone(boolean result, String message)
    {
        mSaveDialog.dismiss();

        if (result)
        {
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
        else
        {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error)
                    .setMessage(message)
                    .setCancelable(true)
                    .show();
        }
    }


    @Click(R.id.imgbtnAddComment)
    void addComment()
    {
        final EditText newComment = new EditText(getActivity());
        new AlertDialog.Builder(getActivity())
                .setView(newComment)
                .setCancelable(true)
                .setTitle(R.string.enter_comment)
                .setPositiveButton(R.string.ok, (dialogInterface, i) -> {
                    mSaveDialog.show();

                    uploadComment(newComment.getText().toString());
                })
                .setNegativeButton(R.string.cancel, null)
                .show();
    }


    @Background
    void uploadComment(String comment)
    {
        boolean result = Apis.OSM_NOTES.addComment(
                mBug.getId(),
                Settings.OsmNotes.getUsername(),
                Settings.OsmNotes.getPassword(),
                comment);

        uploadDone(result);
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
            View v = convertView != null ? convertView : LayoutInflater.from(getContext()).inflate(R.layout.row_comment, parent, false);

            Comment comment = getItem(position);

            TextView username = v.findViewById(R.id.username);
            if (!comment.getUsername().equals(""))
            {
                username.setVisibility(VISIBLE);
                username.setText(comment.getUsername());
            }
            else
            {
                username.setVisibility(GONE);
            }

            TextView text = v.findViewById(R.id.text);
            text.setText(comment.getText());

            return v;
        }
    }
}
