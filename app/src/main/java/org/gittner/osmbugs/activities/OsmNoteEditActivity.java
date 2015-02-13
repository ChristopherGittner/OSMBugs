package org.gittner.osmbugs.activities;

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
import org.gittner.osmbugs.api.OsmNotesApi;
import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.common.IndeterminateProgressAsyncTask;
import org.gittner.osmbugs.statics.Settings;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

public class OsmNoteEditActivity extends BugEditActivity
{
	private static final String EXTRA_BUG = "EXTRA_BUG";

	private OsmNote mBug;

	@Override
	protected void onCreate(final Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_osm_note_edit);

		Bundle args = getIntent().getExtras();

		mBug = args.getParcelable(EXTRA_BUG);

		TextView txtvDescription = (TextView) findViewById(R.id.txtvDescription);
		txtvDescription.setText(mBug.getDescription());

		CommentAdapter adapter = new CommentAdapter(this);
		ListView lstvComments = (ListView) findViewById(R.id.lstvComments);
		lstvComments.setAdapter(adapter);
		adapter.addAll(mBug.getComments());
		adapter.notifyDataSetChanged();


		ImageButton imgvAddComment = (ImageButton) findViewById(R.id.imgbtnAddComment);
		if (mBug.getState() == OsmNote.STATE.CLOSED) {
			imgvAddComment.setVisibility(GONE);
		}
		else {
			imgvAddComment.setOnClickListener(mAddCommentOnClickListener);
		}

		ImageButton mBtnResolve = (ImageButton) findViewById(R.id.btnResolveBug);
		if (mBug.getState() == OsmNote.STATE.CLOSED) {
			mBtnResolve.setVisibility(GONE);
		}
		else {
			mBtnResolve.setOnClickListener(mBtnResolveOnClickListener);
		}
	}

	private final View.OnClickListener mAddCommentOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			final EditText edtxtNewComment = new EditText(OsmNoteEditActivity.this);

			new AlertDialog.Builder(OsmNoteEditActivity.this)
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
											OsmNoteEditActivity.this,
											R.string.saving) {
										@Override
										protected Boolean doInBackground(Void... params) {
											return new OsmNotesApi().addComment(
													mBug.getId(),
													Settings.OsmNotes.getUsername(),
													Settings.OsmNotes.getPassword(),
													message);
										}

										@Override
										protected void onPostExecute(Boolean result) {
											super.onPostExecute(result);

											if (result) {
												setResult(RESULT_SAVED_OSM_NOTES);
												finish();
											}
											else
											{
												new AlertDialog.Builder(OsmNoteEditActivity.this)
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

	private final View.OnClickListener mBtnResolveOnClickListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {

			if (Settings.OsmNotes.getUsername().equals("")) {
				Toast.makeText(
						OsmNoteEditActivity.this,
						R.string.notification_osm_notes_no_username,
						Toast.LENGTH_LONG).show();
				return;
			}

			if (Settings.OsmNotes.getPassword().equals("")) {
				Toast.makeText(OsmNoteEditActivity.this, R.string.notification_osm_notes_no_password, Toast.LENGTH_LONG).show();
				return;
			}

			final EditText edtxtResolveComment = new EditText(OsmNoteEditActivity.this);

			new AlertDialog.Builder(OsmNoteEditActivity.this)
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
											OsmNoteEditActivity.this,
											R.string.saving) {
										@Override
										protected Boolean doInBackground (Void...params){
											return new OsmNotesApi().closeBug(
													mBug.getId(),
													Settings.OsmNotes.getUsername(),
													Settings.OsmNotes.getPassword(),
													message);
										}

										@Override
										protected void onPostExecute (Boolean result){
											super.onPostExecute(result);

											if (result) {
												setResult(RESULT_SAVED_OSM_NOTES);
												finish();
											} else {
												new AlertDialog.Builder(OsmNoteEditActivity.this)
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

	public class CommentAdapter extends ArrayAdapter<Comment>
	{

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
