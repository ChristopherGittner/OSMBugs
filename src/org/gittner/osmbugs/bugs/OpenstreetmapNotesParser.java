package org.gittner.osmbugs.bugs;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/* Parser for Openstreetmap Notes bug lists retrieved from notes */
public class OpenstreetmapNotesParser {
	
	public static ArrayList<Bug> parse(InputStream stream){
		ArrayList<Bug> bugs = new ArrayList<Bug>();
				
		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
			
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("note");
			
			for(int i = 0; i != nList.getLength(); ++i) {
				Element wpt = (Element) nList.item(i);
				
				double lat = Double.parseDouble(wpt.getAttribute("lat"));
				double lon = Double.parseDouble(wpt.getAttribute("lon"));
								
				Bug.STATE state = Bug.STATE.CLOSED;
				if(wpt.getElementsByTagName("status").item(0).getTextContent().equals("open"))
					state = Bug.STATE.OPEN;

				long id = Long.parseLong(wpt.getElementsByTagName("id").item(0).getTextContent());
				
				NodeList nListComments = wpt.getElementsByTagName("comment");
				ArrayList<Comment> comments = new ArrayList<Comment>();
				for(int n = 0; n != nListComments.getLength(); ++n){
					comments.add(new Comment (((Element)nListComments.item(n)).getElementsByTagName("text").item(0).getTextContent()));
				}
				
				String text = "";
				if(comments.size() > 0){
					text = comments.get(0).getText();
					comments.remove(0);
				}

				bugs.add(new OpenstreetmapNote(lat, lon, text, comments, id, state));
			}

		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
		e.printStackTrace();
		}
			
		return bugs;
	}
}
