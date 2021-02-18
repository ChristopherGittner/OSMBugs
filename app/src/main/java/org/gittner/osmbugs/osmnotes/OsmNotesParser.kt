package org.gittner.osmbugs.osmnotes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.gittner.osmbugs.osmnotes.OsmNote.OsmNoteComment
import org.joda.time.format.DateTimeFormat
import org.osmdroid.util.GeoPoint
import org.w3c.dom.Element
import org.w3c.dom.NodeList
import org.xml.sax.InputSource
import java.io.StringReader
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory


// Parser for Keepright bug lists retrieved from points.php
class OsmNotesParser {
    suspend fun parse(data: String): ArrayList<OsmNote> = withContext(Dispatchers.Default) {
        val bugs: ArrayList<OsmNote> = ArrayList()

        val doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(InputSource(StringReader(data)))
        doc.documentElement.normalize()

        val formatter = DateTimeFormat.forPattern("YYYY-MM-dd HH:mm:ss zzz")

        val nList = doc.getElementsByTagName("note")
        for (i in 0 until nList.length) {
            val note = nList.item(i) as Element

            val lat = note.getAttribute("lat").toDouble()
            val lon = note.getAttribute("lon").toDouble()

            val state = if (note.getElementsByTagName("status").item(0).textContent == "open") OsmNote.STATE.OPEN else OsmNote.STATE.CLOSED

            val id = note.getElementsByTagName("id").item(0).textContent.toLong()
            val nListComments = note.getElementsByTagName("comment")

            val date = formatter.parseDateTime(note.getElementsByTagName("date").item(0).textContent)

            val comments = ArrayList<OsmNoteComment>()
            for (n in 0 until nListComments.length) {
                val text = ((nListComments.item(n) as Element).getElementsByTagName("text").item(0).textContent)

                val creationDate = formatter.parseDateTime((nListComments.item(n) as Element).getElementsByTagName("date").item(0).textContent)

                val element: NodeList = (nListComments.item(n) as Element).getElementsByTagName("user")
                val username = if (element.length != 0) element.item(0).textContent else null

                comments.add(OsmNoteComment(text, creationDate, username ?: ""))
            }

            // The first comment is the Bugs main Info (Description, date and user)
            val firstComment = comments[0]
            comments.removeAt(0)

            bugs.add(OsmNote(GeoPoint(lat, lon), id, firstComment.Comment, firstComment.User!!, date, state, comments))
        }

        bugs
    }
}