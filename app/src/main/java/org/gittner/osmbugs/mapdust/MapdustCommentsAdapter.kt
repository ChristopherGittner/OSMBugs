package org.gittner.osmbugs.mapdust

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.RowMapdustCommentBinding

/**
 * Adapter for OsmNote Comments.
 * Each row contains the User an the comment
 * @param context The current context.
 * @param comments The Comments to display. The comments will be added to the Adapter automatically
 */
class MapdustCommentsAdapter(context: Context, comments: ArrayList<MapdustError.MapdustComment>) :
    ArrayAdapter<MapdustError.MapdustComment>(context, R.layout.row_mapdust_comment) {
    init {
        addAll(comments)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_osm_note_comment, parent, false)

        val item = getItem(position)!!

        val binding = RowMapdustCommentBinding.bind(v)

        binding.apply {
            txtvText.text = item.Text
            txtvUser.text = item.User
        }

        return v
    }
}
