package tw.yalan.mvvm.core

import android.app.Application
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import com.nuwarobotics.service.IClientId
import com.nuwarobotics.service.agent.NuwaRobotAPI
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import org.koin.core.module.Module
import timber.log.Timber
import timber.log.Timber.DebugTree
import tw.yalan.mvvm.core.ui.base.CoreScope
import tw.yalan.mvvm.core.ui.base.apiModule
import tw.yalan.mvvm.core.ui.base.baseModule
import tw.yalan.mvvm.core.ui.base.repositoryModule


/**
 * Created by Yalan Ding on 02/01/2020
 */

@CoreScope
abstract class CoreApp : Application() {


    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        initKoin()
        if (BuildConfig.DEBUG) {
            Timber.plant(DebugTree())
        }
    }

    open fun initKoin() {
        startKoin {
            androidContext(this@CoreApp)
            androidLogger(Level.DEBUG)
            modules(
                listOf(
                    baseModule,
                    repositoryModule,
                    apiModule
                ) + getModules()
            )
        }
    }


    abstract fun getModules(): List<Module>

    companion object {
        lateinit var context: Context
        fun get(): CoreApp {
            return context as CoreApp
        }
    }
}
