package tw.yalan.mvvm.core.ui.base

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import tw.yalan.mvvm.core.data.DataRepository
import tw.yalan.mvvm.core.data.DataSource
import tw.yalan.mvvm.core.data.local.LocalRepository
import tw.yalan.mvvm.core.data.remote.RemoteRepository
import tw.yalan.mvvm.core.data.remote.ServiceGenerator
import kotlinx.coroutines.Dispatchers
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

/**
 * Created by Alan Ding on 2020/3/14.
 */
@CoreScope
val baseModule = module {

    fun provideGson(): Gson {
        return GsonBuilder().create()
    }

    fun provideCoroutineContext(): CoroutineContext {
        return Dispatchers.Main
    }

    fun provideSharedPreferences(applicationContext: Application): SharedPreferences {
        return applicationContext.getSharedPreferences(
            applicationContext.packageName,
            Context.MODE_PRIVATE
        )
    }

    single { provideGson() }
    single { provideCoroutineContext() }
    single { provideSharedPreferences(get()) }
}

val apiModule = module {
    fun provideServiceGenerator(gson: Gson): ServiceGenerator {
        return ServiceGenerator(gson)
    }

    single { provideServiceGenerator(get()) }
}

val repositoryModule = module {

    fun provideLocalRepository(prefs: SharedPreferences): LocalRepository {
        return LocalRepository(prefs)
    }

    fun provideRemoteRepository(serverGenerator: ServiceGenerator): RemoteRepository {
        return RemoteRepository(serverGenerator)
    }

    fun provideDataRepository(
        remoteRepository: RemoteRepository,
        localRepository: LocalRepository
    ): DataSource {
        return DataRepository(remoteRepository, localRepository)
    }
    single { provideRemoteRepository(get()) }
    single { provideLocalRepository(get()) }
    single { provideDataRepository(get(), get()) }
}