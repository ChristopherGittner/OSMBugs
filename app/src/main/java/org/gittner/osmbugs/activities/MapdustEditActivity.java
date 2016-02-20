package org.gittner.osmbugs.activities;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
import com.rey.material.widget.ProgressView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.OptionsMenuItem;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.api.MapdustApi;
import org.gittner.osmbugs.bugs.MapdustBug;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.statics.GeoIntentStarter;
import org.gittner.osmbugs.statics.Settings;

import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

@EActivity(R.layout.activity_mapdust_edit)
@OptionsMenu(R.menu.mapdust_edit)
public class MapdustEditActivity
        extends ActionBarActivity
        implements BugEditActivityConstants
{
    @Extra(EXTRA_BUG)
    MapdustBug mBug;

    @ViewById(R.id.txtvDescription)
    TextView mDescription;
    @ViewById(R.id.pbarLoadingComments)
    ProgressView mProgressBarComments;
    @ViewById(R.id.lstvComments)
    ListView mComments;
    @ViewById(R.id.imgbtnAddComment)
    ImageButton mAddComment;

    @OptionsMenuItem(R.id.action_close)
    MenuItem mMenuCloseBug;
    @OptionsMenuItem(R.id.action_ignore)
    MenuItem mMenuIgnoreBug;

    private CommentAdapter mAdapter;

    private MaterialDialog mSaveDialog = null;


    @AfterViews
    void init()
    {
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(mBug.getIcon());

        mDescription.setText(mBug.getDescription());

        mAdapter = new CommentAdapter(this);
        mComments.setAdapter(mAdapter);

        if (mBug.getState() == MapdustBug.STATE.CLOSED)
        {
            mAddComment.setVisibility(GONE);
        }

        mSaveDialog = new MaterialDialog.Builder(this)
                .title(R.string.saving)
                .content(R.string.please_wait)
                .cancelable(false)
                .progress(true, 0)
                .build();

        loadComments();
    }


    @Background
    void loadComments()
    {
        List<Comment> comments = Apis.MAPDUST.retrieveComments(mBug.getId());

        commentsLoaded(comments);
    }


    @UiThread
    void commentsLoaded(List<Comment> comments)
    {
        mBug.setComments(comments);

        mAdapter.addAll(comments);
        mAdapter.notifyDataSetChanged();

        mProgressBarComments.setVisibility(View.GONE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        boolean visible = mBug.getState() == MapdustBug.STATE.OPEN;

        mMenuCloseBug.setVisible(visible);
        mMenuIgnoreBug.setVisible(visible);

        return true;
    }


    @OptionsItem(R.id.action_close)
    void menuCloseBugClicked()
    {
        final EditText resolveComment = new EditText(this);
        new MaterialDialog.Builder(this)
                .customView(resolveComment, false)
                .cancelable(true)
                .title(R.string.enter_comment)
                .positiveText(R.string.close)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        mSaveDialog.show();

                        uploadBugStatus(
                                MapdustBug.STATE.CLOSED,
                                resolveComment.getText().toString());
                    }
                }).show();
    }


    @OptionsItem(R.id.action_ignore)
    void menuIgnoreBugClicked()
    {
        final EditText resolveComment = new EditText(this);
        new MaterialDialog.Builder(this)
                .customView(resolveComment, false)
                .cancelable(true)
                .title(R.string.enter_comment)
                .positiveText(R.string.close)
                .negativeText(R.string.cancel)
                .callback(new MaterialDialog.ButtonCallback()
                {
                    @Override
                    public void onPositive(MaterialDialog dialog)
                    {
                        mSaveDialog.show();

                        uploadBugStatus(
                                MapdustBug.STATE.IGNORED,
                                resolveComment.getText().toString());
                    }
                }).show();
    }


    @Background
    void uploadBugStatus(MapdustBug.STATE state, String message)
    {
        boolean result = Apis.MAPDUST.changeBugStatus(
                mBug.getId(),
                state,
                message,
                Settings.Mapdust.getUsername());

        uploadDone(result);
    }


    @UiThread
    void uploadDone(boolean result)
    {
        mSaveDialog.dismiss();

        if (result)
        {
            setResult(RESULT_OK);
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
        boolean result = new MapdustApi().commentBug(
                mBug.getId(),
                comment,
                Settings.Mapdust.getUsername());

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


    @OptionsItem(R.id.action_share)
    void shareBug()
    {
        GeoIntentStarter.start(this, mBug.getPoint());
    }
}
