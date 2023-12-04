package com.funsol.funmob_sdk.model

import android.os.Parcelable
import androidx.annotation.Keep
import kotlinx.android.parcel.Parcelize
@Keep
@Parcelize
data class CampaignResponse(
    val ad_unit_id: String,
    val combination_id: String,
    val deeplink: String,
    val domain: String,
    val description: String,
    val icon: String,
    val id: String,
    val image: String,
    val title: String,
    val video: String,
    val package_name: String,
    val downloads: Long,
    val ratings: Double
) : Parcelable