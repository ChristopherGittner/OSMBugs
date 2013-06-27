package org.gittner.osmbugs.bugs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.StringTokenizer;


/* Parser for Keepright bug lists retrieved from points.php */
public class KeeprightParser {

    public static ArrayList<Bug> parse(InputStream stream){
        ArrayList<Bug> bugs = new ArrayList<Bug>();

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(stream));

            String line;

            /* Skip the first line which is just the column names eg. Latitude, Longitude etc...*/
            reader.readLine();

            /* Parse the Stream token by token. Entries are separated by tab.
             * Since it is possible that Entries are empty eg. "" the token itself
             * must be detokenized which leads to one extra call of nextToken() per token
             */
            while((line = reader.readLine()) != null){
                try{
                    StringTokenizer token  = new StringTokenizer(line, "\t", true);

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

                    /* 2 Unused token */
                    for(int i = 0; i != 4; ++i)
                        token.nextToken();

                    /* Way */
                    long way = Long.parseLong(token.nextToken());
                    token.nextToken();

                    /* 2 Unused token */
                    for(int i = 0; i != 4; ++i)
                        token.nextToken();

                    /* Schema */
                    int schema = Integer.parseInt(token.nextToken());
                    token.nextToken();

                    /* Id */
                    int id = Integer.parseInt(token.nextToken());
                    token.nextToken();

                    /* Description */
                    String text = token.nextToken();
                    token.nextToken();

                    ArrayList<Comment> comments = new ArrayList<Comment>();
                    /* Comment or \t if no comment available */
                    String sComment = token.nextToken();
                    if(!sComment.equals("\t")){
                        /* Only skip one Token if the Comment wasn't empty */
                        token.nextToken();
                        comments.add(new Comment(sComment));
                    }

                    /* Current state
                     * Temporarily Ignored == "ignore_t"
                     * Ignored == "ignore"
                     * Open == "new" or "" or maybe everything else
                     */
                    String sState = token.nextToken();
                    Bug.STATE state;

                    /* Translate the bug State Note: "" or "new" both apply to open bugs */
                    if(sState.equals("ignore_t"))
                        state = Bug.STATE.CLOSED;
                    else if(sState.equals("ignore"))
                        state = Bug.STATE.IGNORED;
                    else
                        state = Bug.STATE.OPEN;

                    /* Finally add our Bug to the results */
                    bugs.add(new KeeprightBug(lat, lon, title, text, type, comments, way, schema, id, state));
                }
                catch(NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bugs;
    }
}
