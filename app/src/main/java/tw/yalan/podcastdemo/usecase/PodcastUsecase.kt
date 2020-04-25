package tw.yalan.podcastdemo.usecase

/**
 * Created by Yalan Ding on 2020/4/23.
 */
interface PodcastUsecase {

    fun reloadDashboard()

    fun reloadPodcastDetail(id: String?)

}