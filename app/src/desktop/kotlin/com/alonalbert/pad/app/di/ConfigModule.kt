package com.alonalbert.pad.app.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {

    @Provides
    @ServerUrl
    fun providesServerUrl(): String = "http://10.0.0.74:8080/api"
}
