package org.gittner.osmbugs.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatSpinner;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EFragment;
import org.androidannotations.annotations.FragmentArg;
import org.androidannotations.annotations.OptionsItem;
import org.androidannotations.annotations.OptionsMenu;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.Apis;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.statics.Images;

@EFragment(R.layout.fragment_keepright_edit)
@OptionsMenu(R.menu.keepright_edit)
public class KeeprightEditFragment
        extends Fragment
{
    public static final String ARG_BUG = "ARG_BUG";

    @ViewById(R.id.creationDate)
    TextView mCreationDate;
    @ViewById(R.id.txtvTitle)
    TextView mTitle;
    @ViewById(R.id.txtvText)
    TextView mText;
    @ViewById(R.id.edttxtComment)
    AppCompatEditText mComment;
    @ViewById(R.id.spnState)
    AppCompatSpinner mState;

    @FragmentArg(ARG_BUG)
    KeeprightBug mBug;

    private ProgressDialog mSaveDialog = null;


    @AfterViews
    void init()
    {
        mCreationDate.setText(mBug.getCreationDate().toString(getString(R.string.date_time_format)));

        mTitle.setText(mBug.getTitle());
        Linkify.addLinks(mTitle, Linkify.WEB_URLS);
        mTitle.setText(Html.fromHtml(mTitle.getText().toString()));

        mText.setText(mBug.getDescription());
        Linkify.addLinks(mText, Linkify.WEB_URLS);
        mText.setText(Html.fromHtml(mText.getText().toString()));
        mText.setMovementMethod(LinkMovementMethod.getInstance());

        mComment.setText(Html.fromHtml(mBug.getComment()));

        mState.setAdapter(new KeeprightStateAdapter(getActivity(), mBug.getOpenIcon()));
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

        mSaveDialog = new ProgressDialog(getActivity());
        mSaveDialog.setTitle(R.string.saving);
        mSaveDialog.setMessage(getString(R.string.please_wait));
        mSaveDialog.setCancelable(false);
        mSaveDialog.setIndeterminate(true);
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
        boolean result = Apis.KEEPRIGHT.comment(
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
            getActivity().setResult(Activity.RESULT_OK);
            getActivity().finish();
        }
        else
        {
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error)
                    .setMessage(R.string.failed_to_save_bug)
                    .setCancelable(true)
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

            ImageView icon = v.findViewById(R.id.imgvIcon);
            TextView state = v.findViewById(R.id.txtvState);

            switch (position)
            {
                case 0:
                    icon.setImageDrawable(mIcon);
                    state.setText(R.string.open);
                    break;

                case 1:
                    icon.setImageDrawable(Images.get(R.drawable.keepright_zap_closed));
                    state.setText(R.string.closed);
                    break;

                default:
                    icon.setImageDrawable(Images.get(R.drawable.keepright_zap_ignored));
                    state.setText(R.string.ignored);
                    break;
            }

            return v;
        }
    }
}
