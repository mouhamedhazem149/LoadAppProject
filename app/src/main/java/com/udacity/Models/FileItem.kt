package com.udacity.Models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class FileItem(val fileTitle : String,val fileStatus : Int) : Parcelable {
}