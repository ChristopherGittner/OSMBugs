package org.gittner.osmbugs.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.text.util.Linkify;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import org.gittner.osmbugs.R;
import org.gittner.osmbugs.api.KeeprightApi;
import org.gittner.osmbugs.bugs.KeeprightBug;
import org.gittner.osmbugs.common.IndeterminateProgressAsyncTask;
import org.gittner.osmbugs.statics.Images;

public class KeeprightEditActivity extends BugEditActivity
{
    private KeeprightBug mBug = null;
    private EditText mEdtxtComment = null;
    private Spinner mSpnState = null;


    @Override
    protected void onCreate(final Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_keepright_edit);

        Bundle args = getIntent().getExtras();
        mBug = args.getParcelable(EXTRA_BUG);

        TextView txtvTitle = (TextView) findViewById(R.id.txtvTitle);
        txtvTitle.setText(mBug.getTitle());
        Linkify.addLinks(txtvTitle, Linkify.WEB_URLS);
        txtvTitle.setText(Html.fromHtml(txtvTitle.getText().toString()));

        TextView txtvText = (TextView) findViewById(R.id.txtvText);
        txtvText.setText(mBug.getDescription());
        Linkify.addLinks(txtvText, Linkify.WEB_URLS);
        txtvText.setText(Html.fromHtml(txtvText.getText().toString()));
        txtvText.setMovementMethod(LinkMovementMethod.getInstance());

        mEdtxtComment = (EditText) findViewById(R.id.edttxtComment);
        mEdtxtComment.setText(mBug.getComment());

        mSpnState = (Spinner) findViewById(R.id.spnState);
        mSpnState.setAdapter(new KeeprightStateAdapter(this, mBug.getOpenIcon()));
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
    }


    @Override
    public boolean onCreateOptionsMenu(final Menu menu)
    {
        getMenuInflater().inflate(R.menu.keepright_edit, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(final MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.action_done:
                menuDoneClicked();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void menuDoneClicked()
    {
        new IndeterminateProgressAsyncTask<Void, Void, Boolean>(this, R.string.saving)
        {
            @Override
            protected Boolean doInBackground(Void... params)
            {
                KeeprightBug.STATE state;
                switch (mSpnState.getSelectedItemPosition())
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

                return new KeeprightApi().comment(
                        mBug.getSchema(),
                        mBug.getId(),
                        mEdtxtComment.getText().toString(),
                        state);
            }


            @Override
            protected void onPostExecute(Boolean result)
            {
                super.onPostExecute(result);
                if (result)
                {
                    setResult(RESULT_SAVED_KEEPRIGHT);
                    finish();
                }
                else
                {
                    new AlertDialog.Builder(KeeprightEditActivity.this)
                            .setMessage(R.string.failed_to_save_bug)
                            .setCancelable(true)
                            .create().show();
                }
            }
        }.execute();
    }


    private class KeeprightStateAdapter extends ArrayAdapter<KeeprightBug.STATE>
    {
        final Drawable mIcon;


        public KeeprightStateAdapter(Context context, Drawable icon)
        {
            super(context, R.layout.row_keepright_bug_state, R.id.txtvState);

            add(KeeprightBug.STATE.OPEN);
            add(KeeprightBug.STATE.IGNORED_TMP);
            add(KeeprightBug.STATE.IGNORED);

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
