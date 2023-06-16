package com.alonalbert.pad.app.data.di

import android.content.Context
import androidx.room.Room
import com.alonalbert.pad.app.data.DefaultRepository
import com.alonalbert.pad.app.data.Repository
import com.alonalbert.pad.app.data.local.database.AppDatabase
import com.alonalbert.pad.app.data.local.database.LocalDataSource
import com.alonalbert.pad.app.data.local.database.RoomLocalDataSource
import com.alonalbert.pad.app.data.local.database.UserDao
import com.alonalbert.pad.app.data.network.KtorNetworkDataSource
import com.alonalbert.pad.app.data.network.NetworkDataSource
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class RepositoryModule {

    @Singleton
    @Binds
    abstract fun bindRepository(repository: DefaultRepository): Repository
}

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {

    @Singleton
    @Binds
    abstract fun bindNetworkDataSource(dataSource: KtorNetworkDataSource): NetworkDataSource

    @Singleton
    @Binds
    abstract fun bindLocalDataSource(dataSource: RoomLocalDataSource): LocalDataSource
}

@Module
@InstallIn(SingletonComponent::class)
internal object DatabaseModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "pad-database.db"
        ).build()
    }

    @Provides
    fun provideUserDao(database: AppDatabase): UserDao = database.userDao()
}
