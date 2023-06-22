package com.alonalbert.pad.app.data.di

import android.content.Context
import androidx.room.Room
import com.alonalbert.pad.app.data.settings.SettingsDao
import com.alonalbert.pad.app.data.settings.SettingsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal object SettingsModule {

    @Singleton
    @Provides
    fun provideDataBase(@ApplicationContext context: Context): SettingsDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            SettingsDatabase::class.java,
            "settings-database.db"
        ).build()
    }

    @Provides
    fun provideSettingsDao(database: SettingsDatabase): SettingsDao = database.settingsDao()
}
