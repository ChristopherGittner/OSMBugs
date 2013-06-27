package org.gittner.osmbugs.bugs;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class MapdustParser {
	
	public static ArrayList<Bug> parse(InputStream stream, Context context_){
		ArrayList<Bug> bugs = new ArrayList<Bug>();

		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(stream, "iso-8859-1"));

			String line = reader.readLine();

			/* Little Tweak since this was sometimes missing in Answer. Only on Android not on Desktop */
			if(!line.endsWith("\"}}"))
				line += "\"}}";
			
			JSONObject json = new JSONObject(line);
			
			if(!json.getString("status").equals("SUCCESS"))
				return new ArrayList<Bug>();
			
			JSONObject data = json.getJSONObject("data");
			
			JSONArray bugArray = data.getJSONArray("bugs");
			
			for(int i = 0; i != bugArray.length(); ++i) {
				JSONObject bug = bugArray.getJSONObject(i);
				
				long id = bug.getLong("id");
				double lat = bug.getDouble("lat");
				double lon = bug.getDouble("lon");

				Bug.STATE state = Bug.STATE.OPEN;
				if(bug.getString("status").equals("OPEN"))
					state = Bug.STATE.OPEN;
				else if(bug.getString("status").equals("FIXED"))
					state = Bug.STATE.CLOSED;
				else if(bug.getString("status").equals("INVALID"))
					state = Bug.STATE.IGNORED;
				else state = Bug.STATE.OPEN;

				ArrayList<Comment> comments = new ArrayList<Comment>();
				
				String type = bug.getString("type");
				String type_const = bug.getString("type_const");
				String text = bug.getString("text");
				
				int typeInt = MapdustBug.OTHER;
				if(type_const.equals("wrong_turn"))
					typeInt = MapdustBug.WRONGTURN;
				else if(type_const.equals("bad_routing"))
					typeInt = MapdustBug.BADROUTING;
				else if(type_const.equals("oneway_road"))
					typeInt = MapdustBug.ONEWAYROAD;
				else if(type_const.equals("blocked_street"))
					typeInt = MapdustBug.BLOCKEDSTREET;
				else if(type_const.equals("missing_street"))
					typeInt = MapdustBug.MISSINGSTREET;
				else if(type_const.equals("wrong_roundabout"))
					typeInt = MapdustBug.ROUNDABOUTISSUE;
				else if(type_const.equals("missing_speedlimit"))
					typeInt = MapdustBug.MISSINGSPEEDINFO;
				else
					typeInt = MapdustBug.OTHER;

				bugs.add(new MapdustBug(lat, lon, type, text, comments, typeInt, id, state));
			}
			
		} catch (IOException e) {
			e.printStackTrace();
			return new ArrayList<Bug>();
		} catch (JSONException e) {
			e.printStackTrace();
			return new ArrayList<Bug>();
		}
		
		return bugs;
	}
}
