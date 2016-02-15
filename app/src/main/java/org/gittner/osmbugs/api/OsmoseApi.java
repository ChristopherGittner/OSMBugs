package org.gittner.osmbugs.api;

import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.common.OsmoseElement;
import org.gittner.osmbugs.parser.OsmoseParser;
import org.gittner.osmbugs.statics.Settings;
import org.osmdroid.util.BoundingBoxE6;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class OsmoseApi implements BugApi<OsmoseBug>
{
    public static OkHttpClient mOkHttpClient = new OkHttpClient();

    public ArrayList<OsmoseBug> downloadBBox(BoundingBoxE6 bBox)
    {
        Request request = new Request.Builder()
                .url(!Settings.isLanguageGerman() ? "http://osmose.openstreetmap.fr/en/api/0.2/errors?" : "http://osmose.openstreetmap.fr/de/api/0.2/errors?")
                .post(new FormBody.Builder()
                        .add("lat", String.valueOf(bBox.getCenter().getLatitudeE6() / 1000000.0))
                        .add("lon", String.valueOf(bBox.getCenter().getLongitudeE6() / 1000000.0))
                        .add("limit", String.valueOf(Settings.Osmose.getBugLimit()))
                        .add("full", "true")
                        .add("comment", Settings.Osmose.getBugsToDisplay() == 1 ? "done" : "false")
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
                .url(!Settings.isLanguageGerman() ? "http://osmose.openstreetmap.fr/en/api/0.2/error/" + id : "http://osmose.openstreetmap.fr/de/api/0.2/error/" + id)
                .post(new FormBody.Builder()
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
