package tw.yalan.mvvm.core.data

import tw.yalan.mvvm.core.data.local.LocalRepository
import tw.yalan.mvvm.core.data.remote.RemoteRepository
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastDetailResponse
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastResponse
import tw.yalan.mvvm.core.ui.base.CoreScope
import java.io.File


/**
 * Created by Yalan Ding on 02/01/2020
 */

@CoreScope
class DataRepository
constructor(
    private val remoteRepository: RemoteRepository
    , private val localRepository: LocalRepository
) : DataSource {
    override suspend fun fetchPodcastList(): Resource<GetPodcastResponse> =
        remoteRepository.fetchPodcastList()


    override suspend fun fetchPodcastDetail(): Resource<GetPodcastDetailResponse> =
        remoteRepository.fetchPodcastDetail()
}
