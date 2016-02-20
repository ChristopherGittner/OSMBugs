package org.gittner.osmbugs.api;

import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.common.OsmoseElement;
import org.gittner.osmbugs.parser.OsmoseParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OsmoseApi implements BugApi<OsmoseBug>
{
    public static OkHttpClient mOkHttpClient = new OkHttpClient();

    public ArrayList<OsmoseBug> downloadBBox(BoundingBoxE6 bBox)
    {
        Request request = new Request.Builder()
                .url(new HttpUrl.Builder()
                        .scheme("http")
                        .host("osmose.openstreetmap.fr")
                        .addPathSegment(!Settings.isLanguageGerman() ? "en" : "de")
                        .addPathSegment("api")
                        .addPathSegment("0.2")
                        .addPathSegment("errors")
                        .addQueryParameter("lat", String.valueOf(bBox.getCenter().getLatitudeE6() / 1000000.0))
                        .addQueryParameter("lon", String.valueOf(bBox.getCenter().getLongitudeE6() / 1000000.0))
                        .addQueryParameter("limit", String.valueOf(Settings.Osmose.getBugLimit()))
                        .addQueryParameter("full", "true")
                        .addQueryParameter("comment", Settings.Osmose.getBugsToDisplay() == 1 ? "done" : "false")
                        .build())
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            if (response.code() != 200)
            {
                return null;
            }

            return OsmoseParser.parseBugList(response.body().byteStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }


    public ArrayList<OsmoseElement> loadElements(long id)
    {
        Request request = new Request.Builder()
                .url(new HttpUrl.Builder()
                        .scheme("http")
                        .host("osmose.openstreetmap.fr")
                        .addPathSegment(!Settings.isLanguageGerman() ? "en" : "de")
                        .addPathSegment("api")
                        .addPathSegment("0.2")
                        .addPathSegment("error")
                        .addPathSegment((String.valueOf(id)))
                        .build())
                .build();

        try
        {
            Response response = mOkHttpClient.newCall(request).execute();

            if (response.code() != 200)
            {
                return null;
            }

            return OsmoseParser.parseBugElements(response.body().byteStream());
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return null;
        }
    }
}
