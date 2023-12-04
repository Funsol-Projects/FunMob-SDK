package com.funsol.funmob_sdk.model

import androidx.annotation.Keep

@Keep
data class UserDeviceInfo(
    var time: String = "",
    var uuid: String = "",
    var camp_uuid: String = "",
    val Board: String,
    val BootLoader: String,
    val Brand: String,
    val Device: String,
    val Display: String,
    val FingerPrint: String,
    val HOST: String,
    val Hardware: String,
    val ID: String,
    val MANUFACTURER: String,
    val ODM_SKU: String,
    val Product: String,
    val SKU: String,
    val SOC_MANUFACTURER: String,
    val SOC_Model: String,
    val TAGS: String,
    val TIME: String,
    val TYPE: String,
    val model: String,
    val user_unique_id: String
)