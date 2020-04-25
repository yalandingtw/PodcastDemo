package tw.yalan.mvvm.core.data.remote

import com.google.gson.Gson
import tw.yalan.mvvm.core.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import tw.yalan.mvvm.core.net.IgnoreMultipartHttpLoggingInterceptor
import java.util.concurrent.TimeUnit

/**
 * Created by Yalan Ding on 02/01/2020
 */

class ServiceGenerator
constructor(private val gson: Gson) {

    //Network constants
    private val TIMEOUT_CONNECT = 30   //In seconds
    private val TIMEOUT_READ = 30   //In seconds
    private val CONTENT_TYPE = "Content-Type"
    private val CONTENT_TYPE_VALUE = "application/json; charset=utf-8"

    private val okHttpBuilder: OkHttpClient.Builder = OkHttpClient.Builder()
    private var retrofit: Retrofit? = null

    private var headerInterceptor = Interceptor { chain ->
        val original = chain.request()

        val request = original.newBuilder()
            .header(CONTENT_TYPE, CONTENT_TYPE_VALUE)
            .method(original.method, original.body)
            .build()

        chain.proceed(request)
    }

    private val logger: IgnoreMultipartHttpLoggingInterceptor
        get() {
            val loggingInterceptor =
                IgnoreMultipartHttpLoggingInterceptor()
            if (BuildConfig.DEBUG) {
                loggingInterceptor.apply {
                    loggingInterceptor.level = IgnoreMultipartHttpLoggingInterceptor.Level.HEADERS
                }.level = IgnoreMultipartHttpLoggingInterceptor.Level.BODY
            }
            return loggingInterceptor
        }

    init {
        okHttpBuilder.addInterceptor(headerInterceptor)
        okHttpBuilder.addInterceptor(logger)
        okHttpBuilder.connectTimeout(TIMEOUT_CONNECT.toLong(), TimeUnit.SECONDS)
        okHttpBuilder.readTimeout(TIMEOUT_READ.toLong(), TimeUnit.SECONDS)
    }

    fun <S> createService(serviceClass: Class<S>, baseUrl: String): S {
        val client = okHttpBuilder.build()
        retrofit = Retrofit.Builder()
            .baseUrl(baseUrl).client(client)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
        return retrofit!!.create(serviceClass)
    }
}