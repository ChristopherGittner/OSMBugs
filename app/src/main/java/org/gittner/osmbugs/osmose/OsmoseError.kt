package org.gittner.osmbugs.osmose

import android.graphics.drawable.Drawable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import org.gittner.osmbugs.Error
import org.gittner.osmbugs.R
import org.gittner.osmbugs.statics.Images
import org.gittner.osmbugs.statics.OpenStreetMap
import org.joda.time.DateTime
import org.osmdroid.api.IGeoPoint

@Entity
data class OsmoseError(
    override val Point: IGeoPoint,
    @PrimaryKey val Id: Long,
    val Type: ERROR_TYPE,
    val CreationDate: DateTime,
    val Title: String,
    val SubTitle: String
) : Error(Point) {

    var Elements = ArrayList<OpenStreetMap.Object>()

    @Suppress("EnumEntryName")
    enum class ERROR_TYPE(
        val Item: Int,
        val Icon: Drawable,
        val DescriptionId: Int
    ) {
        _0(0, IcMarker0, R.string.osmose_error_description_0),
        _1(1, IcMarker1, R.string.osmose_error_description_1),
        _1010(1010, IcMarker1010, R.string.osmose_error_description_1010),
        _1040(1040, IcMarker1040, R.string.osmose_error_description_1040),
        _1050(1050, IcMarker1050, R.string.osmose_error_description_1050),
        _1060(1060, IcMarker1060, R.string.osmose_error_description_1060),
        _1070(1070, IcMarker1070, R.string.osmose_error_description_1070),
        _1080(1080, IcMarker1080, R.string.osmose_error_description_1080),
        _1090(1090, IcMarker1090, R.string.osmose_error_description_1090),
        _1100(1100, IcMarker1100, R.string.osmose_error_description_1100),
        _1110(1110, IcMarker1110, R.string.osmose_error_description_1110),
        _1120(1120, IcMarker1120, R.string.osmose_error_description_1120),
        _1140(1140, IcMarker1140, R.string.osmose_error_description_1140),
        _1150(1150, IcMarker1150, R.string.osmose_error_description_1150),
        _1160(1160, IcMarker1160, R.string.osmose_error_description_1160),
        _1170(1170, IcMarker1170, R.string.osmose_error_description_1170),
        _1180(1180, IcMarker1180, R.string.osmose_error_description_1180),
        _1190(1190, IcMarker1190, R.string.osmose_error_description_1190),
        _1200(1200, IcMarker1200, R.string.osmose_error_description_1200),
        _1210(1210, IcMarker1210, R.string.osmose_error_description_1210),
        _1220(1220, IcMarker1220, R.string.osmose_error_description_1220),
        _1221(1221, IcMarker1221, R.string.osmose_error_description_1221),
        _1230(1230, IcMarker1230, R.string.osmose_error_description_1230),
        _1240(1240, IcMarker1240, R.string.osmose_error_description_1240),
        _1250(1250, IcMarker1250, R.string.osmose_error_description_1250),
        _1260(1260, IcMarker1260, R.string.osmose_error_description_1260),
        _1270(1270, IcMarker1270, R.string.osmose_error_description_1270),
        _1280(1280, IcMarker1280, R.string.osmose_error_description_1280),
        _1290(1290, IcMarker1290, R.string.osmose_error_description_1290),
        _2010(2010, IcMarker2010, R.string.osmose_error_description_2010),
        _2020(2020, IcMarker2020, R.string.osmose_error_description_2020),
        _2030(2030, IcMarker2030, R.string.osmose_error_description_2030),
        _2040(2040, IcMarker2040, R.string.osmose_error_description_2040),
        _2060(2060, IcMarker2060, R.string.osmose_error_description_2060),
        _2080(2080, IcMarker2080, R.string.osmose_error_description_2080),
        _2090(2090, IcMarker2090, R.string.osmose_error_description_2090),
        _2100(2100, IcMarker2100, R.string.osmose_error_description_2100),
        _2110(2110, IcMarker2110, R.string.osmose_error_description_2110),
        _2120(2120, IcMarker2120, R.string.osmose_error_description_2120),
        _2130(2130, IcMarker2130, R.string.osmose_error_description_2130),
        _2140(2140, IcMarker2140, R.string.osmose_error_description_2140),
        _2150(2150, IcMarker2150, R.string.osmose_error_description_2150),
        _2160(2160, IcMarker2160, R.string.osmose_error_description_2160),
        _3010(3010, IcMarker3010, R.string.osmose_error_description_3010),
        _3020(3020, IcMarker3020, R.string.osmose_error_description_3020),
        _3030(3030, IcMarker3030, R.string.osmose_error_description_3030),
        _3031(3031, IcMarker3031, R.string.osmose_error_description_3031),
        _3032(3032, IcMarker3032, R.string.osmose_error_description_3032),
        _3033(3033, IcMarker3033, R.string.osmose_error_description_3033),
        _3040(3040, IcMarker3040, R.string.osmose_error_description_3040),
        _3050(3050, IcMarker3050, R.string.osmose_error_description_3050),
        _3060(3060, IcMarker3060, R.string.osmose_error_description_3060),
        _3070(3070, IcMarker3070, R.string.osmose_error_description_3070),
        _3080(3080, IcMarker3080, R.string.osmose_error_description_3080),
        _3090(3090, IcMarker3090, R.string.osmose_error_description_3090),
        _3091(3091, IcMarker3091, R.string.osmose_error_description_3091),
        _3092(3092, IcMarker3092, R.string.osmose_error_description_3092),
        _3093(3093, IcMarker3093, R.string.osmose_error_description_3093),
        _3110(3110, IcMarker3110, R.string.osmose_error_description_3110),
        _3120(3120, IcMarker3120, R.string.osmose_error_description_3120),
        _3150(3150, IcMarker3150, R.string.osmose_error_description_3150),
        _3160(3160, IcMarker3160, R.string.osmose_error_description_3160),
        _3161(3161, IcMarker3161, R.string.osmose_error_description_3161),
        _3170(3170, IcMarker3170, R.string.osmose_error_description_3170),
        _3180(3180, IcMarker3180, R.string.osmose_error_description_3180),
        _3190(3190, IcMarker3190, R.string.osmose_error_description_3190),
        _3200(3200, IcMarker3200, R.string.osmose_error_description_3200),
        _3210(3210, IcMarker3210, R.string.osmose_error_description_3210),
        _3220(3220, IcMarker3220, R.string.osmose_error_description_3220),
        _3230(3230, IcMarker3230, R.string.osmose_error_description_3230),
        _3240(3240, IcMarker3240, R.string.osmose_error_description_3240),
        _3250(3250, IcMarker3250, R.string.osmose_error_description_3250),
        _3260(3260, IcMarker3260, R.string.osmose_error_description_3260),
        _4010(4010, IcMarker4010, R.string.osmose_error_description_4010),
        _4020(4020, IcMarker4020, R.string.osmose_error_description_4020),
        _4030(4030, IcMarker4030, R.string.osmose_error_description_4030),
        _4040(4040, IcMarker4040, R.string.osmose_error_description_4040),
        _4060(4060, IcMarker4060, R.string.osmose_error_description_4060),
        _4061(4061, IcMarker4061, R.string.osmose_error_description_4061),
        _4070(4070, IcMarker4070, R.string.osmose_error_description_4070),
        _4080(4080, IcMarker4080, R.string.osmose_error_description_4080),
        _4090(4090, IcMarker4090, R.string.osmose_error_description_4090),
        _4100(4100, IcMarker4100, R.string.osmose_error_description_4100),
        _4110(4110, IcMarker4110, R.string.osmose_error_description_4110),
        _4130(4130, IcMarker4130, R.string.osmose_error_description_4130),
        _5010(5010, IcMarker5010, R.string.osmose_error_description_5010),
        _5020(5020, IcMarker5020, R.string.osmose_error_description_5020),
        _5030(5030, IcMarker5030, R.string.osmose_error_description_5030),
        _5040(5040, IcMarker5040, R.string.osmose_error_description_5040),
        _5050(5050, IcMarker5050, R.string.osmose_error_description_5050),
        _5060(5060, IcMarker5060, R.string.osmose_error_description_5060),
        _5070(5070, IcMarker5070, R.string.osmose_error_description_5070),
        _5080(5080, IcMarker5080, R.string.osmose_error_description_5080),
        _6010(6010, IcMarker6010, R.string.osmose_error_description_6010),
        _6020(6020, IcMarker6020, R.string.osmose_error_description_6020),
        _6030(6030, IcMarker6030, R.string.osmose_error_description_6030),
        _6040(6040, IcMarker6040, R.string.osmose_error_description_6040),
        _6060(6060, IcMarker6060, R.string.osmose_error_description_6060),
        _6070(6070, IcMarker6070, R.string.osmose_error_description_6070),
        _7010(7010, IcMarker7010, R.string.osmose_error_description_7010),
        _7011(7011, IcMarker7011, R.string.osmose_error_description_7011),
        _7012(7012, IcMarker7012, R.string.osmose_error_description_7012),
        _7020(7020, IcMarker7020, R.string.osmose_error_description_7020),
        _7040(7040, IcMarker7040, R.string.osmose_error_description_7040),
        _7050(7050, IcMarker7050, R.string.osmose_error_description_7050),
        _7051(7051, IcMarker7051, R.string.osmose_error_description_7051),
        _7060(7060, IcMarker7060, R.string.osmose_error_description_7060),
        _7070(7070, IcMarker7070, R.string.osmose_error_description_7070),
        _7080(7080, IcMarker7080, R.string.osmose_error_description_7080),
        _7090(7090, IcMarker7090, R.string.osmose_error_description_7090),
        _7100(7100, IcMarker7100, R.string.osmose_error_description_7100),
        _7110(7110, IcMarker7110, R.string.osmose_error_description_7110),
        _7120(7120, IcMarker7120, R.string.osmose_error_description_7120),
        _7130(7130, IcMarker7130, R.string.osmose_error_description_7130),
        _7140(7140, IcMarker7140, R.string.osmose_error_description_7140),
        _7150(7150, IcMarker7150, R.string.osmose_error_description_7150),
        _7160(7160, IcMarker7160, R.string.osmose_error_description_7160),
        _7170(7170, IcMarker7170, R.string.osmose_error_description_7170),
        _7190(7190, IcMarker7190, R.string.osmose_error_description_7190),
        _7220(7220, IcMarker7220, R.string.osmose_error_description_7220),
        _7240(7240, IcMarker7240, R.string.osmose_error_description_7240),
        _7250(7250, IcMarker7250, R.string.osmose_error_description_7250),
        _8010(8010, IcMarker8010, R.string.osmose_error_description_8010),
        _8011(8011, IcMarker8011, R.string.osmose_error_description_8011),
        _8012(8012, IcMarker8012, R.string.osmose_error_description_8012),
        _8020(8020, IcMarker8020, R.string.osmose_error_description_8020),
        _8021(8021, IcMarker8021, R.string.osmose_error_description_8021),
        _8022(8022, IcMarker8022, R.string.osmose_error_description_8022),
        _8025(8025, IcMarker8025, R.string.osmose_error_description_8025),
        _8026(8026, IcMarker8026, R.string.osmose_error_description_8026),
        _8030(8030, IcMarker8030, R.string.osmose_error_description_8030),
        _8031(8031, IcMarker8031, R.string.osmose_error_description_8031),
        _8032(8032, IcMarker8032, R.string.osmose_error_description_8032),
        _8040(8040, IcMarker8040, R.string.osmose_error_description_8040),
        _8041(8041, IcMarker8041, R.string.osmose_error_description_8041),
        _8042(8042, IcMarker8042, R.string.osmose_error_description_8042),
        _8050(8050, IcMarker8050, R.string.osmose_error_description_8050),
        _8051(8051, IcMarker8051, R.string.osmose_error_description_8051),
        _8060(8060, IcMarker8060, R.string.osmose_error_description_8060),
        _8070(8070, IcMarker8070, R.string.osmose_error_description_8070),
        _8080(8080, IcMarker8080, R.string.osmose_error_description_8080),
        _8110(8110, IcMarker8110, R.string.osmose_error_description_8110),
        _8120(8120, IcMarker8120, R.string.osmose_error_description_8120),
        _8121(8121, IcMarker8121, R.string.osmose_error_description_8121),
        _8122(8122, IcMarker8122, R.string.osmose_error_description_8122),
        _8130(8130, IcMarker8130, R.string.osmose_error_description_8130),
        _8131(8131, IcMarker8131, R.string.osmose_error_description_8131),
        _8132(8132, IcMarker8132, R.string.osmose_error_description_8132),
        _8140(8140, IcMarker8140, R.string.osmose_error_description_8140),
        _8150(8150, IcMarker8150, R.string.osmose_error_description_8150),
        _8160(8160, IcMarker8160, R.string.osmose_error_description_8160),
        _8161(8161, IcMarker8161, R.string.osmose_error_description_8161),
        _8162(8162, IcMarker8162, R.string.osmose_error_description_8162),
        _8170(8170, IcMarker8170, R.string.osmose_error_description_8170),
        _8180(8180, IcMarker8180, R.string.osmose_error_description_8180),
        _8190(8190, IcMarker8190, R.string.osmose_error_description_8190),
        _8191(8191, IcMarker8191, R.string.osmose_error_description_8191),
        _8192(8192, IcMarker8192, R.string.osmose_error_description_8192),
        _8210(8210, IcMarker8210, R.string.osmose_error_description_8210),
        _8200(8200, IcMarker8200, R.string.osmose_error_description_8200),
        _8201(8201, IcMarker8201, R.string.osmose_error_description_8201),
        _8202(8202, IcMarker8202, R.string.osmose_error_description_8202),
        _8211(8211, IcMarker8211, R.string.osmose_error_description_8211),
        _8212(8212, IcMarker8212, R.string.osmose_error_description_8212),
        _8221(8221, IcMarker8221, R.string.osmose_error_description_8221),
        _8230(8230, IcMarker8230, R.string.osmose_error_description_8230),
        _8240(8240, IcMarker8240, R.string.osmose_error_description_8240),
        _8250(8250, IcMarker8250, R.string.osmose_error_description_8250),
        _8260(8260, IcMarker8260, R.string.osmose_error_description_8260),
        _8270(8270, IcMarker8270, R.string.osmose_error_description_8270),
        _8280(8280, IcMarker8280, R.string.osmose_error_description_8280),
        _8281(8281, IcMarker8281, R.string.osmose_error_description_8281),
        _8282(8282, IcMarker8282, R.string.osmose_error_description_8282),
        _8290(8290, IcMarker8290, R.string.osmose_error_description_8290),
        _8300(8300, IcMarker8300, R.string.osmose_error_description_8300),
        _8310(8310, IcMarker8310, R.string.osmose_error_description_8310),
        _8320(8320, IcMarker8320, R.string.osmose_error_description_8320),
        _8330(8330, IcMarker8330, R.string.osmose_error_description_8330),
        _8331(8331, IcMarker8331, R.string.osmose_error_description_8331),
        _8340(8340, IcMarker8340, R.string.osmose_error_description_8340),
        _8341(8341, IcMarker8341, R.string.osmose_error_description_8341),
        _8350(8350, IcMarker8350, R.string.osmose_error_description_8350),
        _8351(8351, IcMarker8351, R.string.osmose_error_description_8351),
        _8360(8360, IcMarker8360, R.string.osmose_error_description_8360),
        _8370(8370, IcMarker8370, R.string.osmose_error_description_8370),
        _8380(8380, IcMarker8380, R.string.osmose_error_description_8380),
        _8381(8381, IcMarker8381, R.string.osmose_error_description_8381),
        _8382(8382, IcMarker8382, R.string.osmose_error_description_8382),
        _8390(8390, IcMarker8390, R.string.osmose_error_description_8390),
        _8391(8391, IcMarker8391, R.string.osmose_error_description_8391),
        _8392(8392, IcMarker8392, R.string.osmose_error_description_8392),
        _8410(8410, IcMarker8410, R.string.osmose_error_description_8410),
        _8411(8411, IcMarker8411, R.string.osmose_error_description_8411),
        _8412(8412, IcMarker8412, R.string.osmose_error_description_8412),
        _9000(9000, IcMarker9000, R.string.osmose_error_description_9000),
        _9001(9001, IcMarker9001, R.string.osmose_error_description_9001),
        _9002(9002, IcMarker9002, R.string.osmose_error_description_9002),
        _9003(9003, IcMarker9003, R.string.osmose_error_description_9003),
        _9004(9004, IcMarker9004, R.string.osmose_error_description_9004),
        _9005(9005, IcMarker9005, R.string.osmose_error_description_9005),
        _9006(9006, IcMarker9006, R.string.osmose_error_description_9006),
        _9007(9007, IcMarker9007, R.string.osmose_error_description_9007),
        _9009(9009, IcMarker9009, R.string.osmose_error_description_9009),
        _9010(9010, IcMarker9010, R.string.osmose_error_description_9010),
        _9011(9011, IcMarker9011, R.string.osmose_error_description_9011),
        _9014(9014, IcMarker9014, R.string.osmose_error_description_9014),
        _9015(9015, IcMarker9015, R.string.osmose_error_description_9015),
        _9016(9016, IcMarker9016, R.string.osmose_error_description_9016),
        _9017(9017, IcMarker9017, R.string.osmose_error_description_9017),
        _9018(9018, IcMarker9018, R.string.osmose_error_description_9018),
        _9019(9019, IcMarker9019, R.string.osmose_error_description_9019),
        _9100(9100, IcMarker9100, R.string.osmose_error_description_9100),
    }

    companion object {
        lateinit var IcToggleLayer: Drawable
        lateinit var IcToggleLayerDisabled: Drawable

        private lateinit var IcMarker0: Drawable
        private lateinit var IcMarker1: Drawable
        private lateinit var IcMarker1010: Drawable
        private lateinit var IcMarker1040: Drawable
        private lateinit var IcMarker1050: Drawable
        private lateinit var IcMarker1060: Drawable
        private lateinit var IcMarker1070: Drawable
        private lateinit var IcMarker1080: Drawable
        private lateinit var IcMarker1090: Drawable
        private lateinit var IcMarker1100: Drawable
        private lateinit var IcMarker1110: Drawable
        private lateinit var IcMarker1120: Drawable
        private lateinit var IcMarker1140: Drawable
        private lateinit var IcMarker1150: Drawable
        private lateinit var IcMarker1160: Drawable
        private lateinit var IcMarker1170: Drawable
        private lateinit var IcMarker1180: Drawable
        private lateinit var IcMarker1190: Drawable
        private lateinit var IcMarker1200: Drawable
        private lateinit var IcMarker1210: Drawable
        private lateinit var IcMarker1220: Drawable
        private lateinit var IcMarker1221: Drawable
        private lateinit var IcMarker1230: Drawable
        private lateinit var IcMarker1240: Drawable
        private lateinit var IcMarker1250: Drawable
        private lateinit var IcMarker1260: Drawable
        private lateinit var IcMarker1270: Drawable
        private lateinit var IcMarker1280: Drawable
        private lateinit var IcMarker1290: Drawable
        private lateinit var IcMarker2010: Drawable
        private lateinit var IcMarker2020: Drawable
        private lateinit var IcMarker2030: Drawable
        private lateinit var IcMarker2040: Drawable
        private lateinit var IcMarker2060: Drawable
        private lateinit var IcMarker2080: Drawable
        private lateinit var IcMarker2090: Drawable
        private lateinit var IcMarker2100: Drawable
        private lateinit var IcMarker2110: Drawable
        private lateinit var IcMarker2120: Drawable
        private lateinit var IcMarker2130: Drawable
        private lateinit var IcMarker2140: Drawable
        private lateinit var IcMarker2150: Drawable
        private lateinit var IcMarker2160: Drawable
        private lateinit var IcMarker3010: Drawable
        private lateinit var IcMarker3020: Drawable
        private lateinit var IcMarker3030: Drawable
        private lateinit var IcMarker3031: Drawable
        private lateinit var IcMarker3032: Drawable
        private lateinit var IcMarker3033: Drawable
        private lateinit var IcMarker3040: Drawable
        private lateinit var IcMarker3050: Drawable
        private lateinit var IcMarker3060: Drawable
        private lateinit var IcMarker3070: Drawable
        private lateinit var IcMarker3080: Drawable
        private lateinit var IcMarker3090: Drawable
        private lateinit var IcMarker3091: Drawable
        private lateinit var IcMarker3092: Drawable
        private lateinit var IcMarker3093: Drawable
        private lateinit var IcMarker3110: Drawable
        private lateinit var IcMarker3120: Drawable
        private lateinit var IcMarker3150: Drawable
        private lateinit var IcMarker3160: Drawable
        private lateinit var IcMarker3161: Drawable
        private lateinit var IcMarker3170: Drawable
        private lateinit var IcMarker3180: Drawable
        private lateinit var IcMarker3190: Drawable
        private lateinit var IcMarker3200: Drawable
        private lateinit var IcMarker3210: Drawable
        private lateinit var IcMarker3220: Drawable
        private lateinit var IcMarker3230: Drawable
        private lateinit var IcMarker3240: Drawable
        private lateinit var IcMarker3250: Drawable
        private lateinit var IcMarker3260: Drawable
        private lateinit var IcMarker4010: Drawable
        private lateinit var IcMarker4020: Drawable
        private lateinit var IcMarker4030: Drawable
        private lateinit var IcMarker4040: Drawable
        private lateinit var IcMarker4060: Drawable
        private lateinit var IcMarker4061: Drawable
        private lateinit var IcMarker4070: Drawable
        private lateinit var IcMarker4080: Drawable
        private lateinit var IcMarker4090: Drawable
        private lateinit var IcMarker4100: Drawable
        private lateinit var IcMarker4110: Drawable
        private lateinit var IcMarker4130: Drawable
        private lateinit var IcMarker5010: Drawable
        private lateinit var IcMarker5020: Drawable
        private lateinit var IcMarker5030: Drawable
        private lateinit var IcMarker5040: Drawable
        private lateinit var IcMarker5050: Drawable
        private lateinit var IcMarker5060: Drawable
        private lateinit var IcMarker5070: Drawable
        private lateinit var IcMarker5080: Drawable
        private lateinit var IcMarker6010: Drawable
        private lateinit var IcMarker6020: Drawable
        private lateinit var IcMarker6030: Drawable
        private lateinit var IcMarker6040: Drawable
        private lateinit var IcMarker6060: Drawable
        private lateinit var IcMarker6070: Drawable
        private lateinit var IcMarker7010: Drawable
        private lateinit var IcMarker7011: Drawable
        private lateinit var IcMarker7012: Drawable
        private lateinit var IcMarker7020: Drawable
        private lateinit var IcMarker7040: Drawable
        private lateinit var IcMarker7050: Drawable
        private lateinit var IcMarker7051: Drawable
        private lateinit var IcMarker7060: Drawable
        private lateinit var IcMarker7070: Drawable
        private lateinit var IcMarker7080: Drawable
        private lateinit var IcMarker7090: Drawable
        private lateinit var IcMarker7100: Drawable
        private lateinit var IcMarker7110: Drawable
        private lateinit var IcMarker7120: Drawable
        private lateinit var IcMarker7130: Drawable
        private lateinit var IcMarker7140: Drawable
        private lateinit var IcMarker7150: Drawable
        private lateinit var IcMarker7160: Drawable
        private lateinit var IcMarker7170: Drawable
        private lateinit var IcMarker7190: Drawable
        private lateinit var IcMarker7220: Drawable
        private lateinit var IcMarker7240: Drawable
        private lateinit var IcMarker7250: Drawable
        private lateinit var IcMarker8010: Drawable
        private lateinit var IcMarker8011: Drawable
        private lateinit var IcMarker8012: Drawable
        private lateinit var IcMarker8020: Drawable
        private lateinit var IcMarker8021: Drawable
        private lateinit var IcMarker8022: Drawable
        private lateinit var IcMarker8025: Drawable
        private lateinit var IcMarker8026: Drawable
        private lateinit var IcMarker8030: Drawable
        private lateinit var IcMarker8031: Drawable
        private lateinit var IcMarker8032: Drawable
        private lateinit var IcMarker8040: Drawable
        private lateinit var IcMarker8041: Drawable
        private lateinit var IcMarker8042: Drawable
        private lateinit var IcMarker8050: Drawable
        private lateinit var IcMarker8051: Drawable
        private lateinit var IcMarker8060: Drawable
        private lateinit var IcMarker8070: Drawable
        private lateinit var IcMarker8080: Drawable
        private lateinit var IcMarker8110: Drawable
        private lateinit var IcMarker8120: Drawable
        private lateinit var IcMarker8121: Drawable
        private lateinit var IcMarker8122: Drawable
        private lateinit var IcMarker8130: Drawable
        private lateinit var IcMarker8131: Drawable
        private lateinit var IcMarker8132: Drawable
        private lateinit var IcMarker8140: Drawable
        private lateinit var IcMarker8150: Drawable
        private lateinit var IcMarker8160: Drawable
        private lateinit var IcMarker8161: Drawable
        private lateinit var IcMarker8162: Drawable
        private lateinit var IcMarker8170: Drawable
        private lateinit var IcMarker8180: Drawable
        private lateinit var IcMarker8190: Drawable
        private lateinit var IcMarker8191: Drawable
        private lateinit var IcMarker8192: Drawable
        private lateinit var IcMarker8210: Drawable
        private lateinit var IcMarker8200: Drawable
        private lateinit var IcMarker8201: Drawable
        private lateinit var IcMarker8202: Drawable
        private lateinit var IcMarker8211: Drawable
        private lateinit var IcMarker8212: Drawable
        private lateinit var IcMarker8221: Drawable
        private lateinit var IcMarker8230: Drawable
        private lateinit var IcMarker8240: Drawable
        private lateinit var IcMarker8250: Drawable
        private lateinit var IcMarker8260: Drawable
        private lateinit var IcMarker8270: Drawable
        private lateinit var IcMarker8280: Drawable
        private lateinit var IcMarker8281: Drawable
        private lateinit var IcMarker8282: Drawable
        private lateinit var IcMarker8290: Drawable
        private lateinit var IcMarker8300: Drawable
        private lateinit var IcMarker8310: Drawable
        private lateinit var IcMarker8320: Drawable
        private lateinit var IcMarker8330: Drawable
        private lateinit var IcMarker8331: Drawable
        private lateinit var IcMarker8340: Drawable
        private lateinit var IcMarker8341: Drawable
        private lateinit var IcMarker8350: Drawable
        private lateinit var IcMarker8351: Drawable
        private lateinit var IcMarker8360: Drawable
        private lateinit var IcMarker8370: Drawable
        private lateinit var IcMarker8380: Drawable
        private lateinit var IcMarker8381: Drawable
        private lateinit var IcMarker8382: Drawable
        private lateinit var IcMarker8390: Drawable
        private lateinit var IcMarker8391: Drawable
        private lateinit var IcMarker8392: Drawable
        private lateinit var IcMarker8410: Drawable
        private lateinit var IcMarker8411: Drawable
        private lateinit var IcMarker8412: Drawable
        private lateinit var IcMarker9000: Drawable
        private lateinit var IcMarker9001: Drawable
        private lateinit var IcMarker9002: Drawable
        private lateinit var IcMarker9003: Drawable
        private lateinit var IcMarker9004: Drawable
        private lateinit var IcMarker9005: Drawable
        private lateinit var IcMarker9006: Drawable
        private lateinit var IcMarker9007: Drawable
        private lateinit var IcMarker9009: Drawable
        private lateinit var IcMarker9010: Drawable
        private lateinit var IcMarker9011: Drawable
        private lateinit var IcMarker9014: Drawable
        private lateinit var IcMarker9015: Drawable
        private lateinit var IcMarker9016: Drawable
        private lateinit var IcMarker9017: Drawable
        private lateinit var IcMarker9018: Drawable
        private lateinit var IcMarker9019: Drawable
        private lateinit var IcMarker9100: Drawable

        fun Init() {
            IcToggleLayer = Images.GetDrawable(R.drawable.ic_toggle_osmose_layer)
            IcToggleLayerDisabled = Images.GetDrawable(R.drawable.ic_toggle_osmose_layer_disabled)

            IcMarker0 = Images.GetDrawable(R.drawable.osmose_marker_b_0)
            IcMarker1 = Images.GetDrawable(R.drawable.osmose_marker_b_1)
            IcMarker1010 = Images.GetDrawable(R.drawable.osmose_marker_b_1010)
            IcMarker1040 = Images.GetDrawable(R.drawable.osmose_marker_b_1040)
            IcMarker1050 = Images.GetDrawable(R.drawable.osmose_marker_b_1050)
            IcMarker1060 = Images.GetDrawable(R.drawable.osmose_marker_b_1060)
            IcMarker1070 = Images.GetDrawable(R.drawable.osmose_marker_b_1070)
            IcMarker1080 = Images.GetDrawable(R.drawable.osmose_marker_b_1080)
            IcMarker1090 = Images.GetDrawable(R.drawable.osmose_marker_b_1090)
            IcMarker1100 = Images.GetDrawable(R.drawable.osmose_marker_b_1100)
            IcMarker1110 = Images.GetDrawable(R.drawable.osmose_marker_b_1110)
            IcMarker1120 = Images.GetDrawable(R.drawable.osmose_marker_b_1120)
            IcMarker1140 = Images.GetDrawable(R.drawable.osmose_marker_b_1140)
            IcMarker1150 = Images.GetDrawable(R.drawable.osmose_marker_b_1150)
            IcMarker1160 = Images.GetDrawable(R.drawable.osmose_marker_b_1160)
            IcMarker1170 = Images.GetDrawable(R.drawable.osmose_marker_b_1170)
            IcMarker1180 = Images.GetDrawable(R.drawable.osmose_marker_b_1180)
            IcMarker1190 = Images.GetDrawable(R.drawable.osmose_marker_b_1190)
            IcMarker1200 = Images.GetDrawable(R.drawable.osmose_marker_b_1200)
            IcMarker1210 = Images.GetDrawable(R.drawable.osmose_marker_b_1210)
            IcMarker1220 = Images.GetDrawable(R.drawable.osmose_marker_b_1220)
            IcMarker1221 = Images.GetDrawable(R.drawable.osmose_marker_b_1221)
            IcMarker1230 = Images.GetDrawable(R.drawable.osmose_marker_b_1230)
            IcMarker1240 = Images.GetDrawable(R.drawable.osmose_marker_b_1240)
            IcMarker1250 = Images.GetDrawable(R.drawable.osmose_marker_b_1250)
            IcMarker1260 = Images.GetDrawable(R.drawable.osmose_marker_b_1260)
            IcMarker1270 = Images.GetDrawable(R.drawable.osmose_marker_b_1270)
            IcMarker1280 = Images.GetDrawable(R.drawable.osmose_marker_b_1280)
            IcMarker1290 = Images.GetDrawable(R.drawable.osmose_marker_b_1290)
            IcMarker2010 = Images.GetDrawable(R.drawable.osmose_marker_b_2010)
            IcMarker2020 = Images.GetDrawable(R.drawable.osmose_marker_b_2020)
            IcMarker2030 = Images.GetDrawable(R.drawable.osmose_marker_b_2030)
            IcMarker2040 = Images.GetDrawable(R.drawable.osmose_marker_b_2040)
            IcMarker2060 = Images.GetDrawable(R.drawable.osmose_marker_b_2060)
            IcMarker2080 = Images.GetDrawable(R.drawable.osmose_marker_b_2080)
            IcMarker2090 = Images.GetDrawable(R.drawable.osmose_marker_b_2090)
            IcMarker2100 = Images.GetDrawable(R.drawable.osmose_marker_b_2100)
            IcMarker2110 = Images.GetDrawable(R.drawable.osmose_marker_b_2110)
            IcMarker2120 = Images.GetDrawable(R.drawable.osmose_marker_b_2120)
            IcMarker2130 = Images.GetDrawable(R.drawable.osmose_marker_b_2130)
            IcMarker2140 = Images.GetDrawable(R.drawable.osmose_marker_b_2140)
            IcMarker2150 = Images.GetDrawable(R.drawable.osmose_marker_b_2150)
            IcMarker2160 = Images.GetDrawable(R.drawable.osmose_marker_b_2160)
            IcMarker3010 = Images.GetDrawable(R.drawable.osmose_marker_b_3010)
            IcMarker3020 = Images.GetDrawable(R.drawable.osmose_marker_b_3020)
            IcMarker3030 = Images.GetDrawable(R.drawable.osmose_marker_b_3030)
            IcMarker3031 = Images.GetDrawable(R.drawable.osmose_marker_b_3031)
            IcMarker3032 = Images.GetDrawable(R.drawable.osmose_marker_b_3032)
            IcMarker3033 = Images.GetDrawable(R.drawable.osmose_marker_b_3033)
            IcMarker3040 = Images.GetDrawable(R.drawable.osmose_marker_b_3040)
            IcMarker3050 = Images.GetDrawable(R.drawable.osmose_marker_b_3050)
            IcMarker3060 = Images.GetDrawable(R.drawable.osmose_marker_b_3060)
            IcMarker3070 = Images.GetDrawable(R.drawable.osmose_marker_b_3070)
            IcMarker3080 = Images.GetDrawable(R.drawable.osmose_marker_b_3080)
            IcMarker3090 = Images.GetDrawable(R.drawable.osmose_marker_b_3090)
            IcMarker3091 = Images.GetDrawable(R.drawable.osmose_marker_b_3091)
            IcMarker3092 = Images.GetDrawable(R.drawable.osmose_marker_b_3092)
            IcMarker3093 = Images.GetDrawable(R.drawable.osmose_marker_b_3093)
            IcMarker3110 = Images.GetDrawable(R.drawable.osmose_marker_b_3110)
            IcMarker3120 = Images.GetDrawable(R.drawable.osmose_marker_b_3120)
            IcMarker3150 = Images.GetDrawable(R.drawable.osmose_marker_b_3150)
            IcMarker3160 = Images.GetDrawable(R.drawable.osmose_marker_b_3160)
            IcMarker3161 = Images.GetDrawable(R.drawable.osmose_marker_b_3161)
            IcMarker3170 = Images.GetDrawable(R.drawable.osmose_marker_b_3170)
            IcMarker3180 = Images.GetDrawable(R.drawable.osmose_marker_b_3180)
            IcMarker3190 = Images.GetDrawable(R.drawable.osmose_marker_b_3190)
            IcMarker3200 = Images.GetDrawable(R.drawable.osmose_marker_b_3200)
            IcMarker3210 = Images.GetDrawable(R.drawable.osmose_marker_b_3210)
            IcMarker3220 = Images.GetDrawable(R.drawable.osmose_marker_b_3220)
            IcMarker3230 = Images.GetDrawable(R.drawable.osmose_marker_b_3230)
            IcMarker3240 = Images.GetDrawable(R.drawable.osmose_marker_b_3240)
            IcMarker3250 = Images.GetDrawable(R.drawable.osmose_marker_b_3250)
            IcMarker3260 = Images.GetDrawable(R.drawable.osmose_marker_b_3260)
            IcMarker4010 = Images.GetDrawable(R.drawable.osmose_marker_b_4010)
            IcMarker4020 = Images.GetDrawable(R.drawable.osmose_marker_b_4020)
            IcMarker4030 = Images.GetDrawable(R.drawable.osmose_marker_b_4030)
            IcMarker4040 = Images.GetDrawable(R.drawable.osmose_marker_b_4040)
            IcMarker4060 = Images.GetDrawable(R.drawable.osmose_marker_b_4060)
            IcMarker4061 = Images.GetDrawable(R.drawable.osmose_marker_b_4061)
            IcMarker4070 = Images.GetDrawable(R.drawable.osmose_marker_b_4070)
            IcMarker4080 = Images.GetDrawable(R.drawable.osmose_marker_b_4080)
            IcMarker4090 = Images.GetDrawable(R.drawable.osmose_marker_b_4090)
            IcMarker4100 = Images.GetDrawable(R.drawable.osmose_marker_b_4100)
            IcMarker4110 = Images.GetDrawable(R.drawable.osmose_marker_b_4110)
            IcMarker4130 = Images.GetDrawable(R.drawable.osmose_marker_b_4130)
            IcMarker5010 = Images.GetDrawable(R.drawable.osmose_marker_b_5010)
            IcMarker5020 = Images.GetDrawable(R.drawable.osmose_marker_b_5020)
            IcMarker5030 = Images.GetDrawable(R.drawable.osmose_marker_b_5030)
            IcMarker5040 = Images.GetDrawable(R.drawable.osmose_marker_b_5040)
            IcMarker5050 = Images.GetDrawable(R.drawable.osmose_marker_b_5050)
            IcMarker5060 = Images.GetDrawable(R.drawable.osmose_marker_b_5060)
            IcMarker5070 = Images.GetDrawable(R.drawable.osmose_marker_b_5070)
            IcMarker5080 = Images.GetDrawable(R.drawable.osmose_marker_b_5080)
            IcMarker6010 = Images.GetDrawable(R.drawable.osmose_marker_b_6010)
            IcMarker6020 = Images.GetDrawable(R.drawable.osmose_marker_b_6020)
            IcMarker6030 = Images.GetDrawable(R.drawable.osmose_marker_b_6030)
            IcMarker6040 = Images.GetDrawable(R.drawable.osmose_marker_b_6040)
            IcMarker6060 = Images.GetDrawable(R.drawable.osmose_marker_b_6060)
            IcMarker6070 = Images.GetDrawable(R.drawable.osmose_marker_b_6070)
            IcMarker7010 = Images.GetDrawable(R.drawable.osmose_marker_b_7010)
            IcMarker7011 = Images.GetDrawable(R.drawable.osmose_marker_b_7011)
            IcMarker7012 = Images.GetDrawable(R.drawable.osmose_marker_b_7012)
            IcMarker7020 = Images.GetDrawable(R.drawable.osmose_marker_b_7020)
            IcMarker7040 = Images.GetDrawable(R.drawable.osmose_marker_b_7040)
            IcMarker7050 = Images.GetDrawable(R.drawable.osmose_marker_b_7050)
            IcMarker7051 = Images.GetDrawable(R.drawable.osmose_marker_b_7051)
            IcMarker7060 = Images.GetDrawable(R.drawable.osmose_marker_b_7060)
            IcMarker7070 = Images.GetDrawable(R.drawable.osmose_marker_b_7070)
            IcMarker7080 = Images.GetDrawable(R.drawable.osmose_marker_b_7080)
            IcMarker7090 = Images.GetDrawable(R.drawable.osmose_marker_b_7090)
            IcMarker7100 = Images.GetDrawable(R.drawable.osmose_marker_b_7100)
            IcMarker7110 = Images.GetDrawable(R.drawable.osmose_marker_b_7110)
            IcMarker7120 = Images.GetDrawable(R.drawable.osmose_marker_b_7120)
            IcMarker7130 = Images.GetDrawable(R.drawable.osmose_marker_b_7130)
            IcMarker7140 = Images.GetDrawable(R.drawable.osmose_marker_b_7140)
            IcMarker7150 = Images.GetDrawable(R.drawable.osmose_marker_b_7150)
            IcMarker7160 = Images.GetDrawable(R.drawable.osmose_marker_b_7160)
            IcMarker7170 = Images.GetDrawable(R.drawable.osmose_marker_b_7170)
            IcMarker7190 = Images.GetDrawable(R.drawable.osmose_marker_b_7190)
            IcMarker7220 = Images.GetDrawable(R.drawable.osmose_marker_b_7220)
            IcMarker7240 = Images.GetDrawable(R.drawable.osmose_marker_b_7240)
            IcMarker7250 = Images.GetDrawable(R.drawable.osmose_marker_b_7250)
            IcMarker8010 = Images.GetDrawable(R.drawable.osmose_marker_b_8010)
            IcMarker8011 = Images.GetDrawable(R.drawable.osmose_marker_b_8011)
            IcMarker8012 = Images.GetDrawable(R.drawable.osmose_marker_b_8012)
            IcMarker8020 = Images.GetDrawable(R.drawable.osmose_marker_b_8020)
            IcMarker8021 = Images.GetDrawable(R.drawable.osmose_marker_b_8021)
            IcMarker8022 = Images.GetDrawable(R.drawable.osmose_marker_b_8022)
            IcMarker8025 = Images.GetDrawable(R.drawable.osmose_marker_b_8025)
            IcMarker8026 = Images.GetDrawable(R.drawable.osmose_marker_b_8026)
            IcMarker8030 = Images.GetDrawable(R.drawable.osmose_marker_b_8030)
            IcMarker8031 = Images.GetDrawable(R.drawable.osmose_marker_b_8031)
            IcMarker8032 = Images.GetDrawable(R.drawable.osmose_marker_b_8032)
            IcMarker8040 = Images.GetDrawable(R.drawable.osmose_marker_b_8040)
            IcMarker8041 = Images.GetDrawable(R.drawable.osmose_marker_b_8041)
            IcMarker8042 = Images.GetDrawable(R.drawable.osmose_marker_b_8042)
            IcMarker8050 = Images.GetDrawable(R.drawable.osmose_marker_b_8050)
            IcMarker8051 = Images.GetDrawable(R.drawable.osmose_marker_b_8051)
            IcMarker8060 = Images.GetDrawable(R.drawable.osmose_marker_b_8060)
            IcMarker8070 = Images.GetDrawable(R.drawable.osmose_marker_b_8070)
            IcMarker8080 = Images.GetDrawable(R.drawable.osmose_marker_b_8080)
            IcMarker8110 = Images.GetDrawable(R.drawable.osmose_marker_b_8110)
            IcMarker8120 = Images.GetDrawable(R.drawable.osmose_marker_b_8120)
            IcMarker8121 = Images.GetDrawable(R.drawable.osmose_marker_b_8121)
            IcMarker8122 = Images.GetDrawable(R.drawable.osmose_marker_b_8122)
            IcMarker8130 = Images.GetDrawable(R.drawable.osmose_marker_b_8130)
            IcMarker8131 = Images.GetDrawable(R.drawable.osmose_marker_b_8131)
            IcMarker8132 = Images.GetDrawable(R.drawable.osmose_marker_b_8132)
            IcMarker8140 = Images.GetDrawable(R.drawable.osmose_marker_b_8140)
            IcMarker8150 = Images.GetDrawable(R.drawable.osmose_marker_b_8150)
            IcMarker8160 = Images.GetDrawable(R.drawable.osmose_marker_b_8160)
            IcMarker8161 = Images.GetDrawable(R.drawable.osmose_marker_b_8161)
            IcMarker8162 = Images.GetDrawable(R.drawable.osmose_marker_b_8162)
            IcMarker8170 = Images.GetDrawable(R.drawable.osmose_marker_b_8170)
            IcMarker8180 = Images.GetDrawable(R.drawable.osmose_marker_b_8180)
            IcMarker8190 = Images.GetDrawable(R.drawable.osmose_marker_b_8190)
            IcMarker8191 = Images.GetDrawable(R.drawable.osmose_marker_b_8191)
            IcMarker8192 = Images.GetDrawable(R.drawable.osmose_marker_b_8192)
            IcMarker8200 = Images.GetDrawable(R.drawable.osmose_marker_b_8200)
            IcMarker8201 = Images.GetDrawable(R.drawable.osmose_marker_b_8201)
            IcMarker8202 = Images.GetDrawable(R.drawable.osmose_marker_b_8202)
            IcMarker8210 = Images.GetDrawable(R.drawable.osmose_marker_b_8210)
            IcMarker8211 = Images.GetDrawable(R.drawable.osmose_marker_b_8211)
            IcMarker8212 = Images.GetDrawable(R.drawable.osmose_marker_b_8212)
            IcMarker8221 = Images.GetDrawable(R.drawable.osmose_marker_b_8221)
            IcMarker8230 = Images.GetDrawable(R.drawable.osmose_marker_b_8230)
            IcMarker8240 = Images.GetDrawable(R.drawable.osmose_marker_b_8240)
            IcMarker8250 = Images.GetDrawable(R.drawable.osmose_marker_b_8250)
            IcMarker8260 = Images.GetDrawable(R.drawable.osmose_marker_b_8260)
            IcMarker8270 = Images.GetDrawable(R.drawable.osmose_marker_b_8270)
            IcMarker8280 = Images.GetDrawable(R.drawable.osmose_marker_b_8280)
            IcMarker8281 = Images.GetDrawable(R.drawable.osmose_marker_b_8281)
            IcMarker8282 = Images.GetDrawable(R.drawable.osmose_marker_b_8282)
            IcMarker8290 = Images.GetDrawable(R.drawable.osmose_marker_b_8290)
            IcMarker8300 = Images.GetDrawable(R.drawable.osmose_marker_b_8300)
            IcMarker8310 = Images.GetDrawable(R.drawable.osmose_marker_b_8310)
            IcMarker8320 = Images.GetDrawable(R.drawable.osmose_marker_b_8320)
            IcMarker8330 = Images.GetDrawable(R.drawable.osmose_marker_b_8330)
            IcMarker8331 = Images.GetDrawable(R.drawable.osmose_marker_b_8331)
            IcMarker8340 = Images.GetDrawable(R.drawable.osmose_marker_b_8340)
            IcMarker8341 = Images.GetDrawable(R.drawable.osmose_marker_b_8341)
            IcMarker8350 = Images.GetDrawable(R.drawable.osmose_marker_b_8350)
            IcMarker8351 = Images.GetDrawable(R.drawable.osmose_marker_b_8351)
            IcMarker8360 = Images.GetDrawable(R.drawable.osmose_marker_b_8360)
            IcMarker8370 = Images.GetDrawable(R.drawable.osmose_marker_b_8370)
            IcMarker8380 = Images.GetDrawable(R.drawable.osmose_marker_b_8380)
            IcMarker8381 = Images.GetDrawable(R.drawable.osmose_marker_b_8381)
            IcMarker8382 = Images.GetDrawable(R.drawable.osmose_marker_b_8382)
            IcMarker8390 = Images.GetDrawable(R.drawable.osmose_marker_b_8390)
            IcMarker8391 = Images.GetDrawable(R.drawable.osmose_marker_b_8391)
            IcMarker8392 = Images.GetDrawable(R.drawable.osmose_marker_b_8392)
            IcMarker8410 = Images.GetDrawable(R.drawable.osmose_marker_b_8410)
            IcMarker8411 = Images.GetDrawable(R.drawable.osmose_marker_b_8411)
            IcMarker8412 = Images.GetDrawable(R.drawable.osmose_marker_b_8412)
            IcMarker9000 = Images.GetDrawable(R.drawable.osmose_marker_b_9000)
            IcMarker9001 = Images.GetDrawable(R.drawable.osmose_marker_b_9001)
            IcMarker9002 = Images.GetDrawable(R.drawable.osmose_marker_b_9002)
            IcMarker9003 = Images.GetDrawable(R.drawable.osmose_marker_b_9003)
            IcMarker9004 = Images.GetDrawable(R.drawable.osmose_marker_b_9004)
            IcMarker9005 = Images.GetDrawable(R.drawable.osmose_marker_b_9005)
            IcMarker9006 = Images.GetDrawable(R.drawable.osmose_marker_b_9006)
            IcMarker9007 = Images.GetDrawable(R.drawable.osmose_marker_b_9007)
            IcMarker9009 = Images.GetDrawable(R.drawable.osmose_marker_b_9009)
            IcMarker9010 = Images.GetDrawable(R.drawable.osmose_marker_b_9010)
            IcMarker9011 = Images.GetDrawable(R.drawable.osmose_marker_b_9011)
            IcMarker9014 = Images.GetDrawable(R.drawable.osmose_marker_b_9014)
            IcMarker9015 = Images.GetDrawable(R.drawable.osmose_marker_b_9015)
            IcMarker9016 = Images.GetDrawable(R.drawable.osmose_marker_b_9016)
            IcMarker9017 = Images.GetDrawable(R.drawable.osmose_marker_b_9017)
            IcMarker9018 = Images.GetDrawable(R.drawable.osmose_marker_b_9018)
            IcMarker9019 = Images.GetDrawable(R.drawable.osmose_marker_b_9019)
            IcMarker9100 = Images.GetDrawable(R.drawable.osmose_marker_b_9100)
        }

        fun GetTypeByItemNumber(item: Int): ERROR_TYPE? {
            return ERROR_TYPE.values().find {
                item == it.Item
            }
        }
    }
}