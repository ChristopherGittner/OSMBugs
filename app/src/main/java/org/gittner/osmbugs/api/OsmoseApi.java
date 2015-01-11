package org.gittner.osmbugs.api;

import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.parser.OsmoseParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;

import java.io.IOException;
import java.util.ArrayList;

public class OsmoseApi {

    public static ArrayList<OsmoseBug> downloadBBox(BoundingBoxE6 bBox) {
        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<>();

        arguments.add(new BasicNameValuePair("lat", String.valueOf(bBox.getCenter().getLatitudeE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("lon", String.valueOf(bBox.getCenter().getLongitudeE6() / 1000000.0)));

        arguments.add(new BasicNameValuePair("limit", String.valueOf(Settings.Osmose.getBugLimit())));

        arguments.add(new BasicNameValuePair("full", "true"));

        if(Settings.Osmose.getBugsToDisplay() == 1)
        {
            arguments.add(new BasicNameValuePair("status", "done"));
        }
        if(Settings.Osmose.getBugsToDisplay() == 2)
        {
            arguments.add(new BasicNameValuePair("status", "false"));
        }

        String api;
        if(Settings.isLanguageGerman()) {
            api = "http://osmose.openstreetmap.fr/de/api/0.2/errors?";
        }
        else
        {
            api = "http://osmose.openstreetmap.fr/en/api/0.2/errors?";
        }
        HttpGet request = new HttpGet(api + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute Query */
            HttpResponse response = client.execute(request);

            /* Check for Success */
            if (response.getStatusLine().getStatusCode() != 200)
                return null;

            /* If Request was Successful, parse the Stream */
            return OsmoseParser.parse(response.getEntity().getContent());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }
}
