package org.gittner.osmbugs.activities;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.Extra;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.KeeprightApi;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.statics.Images;

@EActivity(R.layout.activity_keepright_edit)
@OptionsMenu(R.menu.keepright_edit)
public class KeeprightEditActivity
        extends ActionBarActivity
        implements BugEditActivityConstants
{
    @ViewById(R.id.txtvTitle)
    TextView mTitle;
    @ViewById(R.id.txtvText)
    TextView mText;
    @ViewById(R.id.edttxtComment)
    EditText mComment;
    @ViewById(R.id.spnState)
    Spinner mState;

    @Extra(EXTRA_BUG)
    KeeprightBug mBug;

    private MaterialDialog mSaveDialog = null;


    @AfterViews
    void init()
    {
        mTitle.setText(mBug.getTitle());
        Linkify.addLinks(mTitle, Linkify.WEB_URLS);
        mTitle.setText(Html.fromHtml(mTitle.getText().toString()));

        mText.setText(mBug.getDescription());
        Linkify.addLinks(mText, Linkify.WEB_URLS);
        mText.setText(Html.fromHtml(mText.getText().toString()));
        mText.setMovementMethod(LinkMovementMethod.getInstance());

        mComment.setText(mBug.getComment());

        mState.setAdapter(new KeeprightStateAdapter(this, mBug.getOpenIcon()));
        switch (mBug.getState())
        {
            case OPEN:
                mState.setSelection(0);
                break;

            case IGNORED_TMP:
                mState.setSelection(1);
                break;

            case IGNORED:
                mState.setSelection(2);
                break;
        }

        mSaveDialog = new MaterialDialog.Builder(this)
                .title(R.string.saving)
                .content(R.string.please_wait)
                .cancelable(false)
                .progress(true, 0)
                .build();
    }


    @OptionsItem(R.id.action_done)
    void menuDoneClicked()
    {
        KeeprightBug.STATE state;
        switch (mState.getSelectedItemPosition())
        {
            case 0:
                state = KeeprightBug.STATE.OPEN;
                break;

            case 1:
                state = KeeprightBug.STATE.IGNORED_TMP;
                break;

            default:
                state = KeeprightBug.STATE.IGNORED;
                break;
        }

        mSaveDialog.show();

        uploadData(mComment.getText().toString(), state);
    }


    @Background
    void uploadData(String comment, KeeprightBug.STATE state)
    {
        boolean result = new KeeprightApi().comment(
                mBug.getSchema(),
                mBug.getId(),
                comment,
                state);

        uploadDataDone(result);
    }


    @UiThread
    void uploadDataDone(boolean result)
    {
        mSaveDialog.dismiss();

        if (result)
        {
            setResult(RESULT_SAVED_KEEPRIGHT);
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


    private class KeeprightStateAdapter extends ArrayAdapter<KeeprightBug.STATE>
    {
        final Drawable mIcon;


        public KeeprightStateAdapter(Context context, Drawable icon)
        {
            super(context, R.layout.row_keepright_bug_state, R.id.txtvState);

            addAll(
                    KeeprightBug.STATE.OPEN,
                    KeeprightBug.STATE.IGNORED_TMP,
                    KeeprightBug.STATE.IGNORED);

            mIcon = icon;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }


        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent)
        {
            return getCustomView(position, convertView, parent);
        }


        private View getCustomView(int position, View convertView, ViewGroup parent)
        {
            View v = convertView != null ? convertView : LayoutInflater.from(getContext()).inflate(R.layout.row_keepright_bug_state, parent, false);

            ImageView imgvIcon = (ImageView) v.findViewById(R.id.imgvIcon);
            TextView txtvState = (TextView) v.findViewById(R.id.txtvState);

            switch (position)
            {
                case 0:
                    imgvIcon.setImageDrawable(mIcon);
                    txtvState.setText(R.string.open);
                    break;

                case 1:
                    imgvIcon.setImageDrawable(Images.get(R.drawable.keepright_zap_closed));
                    txtvState.setText(R.string.closed);
                    break;

                default:
                    imgvIcon.setImageDrawable(Images.get(R.drawable.keepright_zap_ignored));
                    txtvState.setText(R.string.ignored);
                    break;
            }

            return v;
        }
    }
}