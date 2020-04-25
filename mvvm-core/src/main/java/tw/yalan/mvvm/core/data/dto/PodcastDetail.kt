package tw.yalan.mvvm.core.data.dto

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PodcastDetail(

    @field:SerializedName("artworkUrl100")
    val artworkUrl100: String? = null,

    @field:SerializedName("country")
    val country: String? = null,

    @field:SerializedName("releaseDate")
    val releaseDate: String? = null,

    @field:SerializedName("genres")
    val genres: String? = null,

    @field:SerializedName("artistId")
    val artistId: Int? = null,

    @field:SerializedName("artistName")
    val artistName: String? = null,

    @field:SerializedName("genreIds")
    val genreIds: String? = null,

    @field:SerializedName("artworkUrl600")
    val artworkUrl600: String? = null,

    @field:SerializedName("collectionId")
    val collectionId: Int? = null,

    @field:SerializedName("collectionName")
    val collectionName: String? = null,

    @field:SerializedName("contentFeed")
    val episodes: List<Episode>? = null
) : Parcelable
