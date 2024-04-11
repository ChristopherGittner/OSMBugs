package org.gittner.osmbugs.keepright

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gittner.osmbugs.statics.OpenStreetMap.TYPE
import org.joda.time.format.DateTimeFormat
import org.osmdroid.util.GeoPoint
import java.util.*


// Parser for Keepright bug lists retrieved from points.php
class KeeprightParser {
    suspend fun parse(data: String): ArrayList<KeeprightError> = withContext(Dispatchers.Default) {
        val formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss")
        val errors = ArrayList<KeeprightError>()

        val lines = data.lineSequence().iterator()

        // Skip the first line which is just the column names eg. Latitude, Longitude etc...
        lines.next()

        // Parse the Stream token by token. Entries are separated by tab. Since it is possible
        // that Entries are empty eg. "" the token itself must be detokenized which leads to one
        // extra call of nextToken() per token
        while (lines.hasNext()) {
            val line = lines.next()

            if (line.isEmpty()) {
                continue
            }

            try {
                val tokens = line.split("\t")
                if (tokens.size < 14) {
                    throw RuntimeException("Not enough tokens in line: $line")
                }

                val lat = tokens[0].toDouble()
                val lon = tokens[1].toDouble()
                val title = tokens[2]
                val type = tokens[3].toInt()

                val objectType: TYPE = when (tokens[4]) {
                    "node" -> TYPE.NODE
                    "way" -> TYPE.WAY
                    "relation" -> TYPE.RELATION
                    "Punkt" -> TYPE.NODE
                    "Linie" -> TYPE.WAY
                    "Relation" -> TYPE.RELATION
                    else -> throw RuntimeException("Invalid Type: ${tokens[4]}")
                }

                val way = tokens[6].toLong()
                val creationDate = formatter.parseDateTime(tokens[7])
                val schema = tokens[9].toLong()
                val id = tokens[10].toLong()
                val text = tokens[11]
                val comment = tokens[12]

                // Current state Temporarily Ignored == "ignore_t" Ignored == "ignore" Open == "new" or ""
                val state: KeeprightError.STATE = when (tokens[13]) {
                    "ignore_t" -> KeeprightError.STATE.IGNORED_TMP
                    "ignore" -> KeeprightError.STATE.IGNORED
                    else -> KeeprightError.STATE.OPEN
                }

                // Finally add our Bug to the results
                val errorType = KeeprightError.GetTypeByTypeNumber(type) ?: throw RuntimeException("Invalid Type: $type")

                errors.add(
                    KeeprightError(
                        GeoPoint(lat, lon),
                        creationDate,
                        id,
                        schema,
                        errorType,
                        objectType,
                        title,
                        text,
                        comment,
                        state,
                        way
                    )
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        errors
    }

}