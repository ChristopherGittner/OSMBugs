package org.gittner.osmbugs.parser;

import org.gittner.osmbugs.bugs.OsmNote;
import org.gittner.osmbugs.common.Comment;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/* Parser for Openstreetmap Notes bug lists retrieved from notes */
public class OpenstreetmapNotesParser
{
    public static ArrayList<OsmNote> parse(InputStream stream)
    {
        ArrayList<OsmNote> bugs = new ArrayList<>();
        try
        {
            Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("note");
            for (int i = 0; i != nList.getLength(); ++i)
            {
                Element wpt = (Element) nList.item(i);
                double lat = Double.parseDouble(wpt.getAttribute("lat"));
                double lon = Double.parseDouble(wpt.getAttribute("lon"));

                OsmNote.STATE state = OsmNote.STATE.CLOSED;
                if (wpt.getElementsByTagName("status").item(0).getTextContent().equals("open"))
                {
                    state = OsmNote.STATE.OPEN;
                }

                long id = Long.parseLong(wpt.getElementsByTagName("id").item(0).getTextContent());

                NodeList nListComments = wpt.getElementsByTagName("comment");
                ArrayList<Comment> comments = new ArrayList<>();
                for (int n = 0; n != nListComments.getLength(); ++n)
                {
                    Comment comment = new Comment();
                    comment.setText(((Element) nListComments.item(n)).getElementsByTagName("text").item(0).getTextContent());

                    NodeList element = ((Element) nListComments.item(n)).getElementsByTagName("user");
                    if (element.getLength() != 0)
                    {
                        comment.setUsername(element.item(0).getTextContent());
                    }

                    comments.add(comment);
                }

                String text = "";
                if (comments.size() > 0)
                {
                    text = comments.get(0).getText();
                    comments.remove(0);
                }

                bugs.add(new OsmNote(lat, lon, id, text, comments, state));
            }
        }
        catch (ParserConfigurationException | IOException | SAXException e)
        {
            e.printStackTrace();
        }
        return bugs;
    }
}
