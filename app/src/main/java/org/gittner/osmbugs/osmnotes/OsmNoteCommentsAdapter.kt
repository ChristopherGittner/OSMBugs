package org.gittner.osmbugs.osmnotes

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.RowOsmNoteCommentBinding
import org.joda.time.DateTimeZone

/**
 * Adapter for OsmNote Comments.
 * Each row contains the User an the comment
 * @param context The current context.
 * @param comments The Comments to display. The comments will be added to the Adapter automatically
 */
class OsmNoteCommentsAdapter(context: Context, comments: ArrayList<OsmNote.OsmNoteComment>) : ArrayAdapter<OsmNote.OsmNoteComment>(context, R.layout.row_osm_note_comment) {
    init {
        addAll(comments)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_osm_note_comment, parent, false)

        val binding = RowOsmNoteCommentBinding.bind(v)

        val item = getItem(position)!!

        binding.apply {
            txtvUser.text = item.User
            txtvText.text = item.Comment
            txtvDate.text = item.Date.withZone(DateTimeZone.getDefault()).toString(context.getString(R.string.datetime_format))
        }

        return v
    }
}
