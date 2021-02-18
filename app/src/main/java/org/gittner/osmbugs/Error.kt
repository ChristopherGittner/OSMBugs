package org.gittner.osmbugs

import androidx.room.Entity
import org.osmdroid.api.IGeoPoint

@Entity
open class Error(
    open val Point: IGeoPoint
)