package org.gittner.osmbugs.osmose

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.RowOsmoseElementBinding
import org.gittner.osmbugs.statics.OpenStreetMap

class OsmoseElementAdapter(context: Context, elements: ArrayList<OpenStreetMap.Object>) : ArrayAdapter<OpenStreetMap.Object>(context, R.layout.row_osmose_element) {
    init {
        addAll(elements)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_osmose_element, parent, false)

        val binding = RowOsmoseElementBinding.bind(v)

        val element = getItem(position)!!

        binding.imgBrowse.setOnClickListener {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.Builder()
                .scheme("https")
                .authority("openstreetmap.org")
                .appendPath(
                    when (element.ObjectType) {
                        OpenStreetMap.TYPE.NODE -> "node"
                        OpenStreetMap.TYPE.WAY -> "way"
                        OpenStreetMap.TYPE.RELATION -> "relation"
                    }
                )
                .appendPath(element.Id.toString())
                .build()
            context.startActivity(intent)
        }

        binding.txtvDescription.text = when (element.ObjectType) {
            OpenStreetMap.TYPE.NODE -> "${context.getString(R.string.node)} ${element.Id}"
            OpenStreetMap.TYPE.WAY -> "${context.getString(R.string.way)} ${element.Id}"
            OpenStreetMap.TYPE.RELATION -> "${context.getString(R.string.relation)} ${element.Id}"
        }

        return v
    }
}