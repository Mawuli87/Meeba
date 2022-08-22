package com.aisisabeem.Meeba.models

import android.os.Parcel
import android.os.Parcelable

data class User(
    val id:String = "",
    val name:String = "",
    val email: String = "",
    val image : String = "",
    val mobile:Int = 0,
    val fcmToken : String = ""
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readInt(),
        parcel.readString()!!
    ) {
    }

    override fun writeToParcel(desk: Parcel, flags: Int) = with(desk)  {
        writeString(id)
        writeString(name)
        writeString(email)
        writeString(image)
        writeInt(mobile)
        writeString(fcmToken)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<User> {
        override fun createFromParcel(parcel: Parcel): User {
            return User(parcel)
        }

        override fun newArray(size: Int): Array<User?> {
            return arrayOfNulls(size)
        }
    }
}
