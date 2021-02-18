package org.gittner.osmbugs.ui

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatImageView
import org.gittner.osmbugs.R

/**
 * Adapter that will display displays each Item with an Image, a CheckBox and a Text.
 * Each item also contains a Reference to the original type, to get acces to it depending on the checkbox State
 * @param context The current context.
 */
class EnabledErrorsAdapter<T>(context: Context) : ArrayAdapter<EnabledErrorsAdapter.Data<T>>(context, R.layout.row_select_errors) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val v = convertView ?: LayoutInflater.from(context).inflate(R.layout.row_select_errors, parent, false)

        val item = getItem(position)!!

        v.findViewById<TextView>(R.id.description).setText(item.Description)
        v.findViewById<AppCompatImageView>(R.id.icon).setImageDrawable(item.Image)

        val checkBox = v.findViewById<AppCompatCheckBox>(R.id.checkbox)
        checkBox.setOnCheckedChangeListener(null)
        checkBox.isChecked = item.State
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            item.State = isChecked
        }

        return v
    }

    /**
     * Data class for each Row
     * @param Ref: Reference to the Users type
     * @param State: The state of the Check Box
     * @param Description: The Text displayed
     * @param Image: The Image displayed
     */
    data class Data<T>(
        val Ref: T?,
        var State: Boolean,
        val Description: Int,
        val Image: Drawable
    )
}