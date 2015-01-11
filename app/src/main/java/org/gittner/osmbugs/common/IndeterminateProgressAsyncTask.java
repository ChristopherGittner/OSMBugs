package org.gittner.osmbugs.common;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class IndeterminateProgressAsyncTask<Params, Progress, Result> extends AsyncTask<Params, Progress, Result> {

    private final ProgressDialog mProgressDialog;

    public IndeterminateProgressAsyncTask(Context context, int message)
    {
        this(context, context.getString(message));
    }

    private IndeterminateProgressAsyncTask(Context context, String message)
    {
        mProgressDialog = new ProgressDialog(context);
        mProgressDialog.setMessage(message);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setCancelable(false);
    }

    @Override
    protected void onPreExecute() {
        mProgressDialog.show();
    }

    @Override
    protected void onPostExecute(Result result) {
        mProgressDialog.dismiss();
    }

    @Override
    protected void onCancelled() {
        mProgressDialog.dismiss();
    }

    @Override
    protected void onCancelled(Result result) {
        mProgressDialog.dismiss();
    }
}
