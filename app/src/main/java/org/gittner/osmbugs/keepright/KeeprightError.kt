package org.gittner.osmbugs.keepright

import android.graphics.drawable.Drawable
import androidx.room.Entity
import org.gittner.osmbugs.Error
import org.gittner.osmbugs.R
import org.gittner.osmbugs.statics.Images
import org.gittner.osmbugs.statics.OpenStreetMap
import org.joda.time.DateTime
import org.osmdroid.api.IGeoPoint

@Entity(primaryKeys = ["Id", "Schema"])
data class KeeprightError(
    override val Point: IGeoPoint,
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
        val DescriptionId: Int,
        val Drawable: Int,
        val PreferenceId: Int,
        val DefaultEnabled: Boolean = true
    ) {
        _30(
            30,
            R.string.keepright_error_title_30,
            R.drawable.keepright_zap_30,
            R.string.pref_keepright_enabled_30
        ),
        _40(
            40,
            R.string.keepright_error_title_40,
            R.drawable.keepright_zap_40,
            R.string.pref_keepright_enabled_40
        ),
        _50(
            50,
            R.string.keepright_error_title_50,
            R.drawable.keepright_zap_50,
            R.string.pref_keepright_enabled_50
        ),
        _71(
            71,
            R.string.keepright_error_title_70,
            R.drawable.keepright_zap_70,
            R.string.pref_keepright_enabled_70
        ),
        _90(
            90,
            R.string.keepright_error_title_90,
            R.drawable.keepright_zap_90,
            R.string.pref_keepright_enabled_90
        ),
        _100(
            100,
            R.string.keepright_error_title_100,
            R.drawable.keepright_zap_100,
            R.string.pref_keepright_enabled_100
        ),
        _110(
            110,
            R.string.keepright_error_title_110,
            R.drawable.keepright_zap_110,
            R.string.pref_keepright_enabled_110
        ),
        _120(
            120,
            R.string.keepright_error_title_120,
            R.drawable.keepright_zap_120,
            R.string.pref_keepright_enabled_120
        ),
        _130(
            130,
            R.string.keepright_error_title_130,
            R.drawable.keepright_zap_130,
            R.string.pref_keepright_enabled_130
        ),
        _150(
            150,
            R.string.keepright_error_title_150,
            R.drawable.keepright_zap_150,
            R.string.pref_keepright_enabled_150
        ),
        _160(
            160,
            R.string.keepright_error_title_160,
            R.drawable.keepright_zap_160,
            R.string.pref_keepright_enabled_160
        ),
        _170(
            170,
            R.string.keepright_error_title_170,
            R.drawable.keepright_zap_170,
            R.string.pref_keepright_enabled_170
        ),
        _180(
            180,
            R.string.keepright_error_title_180,
            R.drawable.keepright_zap_180,
            R.string.pref_keepright_enabled_180
        ),
        _191(
            191,
            R.string.keepright_error_title_191,
            R.drawable.keepright_zap_190,
            R.string.pref_keepright_enabled_191
        ),
        _192(
            192,
            R.string.keepright_error_title_192,
            R.drawable.keepright_zap_190,
            R.string.pref_keepright_enabled_192
        ),
        _193(
            193,
            R.string.keepright_error_title_193,
            R.drawable.keepright_zap_190,
            R.string.pref_keepright_enabled_193
        ),
        _194(
            194,
            R.string.keepright_error_title_194,
            R.drawable.keepright_zap_190,
            R.string.pref_keepright_enabled_194
        ),
        _195(
            195,
            R.string.keepright_error_title_195,
            R.drawable.keepright_zap_190,
            R.string.pref_keepright_enabled_195
        ),
        _196(
            196,
            R.string.keepright_error_title_196,
            R.drawable.keepright_zap_190,
            R.string.pref_keepright_enabled_196
        ),
        _197(
            197,
            R.string.keepright_error_title_197,
            R.drawable.keepright_zap_190,
            R.string.pref_keepright_enabled_197
        ),
        _198(
            198,
            R.string.keepright_error_title_198,
            R.drawable.keepright_zap_190,
            R.string.pref_keepright_enabled_198
        ),
        _201(
            201,
            R.string.keepright_error_title_201,
            R.drawable.keepright_zap_200,
            R.string.pref_keepright_enabled_201
        ),
        _202(
            202,
            R.string.keepright_error_title_202,
            R.drawable.keepright_zap_200,
            R.string.pref_keepright_enabled_202
        ),
        _203(
            203,
            R.string.keepright_error_title_203,
            R.drawable.keepright_zap_200,
            R.string.pref_keepright_enabled_203
        ),
        _204(
            204,
            R.string.keepright_error_title_204,
            R.drawable.keepright_zap_200,
            R.string.pref_keepright_enabled_204
        ),
        _205(
            205,
            R.string.keepright_error_title_205,
            R.drawable.keepright_zap_200,
            R.string.pref_keepright_enabled_205
        ),
        _206(
            206,
            R.string.keepright_error_title_206,
            R.drawable.keepright_zap_200,
            R.string.pref_keepright_enabled_206
        ),
        _207(
            207,
            R.string.keepright_error_title_207,
            R.drawable.keepright_zap_200,
            R.string.pref_keepright_enabled_207
        ),
        _208(
            208,
            R.string.keepright_error_title_208,
            R.drawable.keepright_zap_200,
            R.string.pref_keepright_enabled_208
        ),
        _210(
            210,
            R.string.keepright_error_title_210,
            R.drawable.keepright_zap_210,
            R.string.pref_keepright_enabled_210
        ),
        _220(
            220,
            R.string.keepright_error_title_220,
            R.drawable.keepright_zap_220,
            R.string.pref_keepright_enabled_220
        ),
        _231(
            231,
            R.string.keepright_error_title_231,
            R.drawable.keepright_zap_220,
            R.string.pref_keepright_enabled_231
        ),
        _232(
            232,
            R.string.keepright_error_title_232,
            R.drawable.keepright_zap_220,
            R.string.pref_keepright_enabled_232
        ),
        _270(
            270,
            R.string.keepright_error_title_270,
            R.drawable.keepright_zap_270,
            R.string.pref_keepright_enabled_270
        ),
        _281(
            281,
            R.string.keepright_error_title_281,
            R.drawable.keepright_zap_280,
            R.string.pref_keepright_enabled_281
        ),
        _282(
            282,
            R.string.keepright_error_title_282,
            R.drawable.keepright_zap_280,
            R.string.pref_keepright_enabled_282
        ),
        _283(
            283,
            R.string.keepright_error_title_283,
            R.drawable.keepright_zap_280,
            R.string.pref_keepright_enabled_283
        ),
        _284(
            284,
            R.string.keepright_error_title_284,
            R.drawable.keepright_zap_280,
            R.string.pref_keepright_enabled_284
        ),
        _285(
            285,
            R.string.keepright_error_title_285,
            R.drawable.keepright_zap_280,
            R.string.pref_keepright_enabled_285
        ),
        _291(
            291,
            R.string.keepright_error_title_291,
            R.drawable.keepright_zap_290,
            R.string.pref_keepright_enabled_291
        ),
        _292(
            292,
            R.string.keepright_error_title_292,
            R.drawable.keepright_zap_290,
            R.string.pref_keepright_enabled_292
        ),
        _293(
            293,
            R.string.keepright_error_title_293,
            R.drawable.keepright_zap_290,
            R.string.pref_keepright_enabled_293
        ),
        _294(
            294,
            R.string.keepright_error_title_294,
            R.drawable.keepright_zap_290,
            R.string.pref_keepright_enabled_294
        ),
        _295(
            295,
            R.string.keepright_error_title_295,
            R.drawable.keepright_zap_290,
            R.string.pref_keepright_enabled_295
        ),
        _296(
            296,
            R.string.keepright_error_title_296,
            R.drawable.keepright_zap_290,
            R.string.pref_keepright_enabled_296
        ),
        _297(
            297,
            R.string.keepright_error_title_297,
            R.drawable.keepright_zap_290,
            R.string.pref_keepright_enabled_297
        ),
        _298(
            298,
            R.string.keepright_error_title_298,
            R.drawable.keepright_zap_290,
            R.string.pref_keepright_enabled_298
        ),
        _311(
            311,
            R.string.keepright_error_title_311,
            R.drawable.keepright_zap_310,
            R.string.pref_keepright_enabled_311
        ),
        _312(
            312,
            R.string.keepright_error_title_312,
            R.drawable.keepright_zap_310,
            R.string.pref_keepright_enabled_312
        ),
        _313(
            313,
            R.string.keepright_error_title_313,
            R.drawable.keepright_zap_310,
            R.string.pref_keepright_enabled_313
        ),
        _320(
            320,
            R.string.keepright_error_title_320,
            R.drawable.keepright_zap_320,
            R.string.pref_keepright_enabled_320
        ),
        _350(
            350,
            R.string.keepright_error_title_350,
            R.drawable.keepright_zap_350,
            R.string.pref_keepright_enabled_350
        ),
        _370(
            370,
            R.string.keepright_error_title_370,
            R.drawable.keepright_zap_370,
            R.string.pref_keepright_enabled_370
        ),
        _380(
            380,
            R.string.keepright_error_title_380,
            R.drawable.keepright_zap_380,
            R.string.pref_keepright_enabled_380
        ),
        _401(
            401,
            R.string.keepright_error_title_401,
            R.drawable.keepright_zap_400,
            R.string.pref_keepright_enabled_401
        ),
        _402(
            402,
            R.string.keepright_error_title_402,
            R.drawable.keepright_zap_400,
            R.string.pref_keepright_enabled_402
        ),
        _411(
            411,
            R.string.keepright_error_title_411,
            R.drawable.keepright_zap_410,
            R.string.pref_keepright_enabled_411
        ),
        _412(
            412,
            R.string.keepright_error_title_412,
            R.drawable.keepright_zap_410,
            R.string.pref_keepright_enabled_412
        ),
        _413(
            413,
            R.string.keepright_error_title_413,
            R.drawable.keepright_zap_410,
            R.string.pref_keepright_enabled_413
        ),
        _20(
            20,
            R.string.keepright_error_title_20,
            R.drawable.keepright_zap_20,
            R.string.pref_keepright_enabled_20,
            false
        ),
        _60(
            60,
            R.string.keepright_error_title_60,
            R.drawable.keepright_zap_60,
            R.string.pref_keepright_enabled_60,
            false
        ),
        _300(
            300,
            R.string.keepright_error_title_300,
            R.drawable.keepright_zap_300,
            R.string.pref_keepright_enabled_300,
            false
        ),
        _360(
            360,
            R.string.keepright_error_title_360,
            R.drawable.keepright_zap_360,
            R.string.pref_keepright_enabled_360,
            false
        ),
        _390(
            390,
            R.string.keepright_error_title_390,
            R.drawable.keepright_zap_390,
            R.string.pref_keepright_enabled_390,
            false
        )
    }

    companion object {
        fun GetZapIconFor(state: STATE, type: ERROR_TYPE): Drawable {
            return when (state) {
                STATE.IGNORED -> Images.GetDrawable(R.drawable.keepright_zap_ignored)
                STATE.IGNORED_TMP -> Images.GetDrawable(R.drawable.keepright_zap_tmp_ignored)
                else -> Images.GetDrawable(type.Drawable)
            }
        }

        fun GetTypeByTypeNumber(n: Int): ERROR_TYPE? {
            var type = ERROR_TYPE.entries.find {
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