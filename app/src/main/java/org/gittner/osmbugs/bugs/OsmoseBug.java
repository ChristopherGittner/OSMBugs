package org.gittner.osmbugs.bugs;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Parcel;

import org.gittner.osmbugs.App;
import org.gittner.osmbugs.R;
import org.gittner.osmbugs.common.Comment;
import org.gittner.osmbugs.statics.Drawings;
import org.osmdroid.util.GeoPoint;

import java.util.ArrayList;

public class OsmoseBug extends Bug {

    public OsmoseBug(
            double lat,
            double lon,
            long id,
            int item,
            String title) {

        super(App.getContext().getString(
                App.getContext().getResources().getIdentifier(
                        "osmose_item_" + item,
                        "string",
                        App.getContext().getPackageName())),
                "",
                new ArrayList<Comment>(), new GeoPoint(lat, lon));

        setItem(item);
        setId(id);
    }

    public OsmoseBug(Parcel parcel) {
        super(parcel);

        mItem = parcel.readInt();
        mId = parcel.readInt();
    }

    @Override
    public ArrayList<String> getSStates() {
        ArrayList<String> states = new ArrayList<>();

        return states;
    }

    @Override
    public boolean isCommitable(String newSState, String newComment) { return false; }

    @Override
    public boolean commit(String newSState, String newComment) {

        return false;
    }

    /* Osmose Bugs can not be commented */
    @Override
    public boolean isCommentable() {
        return false;
    }

    /* Get the Bugs Item */
    public int getItem() {
        return mItem;
    }

    /* Set the Bugs Item */
    public void setItem(int schema) {
        mItem = schema;
    }

    /* Get the Bugs Id */
    public long getId() {
        return mId;
    }

    /* Set the Bugs Id */
    public void setId(long id) {
        mId = id;
    }
    
    @Override
    public Drawable getMarker(int bitset) {
        switch (mItem)
        {
            case 0: return Drawings.OsmoseMarkerB0;
            case 1010: return Drawings.OsmoseMarkerB1010;
            case 1004: return Drawings.OsmoseMarkerB1040;
            case 1050: return Drawings.OsmoseMarkerB1050;
            case 1060: return Drawings.OsmoseMarkerB1060;
            case 1070: return Drawings.OsmoseMarkerB1070;
            case 1080: return Drawings.OsmoseMarkerB1080;
            case 1090: return Drawings.OsmoseMarkerB1090;
            case 1100: return Drawings.OsmoseMarkerB1100;
            case 1110: return Drawings.OsmoseMarkerB1110;
            case 1120: return Drawings.OsmoseMarkerB1120;
            case 1140: return Drawings.OsmoseMarkerB1140;
            case 1150: return Drawings.OsmoseMarkerB1150;
            case 1160: return Drawings.OsmoseMarkerB1160;
            case 1170: return Drawings.OsmoseMarkerB1170;
            case 1180: return Drawings.OsmoseMarkerB1180;
            case 1190: return Drawings.OsmoseMarkerB1190;
            case 1200: return Drawings.OsmoseMarkerB1200;
            case 1210: return Drawings.OsmoseMarkerB1210;
            case 1220: return Drawings.OsmoseMarkerB1220;
            case 1230: return Drawings.OsmoseMarkerB1230;
            case 1240: return Drawings.OsmoseMarkerB1240;
            case 2010: return Drawings.OsmoseMarkerB2010;
            case 2020: return Drawings.OsmoseMarkerB2020;
            case 2030: return Drawings.OsmoseMarkerB2030;
            case 2040: return Drawings.OsmoseMarkerB2040;
            case 2060: return Drawings.OsmoseMarkerB2060;
            case 2080: return Drawings.OsmoseMarkerB2080;
            case 2090: return Drawings.OsmoseMarkerB2090;
            case 2100: return Drawings.OsmoseMarkerB2100;
            case 2110: return Drawings.OsmoseMarkerB2110;
            case 3010: return Drawings.OsmoseMarkerB3010;
            case 3020: return Drawings.OsmoseMarkerB3020;
            case 3030: return Drawings.OsmoseMarkerB3030;
            case 3031: return Drawings.OsmoseMarkerB3031;
            case 3032: return Drawings.OsmoseMarkerB3032;
            case 3033: return Drawings.OsmoseMarkerB3033;
            case 3040: return Drawings.OsmoseMarkerB3040;
            case 3050: return Drawings.OsmoseMarkerB3050;
            case 3060: return Drawings.OsmoseMarkerB3060;
            case 3070: return Drawings.OsmoseMarkerB3070;
            case 3080: return Drawings.OsmoseMarkerB3080;
            case 3090: return Drawings.OsmoseMarkerB3090;
            case 3091: return Drawings.OsmoseMarkerB3091;
            case 3100: return Drawings.OsmoseMarkerB3100;
            case 3110: return Drawings.OsmoseMarkerB3110;
            case 3120: return Drawings.OsmoseMarkerB3120;
            case 3150: return Drawings.OsmoseMarkerB3150;
            case 3160: return Drawings.OsmoseMarkerB3160;
            case 3161: return Drawings.OsmoseMarkerB3161;
            case 3170: return Drawings.OsmoseMarkerB3170;
            case 3180: return Drawings.OsmoseMarkerB3180;
            case 3190: return Drawings.OsmoseMarkerB3190;
            case 3200: return Drawings.OsmoseMarkerB3200;
            case 3210: return Drawings.OsmoseMarkerB3210;
            case 4010: return Drawings.OsmoseMarkerB4010;
            case 4020: return Drawings.OsmoseMarkerB4020;
            case 4030: return Drawings.OsmoseMarkerB4030;
            case 4040: return Drawings.OsmoseMarkerB4040;
            case 4060: return Drawings.OsmoseMarkerB4060;
            case 4070: return Drawings.OsmoseMarkerB4070;
            case 4080: return Drawings.OsmoseMarkerB4090;
            case 4090: return Drawings.OsmoseMarkerB4090;
            case 4100: return Drawings.OsmoseMarkerB4100;
            case 4110: return Drawings.OsmoseMarkerB4110;
            case 5010: return Drawings.OsmoseMarkerB5010;
            case 5020: return Drawings.OsmoseMarkerB5020;
            case 5030: return Drawings.OsmoseMarkerB5030;
            case 5040: return Drawings.OsmoseMarkerB5040;
            case 5050: return Drawings.OsmoseMarkerB5050;
            case 6010: return Drawings.OsmoseMarkerB6010;
            case 6020: return Drawings.OsmoseMarkerB6020;
            case 6030: return Drawings.OsmoseMarkerB6030;
            case 6040: return Drawings.OsmoseMarkerB6040;
            case 6060: return Drawings.OsmoseMarkerB6060;
            case 6070: return Drawings.OsmoseMarkerB6070;
            case 7010: return Drawings.OsmoseMarkerB7010;
            case 7011: return Drawings.OsmoseMarkerB7011;
            case 7012: return Drawings.OsmoseMarkerB7012;
            case 7040: return Drawings.OsmoseMarkerB7040;
            case 7050: return Drawings.OsmoseMarkerB7050;
            case 7060: return Drawings.OsmoseMarkerB7060;
            case 7070: return Drawings.OsmoseMarkerB7070;
            case 7080: return Drawings.OsmoseMarkerB7080;
            case 7090: return Drawings.OsmoseMarkerB7090;
            case 7100: return Drawings.OsmoseMarkerB7100;
            case 7110: return Drawings.OsmoseMarkerB7110;
            case 7120: return Drawings.OsmoseMarkerB7120;
            case 7130: return Drawings.OsmoseMarkerB7130;
            case 7140: return Drawings.OsmoseMarkerB7140;
            case 7150: return Drawings.OsmoseMarkerB7150;
            case 7160: return Drawings.OsmoseMarkerB7160;
            case 8010: return Drawings.OsmoseMarkerB8010;
            case 8011: return Drawings.OsmoseMarkerB8011;
            case 8020: return Drawings.OsmoseMarkerB8020;
            case 8021: return Drawings.OsmoseMarkerB8021;
            case 8030: return Drawings.OsmoseMarkerB8030;
            case 8031: return Drawings.OsmoseMarkerB8031;
            case 8040: return Drawings.OsmoseMarkerB8040;
            case 8041: return Drawings.OsmoseMarkerB8041;
            case 8050: return Drawings.OsmoseMarkerB8050;
            case 8051: return Drawings.OsmoseMarkerB8051;
            case 8060: return Drawings.OsmoseMarkerB8060;
            case 8070: return Drawings.OsmoseMarkerB8070;
            case 8080: return Drawings.OsmoseMarkerB8080;
            case 8101: return Drawings.OsmoseMarkerB8101;
            case 8110: return Drawings.OsmoseMarkerB8110;
            case 8120: return Drawings.OsmoseMarkerB8120;
            case 8121: return Drawings.OsmoseMarkerB8121;
            case 8130: return Drawings.OsmoseMarkerB8130;
            case 8131: return Drawings.OsmoseMarkerB8131;
            case 8140: return Drawings.OsmoseMarkerB8140;
            case 8150: return Drawings.OsmoseMarkerB8150;
            case 8160: return Drawings.OsmoseMarkerB8160;
            case 8161: return Drawings.OsmoseMarkerB8161;
            case 8170: return Drawings.OsmoseMarkerB8170;
            case 8180: return Drawings.OsmoseMarkerB8180;
            case 8190: return Drawings.OsmoseMarkerB8190;
            case 8210: return Drawings.OsmoseMarkerB8210;
            case 8211: return Drawings.OsmoseMarkerB8211;
            case 8221: return Drawings.OsmoseMarkerB8221;
            default: return Drawings.OsmoseMarkerB0;
        }
    }

    /* Parcelable interface */
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        super.writeToParcel(parcel, flags);

        parcel.writeInt(mItem);
        parcel.writeLong(mId);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<OsmoseBug> CREATOR = new Creator<OsmoseBug>() {

        @Override
        public OsmoseBug createFromParcel(Parcel source) {
            return new OsmoseBug(source);
        }

        @Override
        public OsmoseBug[] newArray(int size) {
            return new OsmoseBug[size];
        }
    };

    /* Holds the Keepright Schema of this Bug */
    private int mItem;

    /* Holds the Keepright ID of this Bug */
    private long mId;
}
