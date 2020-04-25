package tw.yalan.podcastdemo

import android.content.Context
import org.koin.android.ext.android.inject
import org.koin.core.module.Module
import org.koin.core.parameter.parametersOf
import tw.yalan.mvvm.core.CoreApp
import tw.yalan.podcastdemo.di.generalModule
import tw.yalan.podcastdemo.di.usecaseModule
import tw.yalan.podcastdemo.di.viewModelModule
import tw.yalan.podcastdemo.player.PlayerServiceManager


/**
 * Created by Yalan Ding on 04/23/2020
 */

open class App : CoreApp() {

    val playerServiceManager: PlayerServiceManager by inject { parametersOf(this) }

    override fun onCreate() {
        context = this
        super.onCreate()
        playerServiceManager.startService()
    }

    override fun getModules(): List<Module> {
        return arrayListOf(
            generalModule,
            viewModelModule,
            usecaseModule
        )
    }

    companion object {
        lateinit var context: Context
        fun get(): App {
            return context as App
        }
    }
}
