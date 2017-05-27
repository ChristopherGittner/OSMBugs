package org.gittner.osmbugs.Helpers;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;

import org.gittner.osmbugs.R;

public class EmailFeedbackStarter
{
    public static void start(Context context)
    {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.developer_mail)});
        intent.putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.feedback_to_osmbugs));
        intent.setType("plain/text");

        if (!IntentHelper.intentHasReceivers(context, intent))
        {
            new AlertDialog.Builder(context)
                    .setTitle(R.string.sending_feedback_failed_title)
                    .setMessage(R.string.sending_feedback_failed_message)
                    .setCancelable(true)
                    .create().show();
        }

        context.startActivity(intent);
    }
}
