package org.gittner.osmbugs.bugs;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.statics.Drawings;
import org.osmdroid.util.GeoPoint;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class KeeprightBug extends Bug {
	private int type_;
	private int schema_;
	private int id_;
	private long way_;
	
	public KeeprightBug(double lat, double lon, String title, String text, int type, ArrayList<Comment> comments, long way, int shema, int id, Bug.STATE state) {
		super(title + " <a href=http://www.openstreetmap.org/browse/way/" + way + ">" + way + "</a>", text, comments, new GeoPoint(lat, lon));
		setType(type);
		setSchema(shema);
		setId(id);
		setState(state);
	}
	
	public KeeprightBug(Parcel parcel) {
		super(parcel);
		type_ = parcel.readInt();
		schema_ = parcel.readInt();
		id_ = parcel.readInt();
		way_ = parcel.readLong();
	}

	/* Commit the Bug to the Keepright Server */
	@Override
	public boolean commit() {
		HttpClient client = new DefaultHttpClient();
		
		ArrayList<NameValuePair> arguments = new ArrayList<NameValuePair>();
		if(comments_.size() > 0)
			arguments.add(new BasicNameValuePair("co", comments_.get(0).getText()));
		else
			arguments.add(new BasicNameValuePair("co", ""));
		
		arguments.add(new BasicNameValuePair("st", getUrlState()));
		arguments.add(new BasicNameValuePair("schema", String.valueOf(getSchema())));
		arguments.add(new BasicNameValuePair("id", String.valueOf(getId())));

		HttpGet request = new HttpGet("http://keepright.at/comment.php?" + URLEncodedUtils.format(arguments, "utf-8"));
		
		try {		
			/* Execute commit */
			HttpResponse response = client.execute(request);
			
			/* Check result for Success*/
			if(response.getStatusLine().getStatusCode() != 200)
				return false;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
		
	/* Keepright Bugs can be commented */
	@Override
	public boolean isCommentable() {
		return true;
	}
	
	/* Keepright Bugs can be ignored aka. false positive*/
	@Override
	public boolean isIgnorable() {
		return true;
	}
	
	/* Keepright Bugs can be reopened */
	@Override
	public boolean isReopenable() {
		return true;
	}
	
	/* Return a readable usable String for the Server from the current State */
	public String getUrlState() {
		if(getState() == Bug.STATE.OPEN)
			return "";
		else if(getState() == Bug.STATE.CLOSED)
			return "ignore_t";
		else if(getState() == Bug.STATE.IGNORED)
			return "ignore";
		else
			return "";
	}

	/* Get the Bugs Type */
	public int getType() {
		return type_;
	}
	
	/* Set the Bugs Type */
	public void setType(int type) {
		type_ = type;
	}
	
	/* Get the Bugs Schema */
	public int getSchema() {
		return schema_;
	}
	
	/* Set the Bugs Schema */
	public void setSchema(int schema) {
		schema_ = schema;
	}
	
	/* Get the Bugs Id */
	public int getId() {
		return id_;
	}
	
	/* Set the Bugs Id */
	public void setId(int id) {
		id_ = id;
	}
	
	@Override
	public Drawable getMarker(int bitset) {
		if(getState() == Bug.STATE.CLOSED)
			return Drawings.KeeprightDrawableClosed;
		else if(getState() == Bug.STATE.IGNORED)
			return Drawings.KeeprightDrawableIgnored;
		else{
			switch(type_) {
			case 20:
				return Drawings.KeeprightDrawable20;
			case 30:
				return Drawings.KeeprightDrawable30;
			case 40:
				return Drawings.KeeprightDrawable40;
			case 50:
				return Drawings.KeeprightDrawable50;
			case 60:
				return Drawings.KeeprightDrawable60;
			case 70:
				return Drawings.KeeprightDrawable70;
			case 90:
				return Drawings.KeeprightDrawable90;
			case 91:
				return Drawings.KeeprightDrawable91;
			case 92:
				return Drawings.KeeprightDrawable92;
			case 100:
				return Drawings.KeeprightDrawable100;
			case 110:
				return Drawings.KeeprightDrawable110;
			case 120:
				return Drawings.KeeprightDrawable120;
			case 121:
				return Drawings.KeeprightDrawable121;
			case 130:
				return Drawings.KeeprightDrawable130;
			case 140:
				return Drawings.KeeprightDrawable140;
			case 150:
				return Drawings.KeeprightDrawable150;
			case 160:
				return Drawings.KeeprightDrawable160;
			case 170:
				return Drawings.KeeprightDrawable170;
			case 180:
				return Drawings.KeeprightDrawable180;
			case 190:
				return Drawings.KeeprightDrawable190;
			case 191:
				return Drawings.KeeprightDrawable191;
			case 192:
				return Drawings.KeeprightDrawable192;
			case 193:
				return Drawings.KeeprightDrawable193;
			case 194:
				return Drawings.KeeprightDrawable194;
			case 195:
				return Drawings.KeeprightDrawable195;
			case 196:
				return Drawings.KeeprightDrawable196;
			case 197:
				return Drawings.KeeprightDrawable197;
			case 198:
				return Drawings.KeeprightDrawable198;
			case 200:
				return Drawings.KeeprightDrawable200;
			case 201:
				return Drawings.KeeprightDrawable201;
			case 202:
				return Drawings.KeeprightDrawable202;
			case 203:
				return Drawings.KeeprightDrawable203;
			case 204:
				return Drawings.KeeprightDrawable204;
			case 205:
				return Drawings.KeeprightDrawable205;
			case 206:
				return Drawings.KeeprightDrawable206;
			case 207:
				return Drawings.KeeprightDrawable207;
			case 208:
				return Drawings.KeeprightDrawable208;
			case 210:
				return Drawings.KeeprightDrawable210;
			case 211:
				return Drawings.KeeprightDrawable211;
			case 212:
				return Drawings.KeeprightDrawable212;
			case 220:
				return Drawings.KeeprightDrawable220;
			case 221:
				return Drawings.KeeprightDrawable221;
			case 230:
				return Drawings.KeeprightDrawable230;
			case 231:
				return Drawings.KeeprightDrawable231;
			case 232:
				return Drawings.KeeprightDrawable232;
			case 240:
				return Drawings.KeeprightDrawable240;
			case 250:
				return Drawings.KeeprightDrawable250;
			case 260:
				return Drawings.KeeprightDrawable260;
			case 270:
				return Drawings.KeeprightDrawable270;
			case 280:
				return Drawings.KeeprightDrawable280;
			case 281:
				return Drawings.KeeprightDrawable281;
			case 282:
				return Drawings.KeeprightDrawable282;
			case 283:
				return Drawings.KeeprightDrawable283;
			case 284:
				return Drawings.KeeprightDrawable284;
			case 285:
				return Drawings.KeeprightDrawable285;
			case 290:
				return Drawings.KeeprightDrawable290;
			case 291:
				return Drawings.KeeprightDrawable291;
			case 292:
				return Drawings.KeeprightDrawable292;
			case 293:
				return Drawings.KeeprightDrawable293;
			case 300:
				return Drawings.KeeprightDrawable300;
			case 310:
				return Drawings.KeeprightDrawable310;
			case 311:
				return Drawings.KeeprightDrawable311;
			case 312:
				return Drawings.KeeprightDrawable312;
			case 313:
				return Drawings.KeeprightDrawable313;
			case 320:
				return Drawings.KeeprightDrawable320;
			case 350:
				return Drawings.KeeprightDrawable350;
			case 360:
				return Drawings.KeeprightDrawable360;
			case 380:
				return Drawings.KeeprightDrawable380;
			case 390:
				return Drawings.KeeprightDrawable390;
			case 400:
				return Drawings.KeeprightDrawable400;
			case 401:
				return Drawings.KeeprightDrawable401;
			case 402:
				return Drawings.KeeprightDrawable402;
			case 410:
				return Drawings.KeeprightDrawable410;
			case 411:
				return Drawings.KeeprightDrawable411;
			case 412:
				return Drawings.KeeprightDrawable412;
			case 413:
				return Drawings.KeeprightDrawable413;
			default:
				return Drawings.KeeprightDrawableDefault;			
			}
		}
	}

	/* Creates a New Bug on the Server */
	@Override
	public boolean addNew() {
		return false;
	}
	
	/* Parcelable interface */
	@Override
	public void writeToParcel(Parcel parcel, int flags) {
		super.writeToParcel(parcel, flags);
		
		parcel.writeInt(type_);
		parcel.writeInt(schema_);
		parcel.writeInt(id_);
		parcel.writeLong(way_);
	}

	@Override
	public int describeContents() {
		return 0;
	}
	
    public static final Creator<KeeprightBug> CREATOR = new Parcelable.Creator<KeeprightBug>() {

		@Override
		public KeeprightBug createFromParcel(Parcel source) {
			return new KeeprightBug(source);
		}

		@Override
		public KeeprightBug[] newArray(int size) {
			return new KeeprightBug[size];
		}    	
    };

	@Override
	public ArrayList<String> getStateNames(Context context) {
		ArrayList<String> states = new ArrayList<String>();
		states.add(context.getString(R.string.open));
		states.add(context.getString(R.string.closed));
		states.add(context.getString(R.string.ignored));
		
		return states;
	}
	
	@Override
	public String getEditCommentText(){
		if(comments_.size() > 0)
			return comments_.get(0).getText();
		
		return ""; 
	}
	
	@Override
	public void addComment(String text){
		comments_.clear();
		comments_.add(new Comment(text));
	}
}
