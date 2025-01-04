package org.kalvari.ignite.model

import android.os.Parcel
import android.os.Parcelable
import android.os.Parcelable.Creator

class RenunganModel : Parcelable {
    @JvmField
    var key: String? = null
    @JvmField
    var title: String? = null
    @JvmField
    var ayat: String? = null
    @JvmField
    var content: String? = null

    constructor()

    constructor(key: String?, title: String?, ayat: String?, content: String?) {
        this.key = key
        this.title = title
        this.ayat = ayat
        this.content = content
    }

    protected constructor(`in`: Parcel) {
        key = `in`.readString()
        title = `in`.readString()
        ayat = `in`.readString()
        content = `in`.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(key)
        dest.writeString(title)
        dest.writeString(ayat)
        dest.writeString(content)
    }

    companion object{
        @JvmField
        val CREATOR: Creator<RenunganModel> = object : Creator<RenunganModel> {
            override fun createFromParcel(parcel: Parcel): RenunganModel {
                return RenunganModel(parcel)
            }

            override fun newArray(size: Int): Array<RenunganModel?> {
                return arrayOfNulls(size)
            }
        }
    }
}
