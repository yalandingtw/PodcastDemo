package tw.yalan.mvvm.core.data

import tw.yalan.mvvm.core.data.remote.dto.GetPodcastDetailResponse
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastResponse
import java.io.File


interface DataSource {

    suspend fun fetchPodcastList(): Resource<GetPodcastResponse>

    suspend fun fetchPodcastDetail(): Resource<GetPodcastDetailResponse>

}
