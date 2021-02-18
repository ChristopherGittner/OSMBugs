package org.gittner.osmbugs.statics

class OpenStreetMap {
    data class Object(
        val Id: Long,
        val ObjectType: TYPE
    )

    enum class TYPE {
        NODE,
        WAY,
        RELATION
    }
}