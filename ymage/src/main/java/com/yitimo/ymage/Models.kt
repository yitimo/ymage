package com.yitimo.ymage

import android.os.Parcel
import android.os.Parcelable

data class Bucket (
    val Id: Long = 0,
    val Cover: String = "",
    val Size: Int = 0,
    val Name: String = ""
)

data class Image(
    val Id: Long,
    val Data: String,
    val Width: Int = 0,
    val Height: Int = 0,
    val IsGif: Boolean = false
): Parcelable {
    constructor(parcel: Parcel): this (
        parcel.readLong(),
        parcel.readString() ?: "",
        parcel.readInt(),
        parcel.readInt(),
        parcel.readInt() != 0
    )
    override fun writeToParcel(parcel: Parcel?, flags: Int) {
        parcel?.writeLong(Id)
        parcel?.writeString(Data)
        parcel?.writeInt(Width)
        parcel?.writeInt(Height)
        parcel?.writeInt(if (IsGif) 1 else 0)
    }

    override fun describeContents(): Int {
        return 0
    }
    companion object CREATOR : Parcelable.Creator<Image> {
        override fun createFromParcel(parcel: Parcel): Image {
            return Image(parcel)
        }

        override fun newArray(size: Int): Array<Image?> {
            return arrayOfNulls(size)
        }
    }
}

const val resultYmageOrigin: Int = 30926
const val resultYmage: Int = 30927
