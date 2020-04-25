package tw.yalan.mvvm.core.data.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Podcast(

    @field:SerializedName("artworkUrl100")
    val artworkUrl100: String? = null,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("artistName")
    val artistName: String? = null,

    @field:SerializedName("id")
    val id: String? = null
) : Parcelable
