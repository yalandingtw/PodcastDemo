package tw.yalan.podcastdemo.di

import tw.yalan.podcastdemo.usecase.PodcastUsecaseImpl
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import tw.yalan.podcastdemo.player.PlayerServiceManager
import tw.yalan.podcastdemo.ui.epoxy.EpisodeController
import tw.yalan.podcastdemo.ui.epoxy.PodcastDashboardController
import tw.yalan.podcastdemo.ui.DashboardViewModel
import tw.yalan.podcastdemo.ui.fragments.details.EpisodeViewModel

/**
 * Created by Yalan Ding on 2020/4/23.
 */
val generalModule = module {
    single { PlayerServiceManager(get()) }
    factory { PodcastDashboardController() }
    factory { EpisodeController() }
}

val usecaseModule = module {
    factory { PodcastUsecaseImpl(get(), get()) }
}

val viewModelModule = module {
    viewModel { EpisodeViewModel(get()) }
    viewModel { DashboardViewModel(get()) }
}


