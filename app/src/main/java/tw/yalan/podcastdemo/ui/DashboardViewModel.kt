package tw.yalan.podcastdemo.ui

import androidx.lifecycle.LiveData
import tw.yalan.podcastdemo.usecase.PodcastUsecaseImpl
import tw.yalan.mvvm.core.data.Resource
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastResponse
import tw.yalan.mvvm.core.ui.base.BaseViewModel

/**
 * Created by Yalan Ding on 2020/4/23.
 */
class DashboardViewModel(private val usecase: PodcastUsecaseImpl) : BaseViewModel() {


    val podcastResponse: LiveData<Resource<GetPodcastResponse>?> = usecase.podcastResponse

    fun reloadDashboard() {
        usecase.reloadDashboard()
    }
}