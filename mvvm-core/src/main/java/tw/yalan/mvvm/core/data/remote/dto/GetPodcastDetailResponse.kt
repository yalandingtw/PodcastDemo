package tw.yalan.mvvm.core.data.remote.dto


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import tw.yalan.mvvm.core.data.dto.Podcast
import tw.yalan.mvvm.core.data.dto.PodcastDetail

@Parcelize
data class GetPodcastDetailResponse(
    @SerializedName("data")
    val data: DetailContainer? = null
) : Parcelable

@Parcelize
data class DetailContainer(
    @SerializedName("collection")
    val collection: PodcastDetail? = null
) : Parcelable