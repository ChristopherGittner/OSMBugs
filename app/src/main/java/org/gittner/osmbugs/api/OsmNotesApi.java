package org.gittner.osmbugs.api;

import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.parser.OsmNotesParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBox;
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
    private static final String API_SCHEME = "https";
    private static final String DEFAULT_SERVER = "api.openstreetmap.org";
    private static final String DEBUG_SERVER = "api06.dev.openstreetmap.org";
    
    private static final String PATH_SEGMENT_API = "api";
    private static final String PATH_SEGMENT_VERSION = "0.6";
    private static final String PATH_SEGMENT_NOTES = "notes";

    private static final String HEADER_AUTHORIZATION = "Authorization";
    
    private static OkHttpClient mOkHttpClient = new OkHttpClient();

    @Override
    public ArrayList<OsmNote> downloadBBox(BoundingBox bBox)
    {
        return downloadBBox(
                bBox,
                Settings.OsmNotes.getBugLimit(),
                !Settings.OsmNotes.isShowOnlyOpenEnabled()
        );
    }


    private ArrayList<OsmNote> downloadBBox(BoundingBox bBox, int limit, boolean showClosed)
    {
        Request request = new Request.Builder()
                .url(new HttpUrl.Builder()
                        .scheme(API_SCHEME)
                        .host(!Settings.isDebugEnabled() ? DEFAULT_SERVER : DEBUG_SERVER)
                        .addPathSegment(PATH_SEGMENT_API)
                        .addPathSegment(PATH_SEGMENT_VERSION)
                        .addPathSegment(PATH_SEGMENT_NOTES)
                        .addQueryParameter("bbox", String.format(
                                Locale.US,
                                "%f,%f,%f,%f",
                                bBox.getLonWest(),
                                bBox.getLatSouth(),
                                bBox.getLonEast(),
                                bBox.getLatNorth()))
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

            return OsmNotesParser.parse(response.body().byteStream());
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
                        .scheme(API_SCHEME)
                        .host(!Settings.isDebugEnabled() ? DEFAULT_SERVER : DEBUG_SERVER)
                        .addPathSegment(PATH_SEGMENT_API)
                        .addPathSegment(PATH_SEGMENT_VERSION)
                        .addPathSegment(PATH_SEGMENT_NOTES)
                        .addPathSegment(String.valueOf(id))
                        .addPathSegment("comment")
                        .addQueryParameter("text", comment)
                        .build())
                .post(new FormBody.Builder().build())
                .addHeader(HEADER_AUTHORIZATION, Credentials.basic(username, password))
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


    public boolean closeBug(long id, String username, String password, String comment) throws AuthenticationRequiredException
    {
        Request request = new Request.Builder()
                .url(new HttpUrl.Builder()
                        .scheme(API_SCHEME)
                        .host(!Settings.isDebugEnabled() ? DEFAULT_SERVER : DEBUG_SERVER)
                        .addPathSegment(PATH_SEGMENT_API)
                        .addPathSegment(PATH_SEGMENT_VERSION)
                        .addPathSegment(PATH_SEGMENT_NOTES)
                        .addPathSegment(String.valueOf(id))
                        .addPathSegment("close")
                        .addQueryParameter("text", comment)
                        .build())
                .post(new FormBody.Builder().build())
                .addHeader(HEADER_AUTHORIZATION, Credentials.basic(username, password))
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            if(response.code() == 401) {
                throw new AuthenticationRequiredException();
            }

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
                        .scheme(API_SCHEME)
                        .host(!Settings.isDebugEnabled() ? DEFAULT_SERVER : DEBUG_SERVER)
                        .addPathSegment(PATH_SEGMENT_API)
                        .addPathSegment(PATH_SEGMENT_VERSION)
                        .addPathSegment(PATH_SEGMENT_NOTES)
                        .addQueryParameter("lat", String.valueOf(position.getLatitude()) )
                        .addQueryParameter("lon", String.valueOf(position.getLongitude()))
                        .addQueryParameter("text", text)
                        .build())
                .post(new FormBody.Builder().build())
                .addHeader(HEADER_AUTHORIZATION, Credentials.basic(Settings.OsmNotes.getUsername(), Settings.OsmNotes.getPassword()))
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

    public class AuthenticationRequiredException extends Throwable
    {
    }
}
