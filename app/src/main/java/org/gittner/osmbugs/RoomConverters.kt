package org.gittner.osmbugs

import androidx.room.TypeConverter
import org.gittner.osmbugs.keepright.KeeprightError
import org.gittner.osmbugs.osmnotes.OsmNote
import org.gittner.osmbugs.osmose.OsmoseError
import org.gittner.osmbugs.statics.OpenStreetMap
import org.joda.time.DateTime
import org.json.JSONArray
import org.osmdroid.api.IGeoPoint
import org.osmdroid.util.GeoPoint

class RoomConverters {
    @TypeConverter
    fun toGeoPoint(data: String): IGeoPoint {
        val values = data.split("|")
        return GeoPoint(values[0].toDouble(), values[1].toDouble())
    }

    @TypeConverter
    fun fromGeoPoint(data: IGeoPoint): String {
        return "${data.latitude}|${data.longitude}"
    }

    @TypeConverter
    fun toDateTime(data: String): DateTime {
        return DateTime.parse(data)
    }

    @TypeConverter
    fun fromDateTime(date: DateTime): String {
        return date.toString()
    }

    @TypeConverter
    fun toOsmNoteState(data: String): OsmNote.STATE {
        return OsmNote.STATE.valueOf(data)
    }

    @TypeConverter
    fun fromOsmNoteState(data: OsmNote.STATE): String {
        return data.toString()
    }

    @TypeConverter
    fun toOsmNoteComment(data: String): ArrayList<OsmNote.OsmNoteComment> {
        val ret = ArrayList<OsmNote.OsmNoteComment>()

        val json = JSONArray(data)
        for (i in 0 until json.length()) {
            val objData = json.getJSONArray(i)
            ret.add(
                OsmNote.OsmNoteComment(
                    objData.getString(0),
                    DateTime.parse(objData.getString(1)),
                    objData.getString(2)
                )
            )
        }

        return ret
    }

    @TypeConverter
    fun fromOsmNoteComment(data: ArrayList<OsmNote.OsmNoteComment>): String {
        val ret = JSONArray()

        data.forEach {
            val objData = JSONArray()

            objData.put(it.Comment)
            objData.put(it.Date.toString())
            objData.put(it.User)

            ret.put(objData)
        }

        return ret.toString()
    }

    @TypeConverter
    fun toKeeprightErrorState(data: String): KeeprightError.STATE {
        return KeeprightError.STATE.valueOf(data)
    }

    @TypeConverter
    fun fromKeeprightErrorState(data: KeeprightError.STATE): String {
        return data.toString()
    }

    @TypeConverter
    fun toKeeprightErrorType(data: String): KeeprightError.ERROR_TYPE {
        return KeeprightError.ERROR_TYPE.valueOf(data)
    }

    @TypeConverter
    fun fromKeeprightErrorType(data: KeeprightError.ERROR_TYPE): String {
        return data.toString()
    }

    @TypeConverter
    fun toOsmoseErrorType(data: String): OsmoseError.ERROR_TYPE {
        return OsmoseError.ERROR_TYPE.valueOf(data)
    }

    @TypeConverter
    fun fromOsmoseErrorType(data: OsmoseError.ERROR_TYPE): String {
        return data.toString()
    }

    @TypeConverter
    fun toOsmType(data: String): OpenStreetMap.TYPE {
        return OpenStreetMap.TYPE.valueOf(data)
    }

    @TypeConverter
    fun fromOsmType(data: OpenStreetMap.TYPE): String {
        return data.toString()
    }

    @TypeConverter
    fun toOsmObjects(data: String): ArrayList<OpenStreetMap.Object> {
        val ret = ArrayList<OpenStreetMap.Object>()

        val json = JSONArray(data)
        for (i in 0 until json.length()) {
            val objData = json.getJSONArray(i)
            ret.add(
                OpenStreetMap.Object(
                    objData.getLong(0),
                    OpenStreetMap.TYPE.valueOf(objData.getString(1))
                )
            )
        }

        return ret
    }

    @TypeConverter
    fun fromOsmObjects(data: ArrayList<OpenStreetMap.Object>): String {
        val ret = JSONArray()

        data.forEach {
            val objData = JSONArray()

            objData.put(it.Id)
            objData.put(it.ObjectType.toString())

            ret.put(objData)
        }

        return ret.toString()
    }
}