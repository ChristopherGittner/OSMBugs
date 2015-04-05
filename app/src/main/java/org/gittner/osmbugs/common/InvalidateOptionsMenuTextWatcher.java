package org.gittner.osmbugs.common;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;

public class InvalidateOptionsMenuTextWatcher implements TextWatcher
{
    private final Activity mActivity;


    public InvalidateOptionsMenuTextWatcher(final Activity activity)
    {
        mActivity = activity;
    }


    @Override
    public void beforeTextChanged(final CharSequence s, final int start, final int count, final int after)
    {

    }


    @Override
    public void onTextChanged(final CharSequence s, final int start, final int before, final int count)
    {

    }


    @Override
    public void afterTextChanged(final Editable s)
    {
        mActivity.invalidateOptionsMenu();
    }
}
