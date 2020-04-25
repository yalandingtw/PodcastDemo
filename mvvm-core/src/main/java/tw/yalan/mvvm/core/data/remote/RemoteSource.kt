package tw.yalan.mvvm.core.data.remote

import tw.yalan.mvvm.core.data.Resource
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastDetailResponse
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastResponse


internal interface RemoteSource {
    suspend fun fetchPodcastList(): Resource<GetPodcastResponse>

    suspend fun fetchPodcastDetail(): Resource<GetPodcastDetailResponse>

}
