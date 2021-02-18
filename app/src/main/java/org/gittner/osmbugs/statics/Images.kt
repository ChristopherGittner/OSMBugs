package org.gittner.osmbugs.statics

import android.content.res.Resources
import android.graphics.drawable.Drawable
import androidx.core.content.res.ResourcesCompat
import org.gittner.osmbugs.App
import org.gittner.osmbugs.keepright.KeeprightError
import org.gittner.osmbugs.mapdust.MapdustError
import org.gittner.osmbugs.osmnotes.OsmNote
import org.gittner.osmbugs.osmose.OsmoseError

class Images {
    companion object {
        private lateinit var mResources: Resources

        /**
         * Returns a Drawable for the given id
         * @param id The ID of the Drawable
         */
        fun GetDrawable(id: Int): Drawable {
            return ResourcesCompat.getDrawable(mResources, id, null)!!
        }

        fun init(app: App) {
            mResources = app.applicationContext.resources

            OsmNote.Init()
            KeeprightError.Init()
            MapdustError.Init()
            OsmoseError.Init()
        }
    }
}