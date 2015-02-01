package org.gittner.osmbugs;

import android.app.Application;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;
import org.acra.sender.HttpSender;

@ReportsCrashes(
		formKey = "",
		httpMethod = HttpSender.Method.PUT,
		reportType = HttpSender.Type.JSON,
		formUri = "https://gittner.org:6984/acra-osmbugs/_design/acra-storage/_update/report",
		formUriBasicAuthLogin = "osmbugs-report-user",
		formUriBasicAuthPassword = "osmbugs",
		disableSSLCertValidation = true,
		customReportContent = {
				ReportField.APP_VERSION_CODE,
				ReportField.APP_VERSION_NAME,
				ReportField.ANDROID_VERSION,
				ReportField.PACKAGE_NAME,
				ReportField.REPORT_ID,
				ReportField.BUILD,
				ReportField.STACK_TRACE},
		mode = ReportingInteractionMode.DIALOG,
		resDialogText = R.string.crash_dialog_text,
		resDialogIcon = android.R.drawable.ic_dialog_info,
		resDialogTitle = R.string.crash_dialog_title,
		resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
		resDialogOkToast = R.string.crash_dialog_ok_toast)
public class OsmbugsApplication extends Application
{
	@Override
	public void onCreate()
	{
		super.onCreate();

		/* Enable Acra Crash reports only on Release */
		if(!BuildConfig.DEBUG)
		{
			ACRA.init(this);
		}
	}
}
