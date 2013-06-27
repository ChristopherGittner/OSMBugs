package org.gittner.osmbugs.tasks;

import com.actionbarsherlock.app.SherlockActivity;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.gittner.osmbugs.bugs.Bug;
import org.gittner.osmbugs.bugs.KeeprightParser;
import org.gittner.osmbugs.bugs.MapdustParser;
import org.gittner.osmbugs.bugs.OpenstreetbugsParser;
import org.gittner.osmbugs.bugs.OpenstreetmapNotesParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

import android.os.AsyncTask;

import java.io.IOException;
import java.util.ArrayList;

/* Background Worker Class to get a list of Bugs and ad them to the supplied ItemizedIconOverlay */
public class DownloadBugsTask extends AsyncTask<Void, Integer, ArrayList<Bug>> {

    SherlockActivity activity_;
    ItemizedIconOverlay<Bug> bugOverlay_;
    MapView mapView_;
    BoundingBoxE6 bBox_;
    int progress_;

    public DownloadBugsTask(SherlockActivity activity, ItemizedIconOverlay<Bug> bugOverlay, MapView mapView, BoundingBoxE6 bBox) {
        activity_ = activity;
        bugOverlay_ = bugOverlay;
        mapView_ = mapView;
        bBox_ = bBox;
    }

    @Override
    protected void onPreExecute() {
        /* Manage Progress Display */
        activity_.setSupportProgressBarIndeterminateVisibility(true);
        activity_.setSupportProgressBarVisibility(true);

        progress_ = 0;
        publishProgress(0);

        bugOverlay_.removeAllItems();
        mapView_.invalidate();
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {

        int activeProviders = 0;
        if(Settings.Keepright.isEnabled())
            activeProviders += 1;
        if(Settings.Openstreetbugs.isEnabled())
            activeProviders += 1;
        if(Settings.Mapdust.isEnabled())
            activeProviders += 1;
        if(Settings.OpenstreetmapNotes.isEnabled())
            activeProviders += 1;

        progress_ += progress[0];
        activity_.setSupportProgress(progress_ * 10000 / activeProviders / 2);
    }

    @Override
    protected ArrayList<Bug> doInBackground(Void... v) {
        ArrayList<Bug> result = new ArrayList<Bug>();

        if(Settings.Keepright.isEnabled()){
            result.addAll(downloadKeeprightBugs());
            publishProgress(1);
        }

        if(Settings.Openstreetbugs.isEnabled()){
            result.addAll(downloadOpenstreetbugsBugs());
            publishProgress(1);
        }

        if(Settings.Mapdust.isEnabled()){
            result.addAll(downloadMapdustBugs());
            publishProgress(1);
        }

        if(Settings.OpenstreetmapNotes.isEnabled()){
            result.addAll(downloadOpenstreetmapNotes());
            publishProgress(1);
        }

        return result;
    }

    @Override
    protected void onPostExecute(ArrayList<Bug> result) {
        bugOverlay_.removeAllItems();

        for(int i = 0; i != result.size(); ++i) {
            bugOverlay_.addItem(result.get(i));
        }

        mapView_.invalidate();

        activity_.setSupportProgressBarIndeterminateVisibility(false);
        activity_.setSupportProgressBarVisibility(false);
    }

    private ArrayList<Bug> downloadKeeprightBugs() {
        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();

        if(Settings.Keepright.isShowIgnoredEnabled())
            arguments.add(new BasicNameValuePair("show_ign", "1"));
        else
            arguments.add(new BasicNameValuePair("show_ign", "0"));

        if(Settings.Keepright.isShowTempIgnoredEnabled())
            arguments.add(new BasicNameValuePair("show_tmpign", "1"));
        else
            arguments.add(new BasicNameValuePair("show_tmpign", "0"));

        arguments.add(new BasicNameValuePair("ch", getKeeprightSelectionString()));
        arguments.add(new BasicNameValuePair("lat", String.valueOf(bBox_.getCenter().getLatitudeE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("lon", String.valueOf(bBox_.getCenter().getLongitudeE6() / 1000000.0)));
        if(Settings.isLanguageGerman())
            arguments.add(new BasicNameValuePair("lang", "de"));

        HttpGet request = new HttpGet("http://keepright.ipax.at/points.php?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute Query */
            HttpResponse response = client.execute(request);

            /* Check for Success */
            if(response.getStatusLine().getStatusCode() != 200)
                return new ArrayList<Bug>();

            /* Update Progress before Parsing */
            publishProgress(1);

            /* If Request was Successful, parse the Stream */
            return KeeprightParser.parse(response.getEntity().getContent());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<Bug>();
    }

    private String getKeeprightSelectionString() {
        /* Unknown what 0 stands for but its for compatibility Reasons */
        String result = "0,";

        if(Settings.Keepright.is20Enabled())
            result += "20,";
        if(Settings.Keepright.is30Enabled())
            result += "30,";
        if(Settings.Keepright.is40Enabled())
            result += "40,";
        if(Settings.Keepright.is50Enabled())
            result += "50,";
        if(Settings.Keepright.is60Enabled())
            result += "60,";
        if(Settings.Keepright.is70Enabled())
            result += "70,";
        if(Settings.Keepright.is90Enabled())
            result += "90,";
        if(Settings.Keepright.is100Enabled())
            result += "100,";
        if(Settings.Keepright.is110Enabled())
            result += "110,";
        if(Settings.Keepright.is120Enabled())
            result += "120,";
        if(Settings.Keepright.is130Enabled())
            result += "130,";
        if(Settings.Keepright.is150Enabled())
            result += "150,";
        if(Settings.Keepright.is160Enabled())
            result += "160,";
        if(Settings.Keepright.is170Enabled())
            result += "170,";
        if(Settings.Keepright.is180Enabled())
            result += "180,";
        if(Settings.Keepright.is190Enabled()){
            if(Settings.Keepright.is191Enabled())
                result += "191,";
            if(Settings.Keepright.is192Enabled())
                result += "192,";
            if(Settings.Keepright.is193Enabled())
                result += "193,";
            if(Settings.Keepright.is194Enabled())
                result += "194,";
            if(Settings.Keepright.is195Enabled())
                result += "195,";
            if(Settings.Keepright.is196Enabled())
                result += "196,";
            if(Settings.Keepright.is197Enabled())
                result += "197,";
            if(Settings.Keepright.is198Enabled())
                result += "198,";
        }
        if(Settings.Keepright.is200Enabled()){
            if(Settings.Keepright.is201Enabled())
                result += "201,";
            if(Settings.Keepright.is202Enabled())
                result += "202,";
            if(Settings.Keepright.is203Enabled())
                result += "203,";
            if(Settings.Keepright.is204Enabled())
                result += "204,";
            if(Settings.Keepright.is205Enabled())
                result += "205,";
            if(Settings.Keepright.is206Enabled())
                result += "206,";
            if(Settings.Keepright.is207Enabled())
                result += "207,";
            if(Settings.Keepright.is208Enabled())
                result += "208,";
        }
        if(Settings.Keepright.is210Enabled())
            result += "210,";
        if(Settings.Keepright.is220Enabled())
            result += "220,";
        if(Settings.Keepright.is230Enabled()){
            if(Settings.Keepright.is231Enabled())
                result += "231,";
            if(Settings.Keepright.is232Enabled())
                result += "232,";
        }
        if(Settings.Keepright.is270Enabled())
            result += "270,";
        if(Settings.Keepright.is280Enabled()){
            if(Settings.Keepright.is281Enabled())
                result += "281,";
            if(Settings.Keepright.is282Enabled())
                result += "282,";
            if(Settings.Keepright.is283Enabled())
                result += "283,";
            if(Settings.Keepright.is284Enabled())
                result += "284,";
            if(Settings.Keepright.is285Enabled())
                result += "285,";
        }
        if(Settings.Keepright.is290Enabled()){
            if(Settings.Keepright.is291Enabled())
                result += "291,";
            if(Settings.Keepright.is292Enabled())
                result += "292,";
            if(Settings.Keepright.is293Enabled())
                result += "293,";
            if(Settings.Keepright.is294Enabled())
                result += "294,";
        }
        if(Settings.Keepright.is300Enabled())
            result += "300,";
        if(Settings.Keepright.is310Enabled()){
            if(Settings.Keepright.is311Enabled())
                result += "311,";
            if(Settings.Keepright.is312Enabled())
                result += "312,";
            if(Settings.Keepright.is313Enabled())
                result += "313,";
        }
        if(Settings.Keepright.is320Enabled())
            result += "320,";
        if(Settings.Keepright.is350Enabled())
            result += "350,";
        if(Settings.Keepright.is360Enabled())
            result += "360,";
        if(Settings.Keepright.is370Enabled())
            result += "370,";
        if(Settings.Keepright.is380Enabled())
            result += "380,";
        if(Settings.Keepright.is390Enabled())
            result += "390,";
        if(Settings.Keepright.is400Enabled()){
            if(Settings.Keepright.is401Enabled())
                result += "401,";
            if(Settings.Keepright.is402Enabled())
                result += "402,";
        }
        if(Settings.Keepright.is410Enabled()){
            if(Settings.Keepright.is411Enabled())
                result += "411,";
            if(Settings.Keepright.is412Enabled())
                result += "412,";
            if(Settings.Keepright.is413Enabled())
                result += "413,";
        }

        if(result.endsWith(","))
            result = result.substring(0, result.length() - 1);

        return result;
    }


    private ArrayList<Bug> downloadOpenstreetbugsBugs() {
        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();

        arguments.add(new BasicNameValuePair("b", String.valueOf(bBox_.getLatSouthE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("t", String.valueOf(bBox_.getLatNorthE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("l", String.valueOf(bBox_.getLonWestE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("r", String.valueOf(bBox_.getLonEastE6() / 1000000.0)));

        if(Settings.Openstreetbugs.isShowOnlyOpenEnabled())
            arguments.add(new BasicNameValuePair("open", "1"));

        arguments.add(new BasicNameValuePair("limit", String.valueOf(Settings.Openstreetbugs.getBugLimit())));

        HttpGet request = new HttpGet("http://openstreetbugs.schokokeks.org/api/0.1/getGPX?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute Query */
            HttpResponse response = client.execute(request);

            /* Check for Success */
            if(response.getStatusLine().getStatusCode() != 200)
                return new ArrayList<Bug>();

            /* Update Progress before Parsing */
            publishProgress(1);

            /* If Request was Successful, parse the Stream */
            return OpenstreetbugsParser.parse(response.getEntity().getContent());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<Bug>();
    }

    private ArrayList<Bug> downloadMapdustBugs() {
        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();

        arguments.add(new BasicNameValuePair("key", Settings.Mapdust.getApiKey()));
        arguments.add(new BasicNameValuePair("bbox", String.valueOf(bBox_.getLonEastE6() / 1000000.0) + "," +
                String.valueOf(bBox_.getLatSouthE6() / 1000000.0) + "," +
                String.valueOf(bBox_.getLonWestE6() / 1000000.0) + "," +
                String.valueOf(bBox_.getLatNorthE6() / 1000000.0)));
        arguments.add(new BasicNameValuePair("comments", "1"));
        arguments.add(new BasicNameValuePair("ft", getMapdustSelectionString()));
        arguments.add(new BasicNameValuePair("fs", getMapdustEnabledTypesString()));

        HttpGet request;

        if(Settings.DEBUG)
            request = new HttpGet("http://st.www.mapdust.com/api/getBugs?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpGet("http://www.mapdust.com/api/getBugs?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute Query */
            HttpResponse response = client.execute(request);

            /* Check for Success */
            if(response.getStatusLine().getStatusCode() != 200)
                return new ArrayList<Bug>();

            /* Update Progress before Parsing */
            publishProgress(1);

            /* If Request was Successful, parse the Stream */
            return MapdustParser.parse(response.getEntity().getContent(), activity_);
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<Bug>();
    }

    private String getMapdustSelectionString() {
        String result = "";

        if(Settings.Mapdust.isWrongTurnEnabled())
            result += "wrong_turn,";
        if(Settings.Mapdust.isBadRoutingenabled())
            result += "bad_routing,";
        if(Settings.Mapdust.isOnewayRoadEnabled())
            result += "oneway_road,";
        if(Settings.Mapdust.isBlockedStreetEnabled())
            result += "blocked_street,";
        if(Settings.Mapdust.isMissingStreetEnabled())
            result += "missing_street,";
        if(Settings.Mapdust.isRoundaboutIssueEnabled())
            result += "wrong_roundabout,";
        if(Settings.Mapdust.isMissingSpeedInfoEnabled())
            result += "missing_speedlimit,";
        if(Settings.Mapdust.isOtherEnabled())
            result += "other,";

        if(result.endsWith(","))
            result = result.substring(0, result.length() - 1);

        return result;
    }

    private String getMapdustEnabledTypesString() {
        String result = "";

        if(Settings.Mapdust.isShowOpenEnabled())
            result += "1,";
        if(Settings.Mapdust.isShowClosedEnabled())
            result += "2,";
        if(Settings.Mapdust.isShowIgnoredEnabled())
            result += "3,";

        if(result.endsWith(","))
            result = result.substring(0, result.length() - 1);

        return result;
    }

    private ArrayList<Bug> downloadOpenstreetmapNotes() {
        HttpClient client = new DefaultHttpClient();

        ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();

        arguments.add(new BasicNameValuePair("bbox", String.valueOf(bBox_.getLonWestE6() / 1000000.0) +
                "," + String.valueOf(bBox_.getLatSouthE6() / 1000000.0) +
                "," + String.valueOf(bBox_.getLonEastE6() / 1000000.0) +
                "," + String.valueOf(bBox_.getLatNorthE6() / 1000000.0)));

        if(Settings.Openstreetbugs.isShowOnlyOpenEnabled())
            arguments.add(new BasicNameValuePair("closed", "0"));
        else
            arguments.add(new BasicNameValuePair("closed", "-1"));

        arguments.add(new BasicNameValuePair("limit", String.valueOf(Settings.Openstreetbugs.getBugLimit())));

        HttpGet request;

        if(!Settings.DEBUG)
            request = new HttpGet("http://api.openstreetmap.org/api/0.6/notes?" + URLEncodedUtils.format(arguments, "utf-8"));
        else
            request = new HttpGet("http://api06.dev.openstreetmap.org/api/0.6/notes?" + URLEncodedUtils.format(arguments, "utf-8"));

        try {
            /* Execute Query */
            HttpResponse response = client.execute(request);

            /* Check for Success */
            if(response.getStatusLine().getStatusCode() != 200)
                return new ArrayList<Bug>();

            /* Update Progress before Parsing */
            publishProgress(1);

            /* If Request was Successful, parse the Stream */
            return OpenstreetmapNotesParser.parse(response.getEntity().getContent());
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return new ArrayList<Bug>();
    }
}
