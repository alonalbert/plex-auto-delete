package com.alonalbert.pad.app.inject

import android.content.Context
import androidx.room.Room
import com.alonalbert.pad.app.database.PadDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext

@Module
@InstallIn(value = [ActivityComponent::class, ViewModelComponent::class])
object PadModule {
    @Provides
    fun providesDatabase(@ApplicationContext context: Context): PadDatabase =
        Room.databaseBuilder(context, PadDatabase::class.java, "pad-database.db").build()
}
