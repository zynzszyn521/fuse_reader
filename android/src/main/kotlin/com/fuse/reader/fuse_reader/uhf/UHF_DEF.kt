package com.cmcid.myreader.uhf

object UHF_DEF {
    const val MSG_INVTAG = 1
    const val MSG_INVBEGIN = 2
    const val MSG_INVEND = 3
    const val MSG_UPDATEINFO = 4
    const val MSG_STAT = 10
    const val MSG_MSG = 11
    const val HOST_REG_REQ_ACCESS_READ: Byte = 0
    const val HOST_REG_REQ_ACCESS_WRITE: Byte = 1
    const val PACKAGE_SIZE = 8
    const val COMMAND_BEGIN_SIZE = 8
    const val COMMAND_END_SIZE = 8
    const val COMMAND_INV_SIZE = 12
    const val COMMAND_ACC_SIZE = 16
    const val BYTES_PER_LEN_UNIT = 4 // see RFID_PACKET_COMMON declaration
    const val CUSTOM_TAB = 16 // custom tab for showing cleaner printf info
    fun RFID_18K6C_INV_CRC_IS_INVALID(f: Int): Boolean {
        return f and 0x01 > 0
    }

    fun RFID_18K6C_INV_CRC_IS_VALID(f: Int): Boolean {
        return !RFID_18K6C_INV_CRC_IS_INVALID(f)
    }

    fun RFID_18K6C_INVENTORY_CRC_IS_INVALID(f: Int): Boolean {
        return f and 0x01 > 0
    }

    fun RFID_18K6C_INVENTORY_CRC_IS_VALID(f: Int): Boolean {
        return !RFID_18K6C_INVENTORY_CRC_IS_INVALID(f)
    }

    fun RFID_18K6C_TAG_ACCESS_MAC_ERROR(f: Int): Boolean {
        return f and 0x01 > 0
    }

    fun RFID_18K6C_TAG_ACCESS_BACKSCATTER_ERROR(f: Int): Boolean {
        return f and 0x02 > 0
    }

    fun RFID_18K6C_TAG_ACCESS_ANY_ERROR(f: Int): Boolean {
        return RFID_18K6C_TAG_ACCESS_MAC_ERROR(f) ||
                RFID_18K6C_TAG_ACCESS_BACKSCATTER_ERROR(f)
    }

    fun RFID_18K6C_TAG_ACCESS_PADDING_BYTES(f: Int): Int {
        return f shr 6 and 0x3
    }

    fun CREATE_MASK(si: Int): Int {
        return -0x1 shr 32 - si
    }

    fun BIT_FIELD_CREATE(va: Int, si: Int, sh: Int): Int {
        return (va and CREATE_MASK(si) shl sh)
    }

    const val HST_TAGWRDAT_N_DATA_SIZE = 16
    const val HST_TAGWRDAT_N_OFF_SIZE = 16
    const val HST_TAGWRDAT_N_DATA1_SIZE = 16
    const val HST_TAGWRDAT_N_DATA0_SIZE = 16

    /* The number of bits that fields are left-shifted in the HST_TAGWRDAT_N    */ /* register.                                                                */
    const val HST_TAGWRDAT_N_DATA_SHIFT = 0
    const val HST_TAGWRDAT_N_OFF_SHIFT = HST_TAGWRDAT_N_DATA_SHIFT + HST_TAGWRDAT_N_DATA_SIZE
    fun HST_TAGWRDAT_N_DATA(va: Int): Int {
        return BIT_FIELD_CREATE(va, HST_TAGWRDAT_N_DATA_SIZE, HST_TAGWRDAT_N_DATA_SHIFT)
    }

    fun HST_TAGWRDAT_N_OFF(va: Int): Int {
        return BIT_FIELD_CREATE(va, HST_TAGWRDAT_N_OFF_SIZE, HST_TAGWRDAT_N_OFF_SHIFT)
    }

    const val CMD_18K6CINV: Short = 0x0F // ISO 18000-6C Inventory
    const val CMD_18K6CREAD: Short = 0x10 // ISO 18000-6C Read
    const val CMD_18K6CWRITE: Short = 0x11 // ISO 18000-6C Write
    const val CMD_18K6CLOCK: Short = 0x12 // ISO 18000-6C Lock
    const val CMD_18K6CKILL: Short = 0x13 // ISO 18000-6C Kill

    /* RFID 18k6c command codes */
    const val RFID_18K6C_READ = 0xC2.toByte()
    const val RFID_18K6C_WRITE = 0xC3.toByte()
    const val RFID_18K6C_KILL = 0xC4.toByte()
    const val RFID_18K6C_LOCK = 0xC5.toByte()
    const val RFID_18K6C_ACCESS = 0xC6.toByte()
    const val RFID_PACKET_TYPE_COMMAND_BEGIN:Short = 0x00
    const val RFID_PACKET_TYPE_COMMAND_END:Short = 0x01
    const val RFID_PACKET_TYPE_ANTENNA_CYCLE_BEGIN = 0x02
    const val RFID_PACKET_TYPE_ANTENNA_BEGIN = 0x03
    const val RFID_PACKET_TYPE_18K6C_INVENTORY_ROUND_BEGIN:Short = 0x04
    const val RFID_PACKET_TYPE_18K6C_INVENTORY:Short = 0x05
    const val RFID_PACKET_TYPE_18K6C_TAG_ACCESS = 0x06
    const val RFID_PACKET_TYPE_ANTENNA_CYCLE_END = 0x07
    const val RFID_PACKET_TYPE_ANTENNA_END = 0x08
    const val RFID_PACKET_TYPE_18K6C_INVENTORY_ROUND_END:Short = 0x09
    const val RFID_PACKET_TYPE_INVENTORY_CYCLE_BEGIN:Short = 0x0A
    const val RFID_PACKET_TYPE_INVENTORY_CYCLE_END:Short = 0x0B
    const val MAC_VER = 0x0000
    const val MAC_INFO = 0x0001
    const val MAC_RFTRANSINFO = 0x0002
    const val MAC_DBG1 = 0x0003
    const val MAC_DBG2 = 0x0004
    const val MAC_ERROR = 0x0005
    const val MAC_LAST_ERROR = 0x0006
    const val MAC_STATE = 0x0007
    const val MAC_XCVR_HEALTH_CHECK_FAIL_COUNT = 0x0008
    const val MAC_LAST_COMMAND_DURATION = 0x0009
    const val MAC_KEY = 0x000A
    const val MAC_ERROR_DIAGNOSTIC_SEL = 0x000B
    const val MAC_ERROR_DIAGNOSTIC = 0x000C
    const val MAC_FW_DEFAULT_VALUE_SOURCE = 0x000D
    const val MAC_NV_UPDATE_CONTROL = 0x000E
    const val MAC_BL_VER = 0x000F
    const val MAC_ACTIVE_FW = 0x0010
    const val MAC_FW_CRC = 0x0011
    const val MAC_MICROPROCESSOR_ID = 0x0012
    const val HST_ENGTEST_ARG0 = 0x0100
    const val HST_ENGTEST_ARG1 = 0x0101
    const val HST_DBG1 = 0x0102
    const val RESERVED_0x0103 = 0x0103
    const val HST_TX_RANDOM_DATA_DURATION = 0x0104
    const val HST_TX_RANDOM_DATA_CONTROL = 0x0105
    const val HST_TX_RANDOM_DATA_ON_TIME = 0x0106
    const val HST_TX_RANDOM_DATA_OFF_TIME = 0x0107
    const val HST_SET_FREQUENCY_CFG = 0x0108
    const val HST_SET_FREQUENCY_CHANNEL = 0x0109
    const val HST_SET_FREQUENCY_KHZ = 0x010A
    const val HST_SET_FREQUENCY_PLLDIVMULT = 0x010B
    const val HST_SET_FREQUENCY_PLLDACCTL = 0x010C
    const val HST_TEST_FREQUENCY_PLLDIVMULT = 0x010D
    const val HST_TEST_FREQUENCY_PLLDACCTL = 0x010E
    const val RESERVED_0x010F = 0x010F
    const val HST_TEST_INVENTORY_CFG = 0x0110
    const val HST_ENGTEST_RESULT_SEL = 0x0111
    const val HST_ENGTEST_RESULT = 0x0112
    const val HST_TEST_ANTENNA = 0x0113
    const val HST_TEST_RFPOWER = 0x0114
    const val HST_INJECT_RANDOM_TX_COUNT = 0x0115
    const val HST_TEST_MANUAL_OVERRIDE = 0x0116
    const val RESERVED_0x0117 = 0x0117
    const val RESERVED_0x0118 = 0x0118
    const val RESERVED_0x0119 = 0x0119
    const val RESERVED_0x011A = 0x011A
    const val RESERVED_0x011B = 0x011B
    const val RESERVED_0x011C = 0x011C
    const val RESERVED_0x011D = 0x011D
    const val RESERVED_0x011E = 0x011E
    const val RESERVED_0x011F = 0x011F
    const val HST_FORMAT_OEM_CONFIGURATION = 0x0120
    const val HST_FORMAT_OEM_KEY_CHECK = 0x0121
    const val RESERVED_0x0122 = 0x0122
    const val HST_CALIBRATION_CONTROL = 0x0123
    const val HST_CAL_PA_BIAS_CONFIG = 0x0124
    const val HST_CAL_PA_BIAS_MEASUREMENT = 0x0125
    const val HST_CAL_PA_BIAS_TARGET_CURRENT = 0x0126
    const val HST_CAL_PA_BIAS_CURRENT_PER_LSB = 0x0127
    const val HST_CAL_GROSSGAIN_CONFIG = 0x0128
    const val HST_PWRMGMT = 0x0200
    const val HST_CMNDIAGS = 0x0201
    const val HST_TRACE = 0x0202
    const val HST_IMPINJ_EXTENSIONS = 0x0203
    const val HST_PWRMGMT_STATUS = 0x0204
    const val HST_REGULATORY_REGION = 0x0300
    const val HST_PROTSCH_LBTCFG = 0x0301
    const val RESERVED_0x0302 = 0x0302
    const val HST_PROTSCH_FTIME = 0x0303
    const val RESERVED_0x0304 = 0x0304
    const val RESERVED_0x0305 = 0x0305
    const val HST_PROTSCH_TXTIME_ON = 0x0306
    const val HST_PROTSCH_TXTIME_OFF = 0x0307
    const val RESERVED_0x0308 = 0x0308
    const val RESERVED_0x0309 = 0x0309
    const val HST_PROTSCH_ADJCW = 0x030A
    const val RESERVED_0x030B = 0x030B
    const val HST_PROTSCH_TXTIME_ON_OVHD = 0x030C
    const val HST_PROTSCH_TXTIME_OFF_OVHD = 0x030D
    const val HST_PROTSCH_LBTRSSI = 0x030E
    const val HST_MBP_ADDR = 0x0400
    const val HST_MBP_DATA = 0x0401
    const val RESERVED_0x0402 = 0x0402
    const val RESERVED_0x0403 = 0x0403
    const val RESERVED_0x0404 = 0x0404
    const val RESERVED_0x0405 = 0x0405
    const val RESERVED_0x0406 = 0x0406
    const val RESERVED_0x0407 = 0x0407
    const val HST_LPROF_SEL = 0x0408
    const val HST_LPROF_ADDR = 0x0409
    const val HST_LPROF_DATA = 0x040A
    const val HST_OEM_ADDR = 0x0500
    const val HST_OEM_DATA = 0x0501
    const val HST_OEM_STRING_TYPE = 0x0502
    const val HST_OEM_STRING_LENGTH = 0x0503
    const val HST_OEM_STRING_CHAR_SEL = 0x0504
    const val HST_OEM_STRING_CHAR = 0x0505
    const val HST_GPIO_INMSK = 0x0600
    const val HST_GPIO_OUTMSK = 0x0601
    const val HST_GPIO_OUTVAL = 0x0602
    const val HST_GPIO_CFG = 0x0603
    const val HST_ANT_CYCLES = 0x0700
    const val HST_ANT_DESC_SEL = 0x0701
    const val HST_ANT_DESC_CFG = 0x0702
    const val MAC_ANT_DESC_STAT = 0x0703
    const val HST_ANT_DESC_PORTDEF = 0x0704
    const val HST_ANT_DESC_DWELL = 0x0705
    const val HST_ANT_DESC_RFPOWER = 0x0706
    const val HST_ANT_DESC_INV_CNT = 0x0707
    const val HST_TAGMSK_DESC_SEL = 0x0800
    const val HST_TAGMSK_DESC_CFG = 0x0801
    const val HST_TAGMSK_BANK = 0x0802
    const val HST_TAGMSK_PTR = 0x0803
    const val HST_TAGMSK_LEN = 0x0804
    const val HST_TAGMSK_0_3 = 0x0805
    const val HST_TAGMSK_4_7 = 0x0806
    const val HST_TAGMSK_8_11 = 0x0807
    const val HST_TAGMSK_12_15 = 0x0808
    const val HST_TAGMSK_16_19 = 0x0809
    const val HST_TAGMSK_20_23 = 0x080A
    const val HST_TAGMSK_24_27 = 0x080B
    const val HST_TAGMSK_28_31 = 0x080C
    const val HST_QUERY_CFG = 0x0900
    const val HST_INV_CFG = 0x0901
    const val HST_INV_SEL = 0x0902
    const val HST_INV_ALG_PARM_0 = 0x0903
    const val HST_INV_ALG_PARM_1 = 0x0904
    const val HST_INV_ALG_PARM_2 = 0x0905
    const val HST_INV_ALG_PARM_3 = 0x0906
    const val HST_INV_RSSI_FILTERING_CONFIG = 0x0907
    const val HST_INV_RSSI_FILTERING_THRESHOLD = 0x0908
    const val HST_INV_RSSI_FILTERING_COUNT = 0x0909
    const val RESERVED_0x090A = 0x090A
    const val RESERVED_0x090B = 0x090B
    const val RESERVED_0x090C = 0x090C
    const val RESERVED_0x090D = 0x090D
    const val RESERVED_0x090E = 0x090E
    const val RESERVED_0x090F = 0x090F
    const val RESERVED_0x0910 = 0x0910
    const val HST_INV_EPC_MATCH_CFG = 0x0911
    const val HST_INV_EPCDAT_0_3 = 0x0912
    const val HST_INV_EPCDAT_4_7 = 0x0913
    const val HST_INV_EPCDAT_8_11 = 0x0914
    const val HST_INV_EPCDAT_12_15 = 0x0915
    const val HST_INV_EPCDAT_16_19 = 0x0916
    const val HST_INV_EPCDAT_20_23 = 0x0917
    const val HST_INV_EPCDAT_24_27 = 0x0918
    const val HST_INV_EPCDAT_28_31 = 0x0919
    const val HST_INV_EPCDAT_32_35 = 0x091A
    const val HST_INV_EPCDAT_36_39 = 0x091B
    const val HST_INV_EPCDAT_40_43 = 0x091C
    const val HST_INV_EPCDAT_44_47 = 0x091D
    const val HST_INV_EPCDAT_48_51 = 0x091E
    const val HST_INV_EPCDAT_52_55 = 0x091F
    const val HST_INV_EPCDAT_56_59 = 0x0920
    const val HST_INV_EPCDAT_60_63 = 0x0921
    const val RESERVED_0x0922 = 0x0922
    const val RESERVED_0x0923 = 0x0923
    const val RESERVED_0x0924 = 0x0924
    const val RESERVED_0x0925 = 0x0925
    const val RESERVED_0x0926 = 0x0926
    const val RESERVED_0x0927 = 0x0927
    const val RESERVED_0x0928 = 0x0928
    const val RESERVED_0x0929 = 0x0929
    const val RESERVED_0x092A = 0x092A
    const val RESERVED_0x092B = 0x092B
    const val RESERVED_0x092C = 0x092C
    const val RESERVED_0x092D = 0x092D
    const val RESERVED_0x092E = 0x092E
    const val RESERVED_0x092F = 0x092F
    const val HST_INV_STATS_DURATION = 0x0930
    const val HST_INV_STATS_QUERY = 0x0931
    const val HST_INV_STATS_RN16RCV = 0x0932
    const val HST_INV_STATS_RN16TO = 0x0933
    const val HST_INV_STATS_EPCTO = 0x0934
    const val HST_INV_STATS_TAGREADS = 0x0935
    const val HST_INV_STATS_EPCCRC = 0x0936
    const val HST_LBT_STATS_INTF_DURATION = 0x0937
    const val HST_LBT_STATS_INTF_COUNT = 0x0938
    const val RESERVED_0x0A00 = 0x0A00
    const val HST_TAGACC_DESC_CFG = 0x0A01
    const val HST_TAGACC_BANK = 0x0A02
    const val HST_TAGACC_PTR = 0x0A03
    const val HST_TAGACC_CNT = 0x0A04
    const val HST_TAGACC_LOCKCFG = 0x0A05
    const val HST_TAGACC_ACCPWD = 0x0A06
    const val HST_TAGACC_KILLPWD = 0x0A07
    const val HST_TAGWRDAT_SEL = 0x0A08
    const val HST_TAGWRDAT_0 = 0x0A09
    const val HST_TAGWRDAT_1 = 0x0A0A
    const val HST_TAGWRDAT_2 = 0x0A0B
    const val HST_TAGWRDAT_3 = 0x0A0C
    const val HST_TAGWRDAT_4 = 0x0A0D
    const val HST_TAGWRDAT_5 = 0x0A0E
    const val HST_TAGWRDAT_6 = 0x0A0F
    const val HST_TAGWRDAT_7 = 0x0A10
    const val HST_TAGWRDAT_8 = 0x0A11
    const val HST_TAGWRDAT_9 = 0x0A12
    const val HST_TAGWRDAT_10 = 0x0A13
    const val HST_TAGWRDAT_11 = 0x0A14
    const val HST_TAGWRDAT_12 = 0x0A15
    const val HST_TAGWRDAT_13 = 0x0A16
    const val HST_TAGWRDAT_14 = 0x0A17
    const val HST_TAGWRDAT_15 = 0x0A18
    const val HST_TAGQTDAT = 0x0A19
    const val MAC_RFTC_PAPWRLEV = 0x0B00
    const val HST_RFTC_PAPWRCTL_PGAIN = 0x0B01
    const val HST_RFTC_PAPWRCTL_IGAIN = 0x0B02
    const val RESERVED_0x0B03 = 0x0B03
    const val MAC_RFTC_REVPWRLEV = 0x0B04
    const val HST_RFTC_REVPWRTHRSH = 0x0B05
    const val MAC_RFTC_AMBIENTTEMP = 0x0B06
    const val HST_RFTC_AMBIENTTEMPTHRSH = 0x0B07
    const val MAC_RFTC_XCVRTEMP = 0x0B08
    const val HST_RFTC_XCVRTEMPTHRESH = 0x0B09
    const val MAC_RFTC_PATEMP = 0x0B0A
    const val HST_RFTC_PATEMPTHRSH = 0x0B0B
    const val RESERVED_0x0B0C = 0x0B0C
    const val HST_RFTC_PAPWRCTL_AIWDELAY = 0x0B0D
    const val MAC_RFTC_PAPWRCTL_STAT0 = 0x0B0E
    const val MAC_RFTC_PAPWRCTL_STAT1 = 0x0B0F
    const val MAC_RFTC_PAPWRCTL_STAT2 = 0x0B10
    const val MAC_RFTC_PAPWRCTL_STAT3 = 0x0B11
    const val HST_RFTC_ANTSENSRESTHRSH = 0x0B12
    const val HST_RFTC_IFLNAAGCRANGE = 0x0B13
    const val MAC_RFTC_LAST_ANARXGAINNORM = 0x0B14
    const val HST_RFTC_OPENLOOPPWRCTRL = 0x0B15
    const val HST_RFTC_SJC_CFG = 0x0B16
    const val RESERVED_0x0B17 = 0x0B17
    const val RESERVED_0x0B18 = 0x0B18
    const val RESERVED_0x0B19 = 0x0B19
    const val HST_RFTC_DC_OFFSET_COEFF = 0x0B1A
    const val HST_RFTC_CAL_PA_BIAS_DAC = 0x0B1B
    const val HST_RFTC_GROSSGAIN_CONFIG = 0x0B1C
    const val HST_RFTC_CAL_GROSSGAIN_SEL = 0x0B1D
    const val HST_RFTC_CAL_GROSSGAIN_VALUE = 0x0B1E
    const val RESERVED_0x0B1F = 0x0B1F
    const val HST_RFTC_CAL_GGNEG7_X = 0x0B20
    const val HST_RFTC_CAL_GGNEG5_X = 0x0B21
    const val HST_RFTC_CAL_GGNEG3_X = 0x0B22
    const val HST_RFTC_CAL_GGNEG1_X = 0x0B23
    const val HST_RFTC_CAL_GGPLUS1_X = 0x0B24
    const val HST_RFTC_CAL_GGPLUS3_X = 0x0B25
    const val HST_RFTC_CAL_GGPLUS5_X = 0x0B26
    const val HST_RFTC_CAL_GGPLUS7_X = 0x0B27
    const val RESERVED_0x0B28 = 0x0B28
    const val HST_RFTC_CAL_RFFWDPWR_A2 = 0x0B29
    const val HST_RFTC_CAL_RFFWDPWR_A1 = 0x0B2A
    const val HST_RFTC_CAL_RFFWDPWR_A0 = 0x0B2B
    const val HST_RFTC_FWDPWRTHRSH = 0x0B2C
    const val RESERVED_0x0B2D = 0x0B2D
    const val RESERVED_0x0B2E = 0x0B2E
    const val RESERVED_0x0B2F = 0x0B2F
    const val HST_RFTC_CLKDBLR_CFG = 0x0B30
    const val HST_RFTC_CLKDBLR_SEL = 0x0B31
    const val HST_RFTC_CLKDBLR_LUTENTRY = 0x0B32
    const val RESERVED_0x0B33 = 0x0B33
    const val RESERVED_0x0B34 = 0x0B34
    const val RESERVED_0x0B35 = 0x0B35
    const val RESERVED_0x0B36 = 0x0B36
    const val RESERVED_0x0B37 = 0x0B37
    const val RESERVED_0x0B38 = 0x0B38
    const val RESERVED_0x0B39 = 0x0B39
    const val RESERVED_0x0B3A = 0x0B3A
    const val RESERVED_0x0B3B = 0x0B3B
    const val RESERVED_0x0B3C = 0x0B3C
    const val RESERVED_0x0B3D = 0x0B3D
    const val RESERVED_0x0B3E = 0x0B3E
    const val RESERVED_0x0B3F = 0x0B3F
    const val HST_RFTC_FRQHOPMODE = 0x0B40
    const val HST_RFTC_FRQHOPENTRYCNT = 0x0B41
    const val HST_RFTC_FRQHOPTABLEINDEX = 0x0B42
    const val MAC_RFTC_HOPCNT = 0x0B43
    const val HST_RFTC_MINHOPDUR = 0x0B44
    const val HST_RFTC_MAXHOPDUR = 0x0B45
    const val HST_RFTC_FRQHOPRANDSEED = 0x0B46
    const val MAC_RFTC_FRQHOPSHFTREGVAL = 0x0B47
    const val MAC_RFTC_FRQHOPRANDNUMCNT = 0x0B48
    const val HST_RFTC_FRQCHINDEX = 0x0B49
    const val HST_RFTC_PLLLOCKTIMEOUT = 0x0B4A
    const val RESERVED_0x0B4B = 0x0B4B
    const val RESERVED_0x0B4C = 0x0B4C
    const val RESERVED_0x0B4D = 0x0B4D
    const val HST_RFTC_BERREADDELAY = 0x0B4E
    const val RESERVED_0x0B4F = 0x0B4F
    const val MAC_RFTC_FWDRFPWRRAWADC = 0x0B50
    const val MAC_RFTC_REVRFPWRRAWADC = 0x0B51
    const val MAC_RFTC_ANTSENSERAWADC = 0x0B52
    const val MAC_RFTC_AMBTEMPRAWADC = 0x0B53
    const val MAC_RFTC_PATEMPRAWADC = 0x0B54
    const val MAC_RFTC_XCVRTEMPRAWADC = 0x0B55
    const val MAC_RFTC_PACURRENT = 0x0B56
    const val MAC_RFTC_PACURRENTADC = 0x0B57
    const val RESERVED_0x0B58 = 0x0B58
    const val RESERVED_0x0B59 = 0x0B59
    const val RESERVED_0x0B5A = 0x0B5A
    const val RESERVED_0x0B5B = 0x0B5B
    const val RESERVED_0x0B5C = 0x0B5C
    const val RESERVED_0x0B5D = 0x0B5D
    const val RESERVED_0x0B5E = 0x0B5E
    const val HST_RFTC_ACTIVE_PROFILE = 0x0B5F
    const val HST_RFTC_CURRENT_PROFILE = 0x0B60
    const val HST_RFTC_PROF_SEL = 0x0B61
    const val MAC_RFTC_PROF_CFG = 0x0B62
    const val MAC_RFTC_PROF_ID_HIGH = 0x0B63
    const val MAC_RFTC_PROF_ID_LOW = 0x0B64
    const val MAC_RFTC_PROF_IDVER = 0x0B65
    const val MAC_RFTC_PROF_PROTOCOL = 0x0B66
    const val MAC_RFTC_PROF_R2TMODTYPE = 0x0B67
    const val MAC_RFTC_PROF_TARI = 0x0B68
    const val MAC_RFTC_PROF_X = 0x0B69
    const val MAC_RFTC_PROF_PW = 0x0B6A
    const val MAC_RFTC_PROF_RTCAL = 0x0B6B
    const val MAC_RFTC_PROF_TRCAL = 0x0B6C
    const val MAC_RFTC_PROF_DIVIDERATIO = 0x0B6D
    const val MAC_RFTC_PROF_MILLERNUM = 0x0B6E
    const val MAC_RFTC_PROF_T2RLINKFREQ = 0x0B6F
    const val MAC_RFTC_PROF_VART2DELAY = 0x0B70
    const val MAC_RFTC_PROF_RXDELAY = 0x0B71
    const val MAC_RFTC_PROF_MINTOTT2DELAY = 0x0B72
    const val MAC_RFTC_PROF_TXPROPDELAY = 0x0B73
    const val MAC_RFTC_PROF_RSSIAVECFG = 0x0B74
    const val MAC_RFTC_PROF_PREAMCMD = 0x0B75
    const val MAC_RFTC_PROF_FSYNCCMD = 0x0B76
    const val MAC_RFTC_PROF_T2WAITCMD = 0x0B77
    const val RESERVED_0x0B78 = 0x0B78
    const val RESERVED_0x0B79 = 0x0B79
    const val RESERVED_0x0B7A = 0x0B7A
    const val RESERVED_0x0B7B = 0x0B7B
    const val RESERVED_0x0B7C = 0x0B7C
    const val RESERVED_0x0B7D = 0x0B7D
    const val RESERVED_0x0B7E = 0x0B7E
    const val RESERVED_0x0B7F = 0x0B7F
    const val HST_RFTC_CAL_RFREVPWR_A2 = 0x0B80
    const val HST_RFTC_CAL_RFREVPWR_A1 = 0x0B81
    const val HST_RFTC_CAL_RFREVPWR_A0 = 0x0B82
    const val HST_RFTC_CAL_AMBIENT_TEMP_A2 = 0x0B83
    const val HST_RFTC_CAL_AMBIENT_TEMP_A1 = 0x0B84
    const val HST_RFTC_CAL_AMBIENT_TEMP_A0 = 0x0B85
    const val HST_RFTC_CAL_XCVR_TEMP_A2 = 0x0B86
    const val HST_RFTC_CAL_XCVR_TEMP_A1 = 0x0B87
    const val HST_RFTC_CAL_XCVR_TEMP_A0 = 0x0B88
    const val HST_RFTC_CAL_ANT_SENSE_A2 = 0x0B89
    const val HST_RFTC_CAL_ANT_SENSE_A1 = 0x0B8A
    const val HST_RFTC_CAL_ANT_SENSE_A0 = 0x0B8B
    const val HST_RFTC_SJC_EXTERNALLOTHRSH = 0x0B8C
    const val HST_RFTC_PA_CURRENT_TRIM = 0x0B8D
    const val HST_RFTC_CAL_PA_TEMP_A2 = 0x0B8E
    const val HST_RFTC_CAL_PA_TEMP_A1 = 0x0B8F
    const val HST_RFTC_CAL_PA_TEMP_A0 = 0x0B90
    const val HST_RFTC_CAL_PA_CURRENT_A2 = 0x0B91
    const val HST_RFTC_CAL_PA_CURRENT_A1 = 0x0B92
    const val HST_RFTC_CAL_PA_CURRENT_A0 = 0x0B93
    const val HST_RFTC_CAL_EPC_RSSI = 0x0B94
    const val RESERVED_0x0C00 = 0x0C00
    const val HST_RFTC_FRQCH_SEL = 0x0C01
    const val HST_RFTC_FRQCH_CFG = 0x0C02
    const val HST_RFTC_FRQCH_DESC_PLLDIVMULT = 0x0C03
    const val HST_RFTC_FRQCH_DESC_PLLDACCTL = 0x0C04
    const val MAC_RFTC_FRQCH_DESC_PLLLOCKSTAT0 = 0x0C05
    const val MAC_RFTC_FRQCH_DESC_PLLLOCKSTAT1 = 0x0C06
    const val RESERVED_0x0C07 = 0x0C07
    const val HST_RFTC_FRQCH_CMDSTART = 0x0C08
    const val HST_RFTC_PLL_LAST_LOCK_FREQ = 0x0C09
    const val RESERVED_CUSTOMER = 0x0F00
    const val HST_CMD = 0xF000
}