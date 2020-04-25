package tw.yalan.mvvm.core.data.remote.service

import retrofit2.Response
import retrofit2.http.GET
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastDetailResponse
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastResponse


/**
 * Created by Yalan Ding on 02/01/2020
 */

interface APIService {


    @GET("getcasts")
    suspend fun fetchPodcastList(): Response<GetPodcastResponse>

    @GET("getcastdetail")
    suspend fun fetchPodcastDetail(): Response<GetPodcastDetailResponse>
}
