package tw.yalan.podcastdemo.ui

import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_dashboard.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import org.koin.core.parameter.parametersOf
import tw.yalan.mvvm.core.data.Resource
import tw.yalan.mvvm.core.data.dto.Podcast
import tw.yalan.mvvm.core.data.remote.dto.GetPodcastResponse
import tw.yalan.mvvm.core.ext.observe
import tw.yalan.mvvm.core.ext.startActivity
import tw.yalan.mvvm.core.ui.base.BaseActivity
import tw.yalan.podcastdemo.R
import tw.yalan.podcastdemo.ui.epoxy.PodcastDashboardController

class DashboardActivity : BaseActivity(), PodcastDashboardController.Callback {
    private val viewModel: DashboardViewModel by viewModel()
    private val podcastController: PodcastDashboardController by inject { parametersOf(this) }


    override val layoutId: Int
        get() = R.layout.activity_dashboard

    override fun observeViewModel() {
        observe(viewModel.podcastResponse, ::handlePodcastResponse)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        podcastController.callback = this
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = podcastController.adapter
        swipeRefresh.setOnRefreshListener {
            viewModel.reloadDashboard()
        }
    }

    override fun onClickItem(view: View, position: Int, data: Podcast) {
        startActivity<DetailsActivity>(DetailsActivity.newBundle(data))
    }

    private fun handlePodcastResponse(resource: Resource<GetPodcastResponse>?) {
        when (resource) {
            is Resource.Loading -> {
                swipeRefresh.isRefreshing = true
            }
            is Resource.Success<GetPodcastResponse> -> {
                swipeRefresh.isRefreshing = false
                val podcastData = resource.data?.data
                podcastController.data = podcastData?.podcasts ?: emptyList()
                podcastController.requestModelBuild()
            }
            is Resource.DataError -> {
                swipeRefresh.isRefreshing = false
                podcastController.data = emptyList()
                podcastController.requestModelBuild()

            }
        }
    }


    override fun onResume() {
        super.onResume()
        viewModel.reloadDashboard()
    }
}
