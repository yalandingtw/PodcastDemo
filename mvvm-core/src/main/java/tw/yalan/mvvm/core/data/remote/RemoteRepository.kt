package tw.yalan.mvvm.core.data.remote

import tw.yalan.mvvm.core.Constants
import tw.yalan.mvvm.core.net.Network.Utils.isConnected
import tw.yalan.mvvm.core.data.Resource
import tw.yalan.mvvm.core.data.remote.service.APIService
import com.yalan.podcast.host.data.error.Error.Companion.NETWORK_ERROR
import com.yalan.podcast.host.data.error.Error.Companion.NO_INTERNET_CONNECTION
import retrofit2.Response
import tw.yalan.mvvm.core.CoreApp
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastDetailResponse
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastResponse
import java.io.IOException


/**
 * Created by Yalan Ding on 02/01/2020
 */

class RemoteRepository
constructor(private val serviceGenerator: ServiceGenerator) : RemoteSource {

    private var apiService: APIService? = null

    private fun getOrCreateApiService(): APIService {
        if (apiService == null) {
            apiService = serviceGenerator.createService(APIService::class.java, Constants.BASE_URL)
        }
        return apiService!!
    }

    private suspend fun processCall(responseCall: suspend () -> Response<*>): Any? {
        if (!isConnected(CoreApp.context)) {
            return NO_INTERNET_CONNECTION
        }
        return try {
            val response = responseCall.invoke()
            val responseCode = response.code()
            if (response.isSuccessful) {
                response.body()
            } else {
                responseCode
            }
        } catch (e: IOException) {
            NETWORK_ERROR
        }
    }

    override suspend fun fetchPodcastList(): Resource<GetPodcastResponse> {
        val apiService = getOrCreateApiService()

        return when (val response =
            processCall { apiService.fetchPodcastList() }) {
            is GetPodcastResponse -> {
                Resource.Success(data = response)
            }
            else -> {
                Resource.DataError(errorCode = response as Int)
            }
        }
    }

    override suspend fun fetchPodcastDetail(): Resource<GetPodcastDetailResponse> {
        val apiService = getOrCreateApiService()

        return when (val response =
            processCall { apiService.fetchPodcastDetail() }) {
            is GetPodcastDetailResponse -> {
                Resource.Success(data = response)
            }
            else -> {
                Resource.DataError(errorCode = response as Int)
            }
        }
    }

}
