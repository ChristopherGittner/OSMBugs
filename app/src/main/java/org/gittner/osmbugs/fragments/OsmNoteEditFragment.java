package org.gittner.osmbugs.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.OpenstreetmapNotesApi;
import org.gittner.osmbugs.bugs.OpenstreetmapNote;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.common.IndeterminateProgressAsyncTask;
import org.gittner.osmbugs.statics.Globals;
import org.gittner.osmbugs.statics.Settings;

import static android.view.View.GONE;
import static android.view.View.OnClickListener;
import static android.view.View.VISIBLE;

public class OsmNoteEditFragment extends BugEditFragment {

    private static String EXTRA_BUG = "EXTRA_BUG";

    private OpenstreetmapNote mBug;

    public static OsmNoteEditFragment newInstance(OpenstreetmapNote bug)
    {
        OsmNoteEditFragment instance = new OsmNoteEditFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_BUG, bug);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();

        mBug = args.getParcelable(EXTRA_BUG);

        View v = inflater.inflate(R.layout.fragment_osm_note_edit, null);

        TextView txtvDescription = (TextView) v.findViewById(R.id.txtvDescription);
        txtvDescription.setText(mBug.getDescription());

        CommentAdapter adapter = new CommentAdapter(getActivity());
        ListView lstvComments = (ListView) v.findViewById(R.id.lstvComments);
        lstvComments.setAdapter(adapter);
        adapter.addAll(mBug.getComments());
        adapter.notifyDataSetChanged();


        ImageButton imgvAddComment = (ImageButton) v.findViewById(R.id.imgbtnAddComment);
        if (mBug.getState() == OpenstreetmapNote.STATE.CLOSED) {
            imgvAddComment.setVisibility(GONE);
        }
        else {
            imgvAddComment.setOnClickListener(mAddCommentOnClickListener);
        }

        ImageButton mBtnResolve = (ImageButton) v.findViewById(R.id.btnResolveBug);
        if (mBug.getState() == OpenstreetmapNote.STATE.CLOSED) {
            mBtnResolve.setVisibility(GONE);
        }
        else {
            mBtnResolve.setOnClickListener(mBtnResolveOnClickListener);
        }

        return v;
    }

    private OnClickListener mAddCommentOnClickListener = new OnClickListener() {
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
                                    return OpenstreetmapNotesApi.addComment(
                                            mBug.getId(),
                                            Settings.OpenstreetmapNotes.getUsername(),
                                            Settings.OpenstreetmapNotes.getPassword(),
                                            message);
                                }

                                @Override
                                protected void onPostExecute(Boolean result) {
                                    super.onPostExecute(result);

                                    if (result) {
                                        mListener.onBugSaved(Globals.OSM_NOTES);
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

    private OnClickListener mBtnResolveOnClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            if (Settings.OpenstreetmapNotes.getUsername().equals("")) {
                Toast.makeText(getActivity(), R.string.notification_osm_notes_no_username, Toast.LENGTH_LONG).show();
                return;
            }

            if (Settings.OpenstreetmapNotes.getPassword().equals("")) {
                Toast.makeText(getActivity(), R.string.notification_osm_notes_no_password, Toast.LENGTH_LONG).show();
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
                                    return OpenstreetmapNotesApi.closeBug(
                                            mBug.getId(),
                                            Settings.OpenstreetmapNotes.getUsername(),
                                            Settings.OpenstreetmapNotes.getPassword(),
                                            message);
                                }

                                @Override
                                protected void onPostExecute (Boolean result){
                                    super.onPostExecute(result);

                                    if (result) {
                                        mListener.onBugSaved(Globals.OSM_NOTES);
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
