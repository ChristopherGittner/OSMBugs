package org.gittner.osmbugs.osmnotes

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Ignore
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

    companion object {
        lateinit var IcOpen: Drawable
        lateinit var IcClosed: Drawable

        lateinit var IcToggleLayer: Drawable
        lateinit var IcToggleLayerDisabled: Drawable

        fun Init() {
            IcOpen = Images.GetDrawable(R.drawable.osm_note_open)
            IcClosed = Images.GetDrawable(R.drawable.osm_note_closed)

            IcToggleLayer = Images.GetDrawable(R.drawable.ic_toggle_osm_notes_layer)
            IcToggleLayerDisabled = Images.GetDrawable(R.drawable.ic_toggle_osm_notes_layer_disabled)
        }
    }
}