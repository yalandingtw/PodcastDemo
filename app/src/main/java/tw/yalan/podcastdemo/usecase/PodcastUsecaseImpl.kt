package tw.yalan.podcastdemo.usecase

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.yalan.podcast.host.data.error.Error
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import tw.yalan.mvvm.core.data.DataSource
import tw.yalan.mvvm.core.data.Resource
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastDetailResponse
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastResponse
import tw.yalan.podcastdemo.usecase.PodcastUsecase
import java.util.*
import kotlin.coroutines.CoroutineContext

/**
 * Created by Alan Ding on 2020/3/14.
 */
class PodcastUsecaseImpl constructor(
    private val dataRepository: DataSource,
    override val coroutineContext: CoroutineContext
) : PodcastUsecase,
    CoroutineScope {


    val podcastResponse: MutableLiveData<Resource<GetPodcastResponse>> = MutableLiveData()
    val podcastDetailResponse: MutableLiveData<Resource<GetPodcastDetailResponse>> =
        MutableLiveData()

    override fun reloadDashboard() {
        podcastResponse.postValue(Resource.Loading())
        launch(Dispatchers.IO) {
            var serviceResult: Resource<GetPodcastResponse>
            try {
                serviceResult = dataRepository.fetchPodcastList()
            } catch (e: Exception) {
                serviceResult = Resource.DataError(Error.NETWORK_ERROR)
            }
            podcastResponse.postValue(serviceResult)
        }
    }

    override fun reloadPodcastDetail(id: String?) {
        podcastDetailResponse.postValue(Resource.Loading())
        launch(Dispatchers.IO) {
            var serviceResult: Resource<GetPodcastDetailResponse>
            try {
                serviceResult = dataRepository.fetchPodcastDetail() /* Put id to here */
            } catch (e: Exception) {
                serviceResult = Resource.DataError(Error.NETWORK_ERROR)
            }
            podcastDetailResponse.postValue(serviceResult)
        }
    }
}