package org.gittner.osmbugs.api;

import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.parser.OpenstreetmapNotesParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import okhttp3.Credentials;
import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OsmNotesApi implements BugApi<OsmNote>
{
    public static OkHttpClient mOkHttpClient = new OkHttpClient();

    @Override
    public ArrayList<OsmNote> downloadBBox(BoundingBoxE6 bBox)
    {
        return downloadBBox(
                bBox,
                Settings.OsmNotes.getBugLimit(),
                !Settings.OsmNotes.isShowOnlyOpenEnabled()
        );
    }


    private ArrayList<OsmNote> downloadBBox(BoundingBoxE6 bBox, int limit, boolean showClosed)
    {
        Request request = new Request.Builder()
                .url(new HttpUrl.Builder()
                        .scheme("http")
                        .host(!Settings.isDebugEnabled() ? "api.openstreetmap.org" : "api06.dev.openstreetmap.org")
                        .addPathSegment("api")
                        .addPathSegment("0.6")
                        .addPathSegment("notes")
                        .addQueryParameter("bbox", String.format(
                                Locale.US,
                                "%f,%f,%f,%f",
                                bBox.getLonWestE6() / 1000000.0,
                                bBox.getLatSouthE6() / 1000000.0,
                                bBox.getLonEastE6() / 1000000.0,
                                bBox.getLatNorthE6() / 1000000.0))
                        .addQueryParameter("closed", showClosed ? "1" : "0")
                        .addQueryParameter("limit", String.valueOf(limit))
                        .build())
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            if (response.code() != 200)
            {
                return null;
            }

            return OpenstreetmapNotesParser.parse(response.body().byteStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public boolean addComment(long id, String username, String password, String comment)
    {
        Request request = new Request.Builder()
                .url(new HttpUrl.Builder()
                        .scheme("http")
                        .host(!Settings.isDebugEnabled() ? "api.openstreetmap.org" : "api06.dev.openstreetmap.org")
                        .addPathSegment("api")
                        .addPathSegment("0.6")
                        .addPathSegment("notes")
                        .addPathSegment(String.valueOf(id))
                        .addPathSegment("comment")
                        .addQueryParameter("text", comment)
                        .build())
                .post(new FormBody.Builder().build())
                .addHeader("Authorization", Credentials.basic(username, password))
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            return response.code() == 200;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }


    public boolean closeBug(long id, String username, String password, String comment)
    {
        Request request = new Request.Builder()
                .url(new HttpUrl.Builder()
                        .scheme("http")
                        .host(!Settings.isDebugEnabled() ? "api.openstreetmap.org" : "api06.dev.openstreetmap.org")
                        .addPathSegment("api")
                        .addPathSegment("0.6")
                        .addPathSegment("notes")
                        .addPathSegment(String.valueOf(id))
                        .addPathSegment("close")
                        .addQueryParameter("text", comment)
                        .build())
                .post(new FormBody.Builder().build())
                .addHeader("Authorization", Credentials.basic(username, password))
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            return response.code() == 200;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }


    public boolean addNew(GeoPoint position, String text)
    {
        Request request = new Request.Builder()
                .url(new HttpUrl.Builder()
                        .scheme("http")
                        .host(!Settings.isDebugEnabled() ? "api.openstreetmap.org" : "api06.dev.openstreetmap.org")
                        .addPathSegment("api")
                        .addPathSegment("0.6")
                        .addPathSegment("notes")
                        .addQueryParameter("lat", String.valueOf(position.getLatitudeE6() / 1000000.0))
                        .addQueryParameter("lon", String.valueOf(position.getLongitudeE6() / 1000000.0))
                        .addQueryParameter("text", text)
                        .build())
                .post(new FormBody.Builder().build())
                .addHeader("Authorization", Credentials.basic(Settings.OsmNotes.getUsername(), Settings.OsmNotes.getPassword()))
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            return response.code() == 200;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return false;
        }
    }
}
