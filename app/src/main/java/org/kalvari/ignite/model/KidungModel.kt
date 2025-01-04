package org.kalvari.ignite.model

import android.os.Parcel
import android.os.Parcelable

open class KidungModel : Parcelable {
    @JvmField
    var key: String? = null
    @JvmField
    var title: String? = null
    var bait: String? = null
    var koor: String? = null
    var nada: String? = null
    @JvmField
    var no: String? = null

    constructor()

    constructor(
        key: String?,
        title: String?,
        bait: String?,
        koor: String?,
        nada: String?,
        no: String?
    ) {
        this.key = key
        this.title = title
        this.bait = bait
        this.koor = koor
        this.nada = nada
        this.no = no
    }

    protected constructor(`in`: Parcel) {
        key = `in`.readString()
        title = `in`.readString()
        bait = `in`.readString()
        koor = `in`.readString()
        nada = `in`.readString()
        no = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(key)
        dest.writeString(title)
        dest.writeString(bait)
        dest.writeString(koor)
        dest.writeString(nada)
        dest.writeString(no)
    }

    companion object{
        @JvmField
        val CREATOR: Parcelable.Creator<KidungModel> = object : Parcelable.Creator<KidungModel> {
            override fun createFromParcel(parcel: Parcel): KidungModel {
                return KidungModel(parcel)
            }

            override fun newArray(size: Int): Array<KidungModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}
