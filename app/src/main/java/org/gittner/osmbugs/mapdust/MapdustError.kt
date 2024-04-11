package org.gittner.osmbugs.mapdust

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
data class MapdustError(
    override val Point: IGeoPoint,
    @PrimaryKey val Id: Long,
    val CreationDate: DateTime,
    val User: String,
    val Type: ERROR_TYPE,
    val Description: String,
    var State: STATE
) : Error(Point) {

    @Ignore
    var Comments = ArrayList<MapdustComment>()

    enum class ERROR_TYPE(
        val type: Int,
        val Icon: Drawable,
        val DescriptionId: Int,
        val PreferenceId: Int
    ) {
        WRONG_TURN(1, IcWrongTurn, R.string.wrong_turn, R.string.pref_mapdust_enabled_wrong_turn),
        BAD_ROUTING(2, IcBadRouting, R.string.bad_routing, R.string.pref_mapdust_enabled_bad_routing),
        ONEWAY_ROAD(3, IcOnewayRoad, R.string.oneway_road, R.string.pref_mapdust_enabled_oneway_road),
        BLOCKED_STREET(4, IcBlockedStreet, R.string.blocked_street, R.string.pref_mapdust_enabled_blocked_street),
        MISSING_STREET(5, IcMissingStreet, R.string.missing_street, R.string.pref_mapdust_enabled_missing_street),
        ROUNDABOUT_ISSUE(6, IcRoundaboutIssue, R.string.roundabout_issue, R.string.pref_mapdust_enabled_roundabout_issue),
        MISSING_SPEED_INFO(7, IcMissingSpeedInfo, R.string.missing_speed_info, R.string.pref_mapdust_enabled_missing_speed_info),
        OTHER(8, IcOther, R.string.other, R.string.pref_mapdust_enabled_other);
    }

    enum class STATE {
        OPEN,
        CLOSED,
        IGNORED
    }

    class MapdustComment(
        val Text: String,
        val User: String
    )

    companion object {
        lateinit var IcToggleLayer: Drawable
        lateinit var IcToggleLayerDisabled: Drawable

        lateinit var IcWrongTurn: Drawable
        lateinit var IcBadRouting: Drawable
        lateinit var IcOnewayRoad: Drawable
        lateinit var IcBlockedStreet: Drawable
        lateinit var IcMissingStreet: Drawable
        lateinit var IcRoundaboutIssue: Drawable
        lateinit var IcMissingSpeedInfo: Drawable
        lateinit var IcOther: Drawable
        lateinit var IcIgnored: Drawable
        lateinit var IcClosed: Drawable

        fun Init() {
            IcToggleLayer = Images.GetDrawable(R.drawable.ic_toggle_mapdust_layer)
            IcToggleLayerDisabled = Images.GetDrawable(R.drawable.ic_toggle_mapdust_layer_disabled)

            IcWrongTurn = Images.GetDrawable(R.drawable.mapdust_wrong_turn)
            IcBadRouting = Images.GetDrawable(R.drawable.mapdust_bad_routing)
            IcOnewayRoad = Images.GetDrawable(R.drawable.mapdust_oneway_road)
            IcBlockedStreet = Images.GetDrawable(R.drawable.mapdust_blocked_street)
            IcMissingStreet = Images.GetDrawable(R.drawable.mapdust_missing_street)
            IcRoundaboutIssue = Images.GetDrawable(R.drawable.mapdust_roundabout_issue)
            IcMissingSpeedInfo = Images.GetDrawable(R.drawable.mapdust_missing_speed_info)
            IcOther = Images.GetDrawable(R.drawable.mapdust_other)
            IcIgnored = Images.GetDrawable(R.drawable.mapdust_ignored)
            IcClosed = Images.GetDrawable(R.drawable.mapdust_closed)
        }

        fun GetIconFor(state: STATE, type: ERROR_TYPE): Drawable {
            return when (state) {
                STATE.IGNORED -> IcIgnored
                STATE.CLOSED -> IcClosed
                else -> return when (type) {
                    ERROR_TYPE.WRONG_TURN -> IcWrongTurn
                    ERROR_TYPE.BAD_ROUTING -> IcBadRouting
                    ERROR_TYPE.ONEWAY_ROAD -> IcOnewayRoad
                    ERROR_TYPE.BLOCKED_STREET -> IcBlockedStreet
                    ERROR_TYPE.MISSING_STREET -> IcMissingStreet
                    ERROR_TYPE.ROUNDABOUT_ISSUE -> IcRoundaboutIssue
                    ERROR_TYPE.MISSING_SPEED_INFO -> IcMissingSpeedInfo
                    ERROR_TYPE.OTHER -> IcOther
                }
            }
        }
    }
}