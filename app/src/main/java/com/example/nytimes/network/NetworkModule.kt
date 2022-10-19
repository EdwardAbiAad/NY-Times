package com.example.nytimes.network

import android.content.Context
import com.example.nytimes.BuildConfig
import com.example.nytimes.network.service.NetworkService
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object NetworkModule {

    @Singleton
    @Provides
    fun provideNetworkService(
        retrofit: Retrofit
    ): NetworkService {
        return retrofit.create(NetworkService::class.java)
    }

    @Singleton
    @Provides
    fun provideRetrofit(
        gson: Gson,
        httpClient: OkHttpClient
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://${BuildConfig.BASE_HOST}/")
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(httpClient)
            .build()
    }

    @Singleton
    @Provides
    fun provideJsonConverter(): Gson {
        return GsonBuilder().setLenient().create()
    }

    @Singleton
    @Provides
    fun provideHttpClient(
        @ApplicationContext context: Context,
        loggingInterceptor: HttpLoggingInterceptor,
    ): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .retryOnConnectionFailure(true)
            .addInterceptor(loggingInterceptor)

        return builder.build()
    }


    @Singleton
    @Provides
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            if (BuildConfig.DEBUG) {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }
        }
    }

}