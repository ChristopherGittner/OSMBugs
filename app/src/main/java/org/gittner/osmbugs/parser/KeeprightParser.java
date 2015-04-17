package org.gittner.osmbugs.parser;

import org.gittner.osmbugs.Helpers.Openstreetmap;
import org.gittner.osmbugs.bugs.KeeprightBug;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;

/* Parser for Keepright bug lists retrieved from points.php */
public class KeeprightParser
{
    public static ArrayList<KeeprightBug> parse(InputStream stream)
    {
        ArrayList<KeeprightBug> bugs = new ArrayList<>();
        try
        {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            String line;

            /* Skip the first line which is just the column names eg. Latitude, Longitude etc... */
            reader.readLine();

            /*
             * Parse the Stream token by token. Entries are separated by tab. Since it is possible
             * that Entries are empty eg. "" the token itself must be detokenized which leads to one
             * extra call of nextToken() per token
             */
            while ((line = reader.readLine()) != null)
            {
                try
                {
                    StringTokenizer token = new StringTokenizer(line, "\t", true);

                    /* Latitude */
                    double lat = Double.parseDouble(token.nextToken());
                    token.nextToken();

                    /* Longitude */
                    double lon = Double.parseDouble(token.nextToken());
                    token.nextToken();

                    /* Title */
                    String title = token.nextToken();
                    token.nextToken();

                    /* Bug Type */
                    int type = Integer.parseInt(token.nextToken());
                    token.nextToken();

                    /* Object Type */
                    int object_type;
                    switch (token.nextToken())
                    {
                        case "Node":
                            object_type = Openstreetmap.TYPE_NODE;
                            break;
                        case "Way":
                            object_type = Openstreetmap.TYPE_WAY;
                            break;
                        default:
                            object_type = Openstreetmap.TYPE_RELATION;
                            break;
                    }
                    token.nextToken();

					/* Object Type EN */
                    token.nextToken();
                    token.nextToken();

                    /* Way */
                    long way = Long.parseLong(token.nextToken());
                    token.nextToken();

                    /* 2 Unused token */
                    for (int i = 0; i != 4; ++i)
                    {
                        token.nextToken();
                    }

                    /* Schema */
                    int schema = Integer.parseInt(token.nextToken());
                    token.nextToken();

                    /* Id */
                    int id = Integer.parseInt(token.nextToken());
                    token.nextToken();

                    /* Description */
                    String text = token.nextToken();
                    token.nextToken();

                    /* Comment or \t if no comment available */
                    String comment = token.nextToken();
                    if (!comment.equals("\t"))
                    {
                        token.nextToken();
                    }
                    else
                    {
                        comment = "";
                    }

                    /*
                     * Current state Temporarily Ignored == "ignore_t" Ignored == "ignore" Open ==
                     * "new" or "" or maybe everything else
                     */
                    String sState = token.nextToken();
                    KeeprightBug.STATE state;

                    /* Translate the bug State Note: "" or "new" both apply to open bugs */
                    switch (sState)
                    {
                        case "ignore_t":
                            state = KeeprightBug.STATE.IGNORED_TMP;
                            break;
                        case "ignore":
                            state = KeeprightBug.STATE.IGNORED;
                            break;
                        default:
                            state = KeeprightBug.STATE.OPEN;
                            break;
                    }

                    /* Finally add our Bug to the results */
                    bugs.add(new KeeprightBug(lat, lon, id, object_type, schema, type, state, title, text, comment, way));
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return bugs;
    }
}
