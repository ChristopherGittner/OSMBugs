package org.gittner.osmbugs.mapdust

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import org.gittner.osmbugs.R
import org.gittner.osmbugs.databinding.RowMapdustTypeBinding

/**
 * Maps all Mapdust Types to a Row.
 * Each row contains an Image and the Description for the Type
 * The Types are taken from MapdustError.ERROR_TYPE
 * @param context The current Context
 */
class MapdustTypeAdapter(context: Context) : ArrayAdapter<MapdustError.ERROR_TYPE>(context, R.layout.row_mapdust_type) {
    init {
        MapdustError.ERROR_TYPE.values().forEach {
            add(it)
        }
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_mapdust_type, parent, false)

        val binding = RowMapdustTypeBinding.bind(v)

        val item = getItem(position)

        binding.icon.setImageDrawable(item!!.Icon)
        binding.name.text = context.getString(item.DescriptionId)

        return v
    }
}
