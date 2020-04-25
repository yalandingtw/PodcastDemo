package tw.yalan.mvvm.core.data.dto

import android.os.Parcelable
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Episode(

    @field:SerializedName("contentUrl")
    val contentUrl: String? = null,

    @field:SerializedName("publishedDate")
    val publishedDate: String? = null,

    @field:SerializedName("title")
    val title: String? = null,

    @field:SerializedName("desc")
    val desc: String? = null,

    @Expose
    var albumUrl: String? = null,
    @Expose
    var podcastName: String? = null
) : Parcelable{
    override fun equals(other: Any?): Boolean {
        return contentUrl == (other as? Episode)?.contentUrl
    }
}
