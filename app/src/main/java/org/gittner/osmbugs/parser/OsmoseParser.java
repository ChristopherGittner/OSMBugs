package org.gittner.osmbugs.parser;

import org.gittner.osmbugs.Helpers.Openstreetmap;
import org.gittner.osmbugs.bugs.OsmoseBug;
import org.gittner.osmbugs.common.OsmKeyValuePair;
import org.gittner.osmbugs.common.OsmoseElement;
import org.gittner.osmbugs.common.OsmoseFix;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class OsmoseParser
{
    public static ArrayList<OsmoseBug> parseBugList(InputStream stream)
    {
        ArrayList<OsmoseBug> bugs = new ArrayList<>();

        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new InputStreamReader(stream, "iso-8859-1"));

            String line = reader.readLine();

            JSONObject json = new JSONObject(line);

            JSONArray bugArray = json.getJSONArray("errors");
            for (int i = 0; i != bugArray.length(); ++i)
            {
                JSONArray bug = bugArray.getJSONArray(i);
                double lat = Double.valueOf(bug.getString(0));
                double lon = Double.valueOf(bug.getString(1));
                long id = Long.valueOf(bug.getString(2));
                int item = Integer.valueOf(bug.getString(3));
                String title = bug.getString(9);

                bugs.add(new OsmoseBug(lat, lon, id, item, title));
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return bugs;
    }


    public static ArrayList<OsmoseElement> parseBugElements(InputStream stream)
    {
        ArrayList<OsmoseElement> elements = new ArrayList<>();

        BufferedReader reader;
        try
        {
            reader = new BufferedReader(new InputStreamReader(stream, "iso-8859-1"));

            String line = reader.readLine();

            JSONObject json = new JSONObject(line);

            JSONArray elems = json.getJSONArray("elems");
            for (int iElems = 0; iElems != elems.length(); ++iElems)
            {
                JSONObject elem = elems.getJSONObject(iElems);

                OsmoseElement element = new OsmoseElement();

                String type = elem.getString("type");
                switch (type)
                {
                    case "node":
                        element.setType(Openstreetmap.TYPE_NODE);
                        break;
                    case "way":
                        element.setType(Openstreetmap.TYPE_WAY);
                        break;
                    default:
                        element.setType(Openstreetmap.TYPE_RELATION);
                        break;
                }

                element.setId(elem.getLong("id"));
                JSONArray tags = elem.getJSONArray("tags");
                for (int iTags = 0; iTags != tags.length(); ++iTags)
                {
                    OsmKeyValuePair tag = new OsmKeyValuePair();
                    tag.setKey(tags.getJSONObject(iTags).getString("k"));
                    tag.setValue(tags.getJSONObject(iTags).getString("v"));
                    element.getTags().add(tag);
                }

                JSONArray fixes = elem.getJSONArray("fixes");
                for (int iFixes = 0; iFixes != fixes.length(); ++iFixes)
                {
                    OsmoseFix fix = new OsmoseFix();

                    JSONArray adds = fixes.getJSONObject(iFixes).getJSONArray("add");
                    for (int iAdds = 0; iAdds != adds.length(); ++iAdds)
                    {
                        OsmKeyValuePair tag = new OsmKeyValuePair();

                        tag.setKey(adds.getJSONObject(iAdds).getString("k"));
                        if (adds.getJSONObject(iAdds).has("v"))
                        {
                            tag.setValue(adds.getJSONObject(iAdds).getString("v"));
                        }

                        fix.getAdd().add(tag);
                    }

                    JSONArray modifies = fixes.getJSONObject(iFixes).getJSONArray("mod");
                    for (int iModifies = 0; iModifies != modifies.length(); ++iModifies)
                    {
                        OsmKeyValuePair tag = new OsmKeyValuePair();

                        tag.setKey(modifies.getJSONObject(iModifies).getString("k"));
                        if (modifies.getJSONObject(iModifies).has("v"))
                        {
                            tag.setValue(modifies.getJSONObject(iModifies).getString("v"));
                        }

                        fix.getModify().add(tag);
                    }
                    JSONArray dels = fixes.getJSONObject(iFixes).getJSONArray("del");
                    for (int iDels = 0; iDels != dels.length(); ++iDels)
                    {
                        OsmKeyValuePair tag = new OsmKeyValuePair();

                        tag.setKey(dels.getJSONObject(iDels).getString("k"));
                        if (dels.getJSONObject(iDels).has("v"))
                        {
                            tag.setValue(dels.getJSONObject(iDels).getString("v"));
                        }

                        fix.getDelete().add(tag);
                    }

                    element.getFixes().add(fix);
                }

                elements.add(element);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return new ArrayList<>();
        }

        return elements;
    }
}
