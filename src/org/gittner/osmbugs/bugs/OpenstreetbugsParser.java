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

/* Parser for Openstreetbugs bug lists retrieved from getGPX.php */
public class OpenstreetbugsParser {
	
	public static ArrayList<Bug> parse(InputStream stream){
		ArrayList<Bug> bugs = new ArrayList<Bug>();

		try {
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(stream);
			
			doc.getDocumentElement().normalize();
			
			NodeList nList = doc.getElementsByTagName("wpt");
			
			for(int i = 0; i != nList.getLength(); ++i) {
				Element wpt = (Element) nList.item(i);
				Element extensions = (Element) wpt.getElementsByTagName("extensions").item(0);
				
				double lat = Double.parseDouble(wpt.getAttribute("lat"));
				double lon = Double.parseDouble(wpt.getAttribute("lon"));
				
				ArrayList<Comment> comments = new ArrayList<Comment>();
				String text = wpt.getElementsByTagName("desc").item(0).getTextContent();
				
				int start = 0;
				int end = 0;
								
				while((end = text.indexOf("<hr />", start)) != -1){
					comments.add(new Comment(text.substring(start, end)));
					start = end + 6;
				}
				comments.add(new Comment(text.substring(start, text.length() - 1)));
				
				text = comments.get(0).getText();
				comments.remove(0);
				
				
				Bug.STATE state = Bug.STATE.OPEN;
				if(extensions.getElementsByTagName("closed").item(0).getTextContent().equals("1"))
					state = Bug.STATE.CLOSED;

				long id = Long.parseLong(extensions.getElementsByTagName("id").item(0).getTextContent());

				bugs.add(new OpenstreetbugsBug(lat, lon, text, comments, id, state));
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
