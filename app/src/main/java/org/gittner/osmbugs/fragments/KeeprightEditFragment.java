package org.gittner.osmbugs.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.KeeprightApi;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.common.IndeterminateProgressAsyncTask;
import org.gittner.osmbugs.statics.Images;
import org.gittner.osmbugs.statics.Globals;

public class KeeprightEditFragment extends BugEditFragment {

    private static final String EXTRA_BUG = "EXTRA_BUG";

    private KeeprightBug mBug;

    private EditText mEdtxtComment;

    private Spinner mSpnState;

    public static KeeprightEditFragment newInstance(KeeprightBug bug)
    {
        KeeprightEditFragment instance = new KeeprightEditFragment();

        Bundle args = new Bundle();
        args.putParcelable(EXTRA_BUG, bug);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Bundle args = getArguments();

        mBug = args.getParcelable(EXTRA_BUG);

        View v = inflater.inflate(R.layout.fragment_keepright_edit, null);

        TextView txtvTitle = (TextView) v.findViewById(R.id.txtvTitle);
        txtvTitle.setText(mBug.getTitle());
        Linkify.addLinks(txtvTitle, Linkify.WEB_URLS);
        txtvTitle.setText(Html.fromHtml(txtvTitle.getText().toString()));

        TextView txtvText = (TextView) v.findViewById(R.id.txtvText);
        txtvText.setText(mBug.getDescription());
        Linkify.addLinks(txtvText, Linkify.WEB_URLS);
        txtvText.setText(Html.fromHtml(txtvText.getText().toString()));
        txtvText.setMovementMethod(LinkMovementMethod.getInstance());

        mEdtxtComment = (EditText) v.findViewById(R.id.edttxtComment);
        mEdtxtComment.setText(mBug.getComment());

        mSpnState = (Spinner) v.findViewById(R.id.spnState);
        mSpnState.setAdapter(new KeeprightStateAdapter(getActivity(), mBug.getOpenIcon()));
        switch (mBug.getState())
        {
            case OPEN:
                mSpnState.setSelection(0);
                break;

            case IGNORED_TMP:
                mSpnState.setSelection(1);
                break;

            case IGNORED:
                mSpnState.setSelection(2);
                break;
        }

        ImageButton btnSave = (ImageButton) v.findViewById(R.id.btnSave);
        btnSave.setOnClickListener(mBtnSaveOnClickListener);

        return v;
    }

    private class KeeprightStateAdapter extends ArrayAdapter<KeeprightBug.STATE>
    {

        final Drawable mIcon;

        public KeeprightStateAdapter(Context context, Drawable icon) {
            super(context, R.layout.row_keepright_bug_state, R.id.txtvState);

            add(KeeprightBug.STATE.OPEN);
            add(KeeprightBug.STATE.IGNORED_TMP);
            add(KeeprightBug.STATE.IGNORED);

            mIcon = icon;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView);
        }

        private View getCustomView(int position, View convertView)
        {
            View v = convertView;

            if(v == null)
            {
                v = LayoutInflater.from(getContext()).inflate(R.layout.row_keepright_bug_state, null);
            }

            ImageView imgvIcon = (ImageView) v.findViewById(R.id.imgvIcon);
            TextView txtvState = (TextView) v.findViewById(R.id.txtvState);

            switch(position)
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

    private final View.OnClickListener mBtnSaveOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            new IndeterminateProgressAsyncTask<Void, Void, Boolean>(getActivity(), R.string.saving){

                @Override
                protected Boolean doInBackground(Void... params) {
                    KeeprightBug.STATE state;
                    switch(mSpnState.getSelectedItemPosition())
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
                    return KeeprightApi.comment(
                            mBug.getSchema(),
                            mBug.getId(),
                            mEdtxtComment.getText().toString(),
                            state);
                }

                @Override
                protected void onPostExecute(Boolean result) {
                    super.onPostExecute(result);

                    if(result)
                    {
                        mListener.onBugSaved(Globals.KEEPRIGHT);
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
    };
}
