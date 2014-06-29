package org.gittner.osmbugs.api;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.gittner.osmbugs.bugs.OpenstreetmapNote;
import org.gittner.osmbugs.parser.OpenstreetmapNotesParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;

import java.io.IOException;
import java.util.ArrayList;

public class OpenstreetmapNotesApi {

    public static ArrayList<OpenstreetmapNote> downloadBBox(BoundingBoxE6 bBox, int limit, boolean showClosed) {
        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();

        arguments.add(new BasicNameValuePair("bbox", String.valueOf(bBox.getLonWestE6() / 1000000.0) + ","
                + String.valueOf(bBox.getLatSouthE6() / 1000000.0) + ","
                + String.valueOf(bBox.getLonEastE6() / 1000000.0) + ","
                + String.valueOf(bBox.getLatNorthE6() / 1000000.0)));

        if (!showClosed)
            arguments.add(new BasicNameValuePair("closed", "0"));

        arguments.add(new BasicNameValuePair("limit", String.valueOf(limit)));

        HttpGet request;

        if (!Settings.isDebugEnabled())
            request = new HttpGet("http://api.openstreetmap.org/api/0.6/notes?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpGet("http://api06.dev.openstreetmap.org/api/0.6/notes?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute Query */
            HttpResponse response = client.execute(request);

            /* Check for Success */
            if (response.getStatusLine().getStatusCode() != 200)
                return null;

            /* If Request was Successful, parse the Stream */
            return OpenstreetmapNotesParser.parse(response.getEntity().getContent());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean addComment(long id, String username, String password, String comment)
    {
        DefaultHttpClient client = new DefaultHttpClient();

        /* Add the Authentication Details if we have a username */
        if (!username.equals("")) {
            client.getCredentialsProvider().setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password)
            );
        }

        /* Add all Arguments */
        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
        arguments.add(new BasicNameValuePair("text", comment));

        HttpPost request;
        if (!Settings.isDebugEnabled())
            request = new HttpPost("http://api.openstreetmap.org/api/0.6/notes/" + id + "/comment?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpPost("http://api06.dev.openstreetmap.org/api/0.6/notes/" + id + "/comment?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute commit */
            HttpResponse response = client.execute(request);

            /* Check result for Success */
            if (response.getStatusLine().getStatusCode() != 200)
                return false;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean closeBug(long id, String username, String password, String comment)
    {
        DefaultHttpClient client = new DefaultHttpClient();

        /* Add the Authentication Details if we have a username */
        if (!username.equals("")) {
            client.getCredentialsProvider().setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(username, password)
            );
        }

        /* Add all Arguments */
        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
        arguments.add(new BasicNameValuePair("text", comment));

        HttpPost request;
        if (!Settings.isDebugEnabled())
            request = new HttpPost("http://api.openstreetmap.org/api/0.6/notes/" + id + "/close?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpPost("http://api06.dev.openstreetmap.org/api/0.6/notes/" + id + "/close?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute commit */
            HttpResponse response = client.execute(request);

            /* Check result for Success */
            if (response.getStatusLine().getStatusCode() != 200)
                return false;
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
