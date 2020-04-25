package tw.yalan.mvvm.core.data.remote.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import tw.yalan.mvvm.core.data.dto.Podcast

@Parcelize
data class GetPodcastResponse(
    @SerializedName("data")
    val data: PodcastList? = null
) : Parcelable

@Parcelize
data class PodcastList(
    @SerializedName("podcast")
    val podcasts: List<Podcast>? = null
) : Parcelable