package org.gittner.osmbugs.osmnotes

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.PrimaryKey
import org.gittner.osmbugs.Error
import org.gittner.osmbugs.R
import org.gittner.osmbugs.statics.Images
import org.joda.time.DateTime
import org.osmdroid.api.IGeoPoint

@Entity
data class OsmNote(
    override val Point: IGeoPoint,
    @PrimaryKey val Id: Long,
    val Description: String,
    val User: String,
    val Date: DateTime,
    val State: STATE,
    val Comments: ArrayList<OsmNoteComment>
) : Error(Point) {

    data class OsmNoteComment(
        val Comment: String,
        val Date: DateTime,
        val User: String?
    )

    enum class STATE {
        OPEN,
        CLOSED
    }
}