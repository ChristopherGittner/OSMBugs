package org.gittner.osmbugs.tasks;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.gittner.osmbugs.R;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class SendFeedbackTask extends AsyncTask<String, Void, Boolean> {

	Context context_;
	
	public SendFeedbackTask(Context context) {
		context_ = context;
	}
	
	@Override
	protected Boolean doInBackground(String... message) {
		
		if(message.length != 1)
			return false;
		
		HttpClient client = new DefaultHttpClient();
		
		ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
		arguments.add(new BasicNameValuePair("message", message[0]));

		HttpGet request = new HttpGet("http://gittner.org/osmbugs-scripts/feedback.php?" + URLEncodedUtils.format(arguments, "utf-8"));
		
		try {		
			/* Send Feedback */
			HttpResponse response = client.execute(request);
			
			/* Check result for Success*/
			if(response.getStatusLine().getStatusCode() != 200)
				return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return true;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		if(result)
			Toast.makeText(context_, context_.getString(R.string.feedback_transmitted), Toast.LENGTH_LONG).show();
		else
			Toast.makeText(context_, context_.getString(R.string.failed_to_transmit_feedback), Toast.LENGTH_LONG).show();
	}
}
