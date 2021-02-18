package org.gittner.osmbugs.keepright

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Ignore
import org.gittner.osmbugs.Error
import org.gittner.osmbugs.R
import org.gittner.osmbugs.statics.Images
import org.gittner.osmbugs.statics.OpenStreetMap
import org.joda.time.DateTime
import org.osmdroid.api.IGeoPoint

@Entity(primaryKeys = ["Id", "Schema"])
data class KeeprightError(
    @Ignore override val Point: IGeoPoint,
    val CreationDate: DateTime,
    val Id: Long,
    val Schema: Long,
    val Type: ERROR_TYPE,
    val ObjectType: OpenStreetMap.TYPE,
    val Title: String,
    val Description: String,
    val Comment: String,
    val State: STATE,
    val Way: Long
) : Error(Point) {

    constructor(error: KeeprightError, comment: String, state: STATE) : this(
        error.Point,
        error.CreationDate,
        error.Id,
        error.Schema,
        error.Type,
        error.ObjectType,
        error.Title,
        error.Description,
        comment,
        state,
        error.Way
    )

    enum class STATE {
        OPEN,
        IGNORED,
        IGNORED_TMP
    }

    enum class ERROR_TYPE(
        val Type: Int,
        val Icon: Drawable,
        val DescriptionId: Int,
        val PreferenceId: Int,
        val DefaultEnabled: Boolean = true
    ) {
        _30(30, IcZap30, R.string.keepright_error_title_30, R.string.pref_keepright_enabled_30),
        _40(40, IcZap40, R.string.keepright_error_title_40, R.string.pref_keepright_enabled_40),
        _50(50, IcZap50, R.string.keepright_error_title_50, R.string.pref_keepright_enabled_50),
        _71(71, IcZap70, R.string.keepright_error_title_70, R.string.pref_keepright_enabled_70),
        _90(90, IcZap90, R.string.keepright_error_title_90, R.string.pref_keepright_enabled_90),
        _100(100, IcZap100, R.string.keepright_error_title_100, R.string.pref_keepright_enabled_100),
        _110(110, IcZap110, R.string.keepright_error_title_110, R.string.pref_keepright_enabled_110),
        _120(120, IcZap120, R.string.keepright_error_title_120, R.string.pref_keepright_enabled_120),
        _130(130, IcZap130, R.string.keepright_error_title_130, R.string.pref_keepright_enabled_130),
        _150(150, IcZap150, R.string.keepright_error_title_150, R.string.pref_keepright_enabled_150),
        _160(160, IcZap160, R.string.keepright_error_title_160, R.string.pref_keepright_enabled_160),
        _170(170, IcZap170, R.string.keepright_error_title_170, R.string.pref_keepright_enabled_170),
        _180(180, IcZap180, R.string.keepright_error_title_180, R.string.pref_keepright_enabled_180),
        _191(191, IcZap190, R.string.keepright_error_title_191, R.string.pref_keepright_enabled_191),
        _192(192, IcZap190, R.string.keepright_error_title_192, R.string.pref_keepright_enabled_192),
        _193(193, IcZap190, R.string.keepright_error_title_193, R.string.pref_keepright_enabled_193),
        _194(194, IcZap190, R.string.keepright_error_title_194, R.string.pref_keepright_enabled_194),
        _195(195, IcZap190, R.string.keepright_error_title_195, R.string.pref_keepright_enabled_195),
        _196(196, IcZap190, R.string.keepright_error_title_196, R.string.pref_keepright_enabled_196),
        _197(197, IcZap190, R.string.keepright_error_title_197, R.string.pref_keepright_enabled_197),
        _198(198, IcZap190, R.string.keepright_error_title_198, R.string.pref_keepright_enabled_198),
        _201(201, IcZap200, R.string.keepright_error_title_201, R.string.pref_keepright_enabled_201),
        _202(202, IcZap200, R.string.keepright_error_title_202, R.string.pref_keepright_enabled_202),
        _203(203, IcZap200, R.string.keepright_error_title_203, R.string.pref_keepright_enabled_203),
        _204(204, IcZap200, R.string.keepright_error_title_204, R.string.pref_keepright_enabled_204),
        _205(205, IcZap200, R.string.keepright_error_title_205, R.string.pref_keepright_enabled_205),
        _206(206, IcZap200, R.string.keepright_error_title_206, R.string.pref_keepright_enabled_206),
        _207(207, IcZap200, R.string.keepright_error_title_207, R.string.pref_keepright_enabled_207),
        _208(208, IcZap200, R.string.keepright_error_title_208, R.string.pref_keepright_enabled_208),
        _210(210, IcZap210, R.string.keepright_error_title_210, R.string.pref_keepright_enabled_210),
        _220(220, IcZap220, R.string.keepright_error_title_220, R.string.pref_keepright_enabled_220),
        _231(231, IcZap230, R.string.keepright_error_title_231, R.string.pref_keepright_enabled_231),
        _232(232, IcZap230, R.string.keepright_error_title_232, R.string.pref_keepright_enabled_232),
        _270(270, IcZap270, R.string.keepright_error_title_270, R.string.pref_keepright_enabled_270),
        _281(281, IcZap280, R.string.keepright_error_title_281, R.string.pref_keepright_enabled_281),
        _282(282, IcZap280, R.string.keepright_error_title_282, R.string.pref_keepright_enabled_282),
        _283(283, IcZap280, R.string.keepright_error_title_283, R.string.pref_keepright_enabled_283),
        _284(284, IcZap280, R.string.keepright_error_title_284, R.string.pref_keepright_enabled_284),
        _285(285, IcZap280, R.string.keepright_error_title_285, R.string.pref_keepright_enabled_285),
        _291(291, IcZap290, R.string.keepright_error_title_291, R.string.pref_keepright_enabled_291),
        _292(292, IcZap290, R.string.keepright_error_title_292, R.string.pref_keepright_enabled_292),
        _293(293, IcZap290, R.string.keepright_error_title_293, R.string.pref_keepright_enabled_293),
        _294(294, IcZap290, R.string.keepright_error_title_294, R.string.pref_keepright_enabled_294),
        _295(295, IcZap290, R.string.keepright_error_title_295, R.string.pref_keepright_enabled_295),
        _296(296, IcZap290, R.string.keepright_error_title_296, R.string.pref_keepright_enabled_296),
        _297(297, IcZap290, R.string.keepright_error_title_297, R.string.pref_keepright_enabled_297),
        _298(298, IcZap290, R.string.keepright_error_title_298, R.string.pref_keepright_enabled_298),
        _311(311, IcZap310, R.string.keepright_error_title_311, R.string.pref_keepright_enabled_311),
        _312(312, IcZap310, R.string.keepright_error_title_312, R.string.pref_keepright_enabled_312),
        _313(313, IcZap310, R.string.keepright_error_title_313, R.string.pref_keepright_enabled_313),
        _320(320, IcZap320, R.string.keepright_error_title_320, R.string.pref_keepright_enabled_320),
        _350(350, IcZap350, R.string.keepright_error_title_350, R.string.pref_keepright_enabled_350),
        _370(370, IcZap370, R.string.keepright_error_title_370, R.string.pref_keepright_enabled_370),
        _380(380, IcZap380, R.string.keepright_error_title_380, R.string.pref_keepright_enabled_380),
        _401(401, IcZap400, R.string.keepright_error_title_401, R.string.pref_keepright_enabled_401),
        _402(402, IcZap400, R.string.keepright_error_title_402, R.string.pref_keepright_enabled_402),
        _411(411, IcZap410, R.string.keepright_error_title_411, R.string.pref_keepright_enabled_411),
        _412(412, IcZap410, R.string.keepright_error_title_412, R.string.pref_keepright_enabled_412),
        _413(413, IcZap410, R.string.keepright_error_title_413, R.string.pref_keepright_enabled_413),
        _20(20, IcZap20, R.string.keepright_error_title_20, R.string.pref_keepright_enabled_20, false),
        _60(60, IcZap60, R.string.keepright_error_title_60, R.string.pref_keepright_enabled_60, false),
        _300(300, IcZap300, R.string.keepright_error_title_300, R.string.pref_keepright_enabled_300, false),
        _360(360, IcZap360, R.string.keepright_error_title_360, R.string.pref_keepright_enabled_360, false),
        _390(390, IcZap390, R.string.keepright_error_title_390, R.string.pref_keepright_enabled_390, false)
    }

    companion object {
        lateinit var IcToggleLayer: Drawable
        lateinit var IcToggleLayerDisabled: Drawable

        lateinit var IcZapIgnored: Drawable
        lateinit var IcZapTmpIgnored: Drawable

        lateinit var IcZap20: Drawable
        lateinit var IcZap30: Drawable
        lateinit var IcZap40: Drawable
        lateinit var IcZap50: Drawable
        lateinit var IcZap60: Drawable
        lateinit var IcZap70: Drawable
        lateinit var IcZap90: Drawable
        lateinit var IcZap100: Drawable
        lateinit var IcZap110: Drawable
        lateinit var IcZap120: Drawable
        lateinit var IcZap130: Drawable
        lateinit var IcZap150: Drawable
        lateinit var IcZap160: Drawable
        lateinit var IcZap170: Drawable
        lateinit var IcZap180: Drawable
        lateinit var IcZap190: Drawable
        lateinit var IcZap200: Drawable
        lateinit var IcZap210: Drawable
        lateinit var IcZap220: Drawable
        lateinit var IcZap230: Drawable
        lateinit var IcZap270: Drawable
        lateinit var IcZap280: Drawable
        lateinit var IcZap290: Drawable
        lateinit var IcZap300: Drawable
        lateinit var IcZap310: Drawable
        lateinit var IcZap320: Drawable
        lateinit var IcZap350: Drawable
        lateinit var IcZap360: Drawable
        lateinit var IcZap370: Drawable
        lateinit var IcZap380: Drawable
        lateinit var IcZap390: Drawable
        lateinit var IcZap400: Drawable
        lateinit var IcZap410: Drawable

        fun Init() {
            IcToggleLayer = Images.GetDrawable(R.drawable.ic_toggle_keepright_layer)
            IcToggleLayerDisabled = Images.GetDrawable(R.drawable.ic_toggle_keepright_layer_disabled)

            IcZapIgnored = Images.GetDrawable(R.drawable.keepright_zap_ignored)
            IcZapTmpIgnored = Images.GetDrawable(R.drawable.keepright_zap_tmp_ignored)

            IcZap20 = Images.GetDrawable(R.drawable.keepright_zap_20)
            IcZap30 = Images.GetDrawable(R.drawable.keepright_zap_30)
            IcZap40 = Images.GetDrawable(R.drawable.keepright_zap_40)
            IcZap50 = Images.GetDrawable(R.drawable.keepright_zap_50)
            IcZap60 = Images.GetDrawable(R.drawable.keepright_zap_60)
            IcZap70 = Images.GetDrawable(R.drawable.keepright_zap_70)
            IcZap90 = Images.GetDrawable(R.drawable.keepright_zap_90)
            IcZap100 = Images.GetDrawable(R.drawable.keepright_zap_100)
            IcZap110 = Images.GetDrawable(R.drawable.keepright_zap_110)
            IcZap120 = Images.GetDrawable(R.drawable.keepright_zap_120)
            IcZap130 = Images.GetDrawable(R.drawable.keepright_zap_130)
            IcZap150 = Images.GetDrawable(R.drawable.keepright_zap_150)
            IcZap160 = Images.GetDrawable(R.drawable.keepright_zap_160)
            IcZap170 = Images.GetDrawable(R.drawable.keepright_zap_170)
            IcZap180 = Images.GetDrawable(R.drawable.keepright_zap_180)
            IcZap190 = Images.GetDrawable(R.drawable.keepright_zap_190)
            IcZap200 = Images.GetDrawable(R.drawable.keepright_zap_200)
            IcZap210 = Images.GetDrawable(R.drawable.keepright_zap_210)
            IcZap220 = Images.GetDrawable(R.drawable.keepright_zap_220)
            IcZap230 = Images.GetDrawable(R.drawable.keepright_zap_220)
            IcZap270 = Images.GetDrawable(R.drawable.keepright_zap_270)
            IcZap280 = Images.GetDrawable(R.drawable.keepright_zap_280)
            IcZap290 = Images.GetDrawable(R.drawable.keepright_zap_290)
            IcZap300 = Images.GetDrawable(R.drawable.keepright_zap_300)
            IcZap310 = Images.GetDrawable(R.drawable.keepright_zap_310)
            IcZap320 = Images.GetDrawable(R.drawable.keepright_zap_320)
            IcZap350 = Images.GetDrawable(R.drawable.keepright_zap_350)
            IcZap360 = Images.GetDrawable(R.drawable.keepright_zap_360)
            IcZap370 = Images.GetDrawable(R.drawable.keepright_zap_370)
            IcZap380 = Images.GetDrawable(R.drawable.keepright_zap_380)
            IcZap390 = Images.GetDrawable(R.drawable.keepright_zap_390)
            IcZap400 = Images.GetDrawable(R.drawable.keepright_zap_400)
            IcZap410 = Images.GetDrawable(R.drawable.keepright_zap_410)
        }

        fun GetZapIconFor(state: STATE, type: ERROR_TYPE): Drawable {
            return when (state) {
                STATE.IGNORED -> IcZapIgnored
                STATE.IGNORED_TMP -> IcZapTmpIgnored
                else -> type.Icon
            }
        }

        fun GetTypeByTypeNumber(n: Int): ERROR_TYPE? {
            var type = ERROR_TYPE.values().find {
                n == it.Type
            }

            if (type == null && n % 10 > 0) {
                // Get the next highest Type i.e. 71 -> 70
                type = GetTypeByTypeNumber(n / 10 * 10)
            }

            return type
        }
    }
}