package com.darkndev.netkeep.di

import android.app.Application
import androidx.room.Room
import com.darkndev.netkeep.database.NetKeepDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetKeepModule {

    @Singleton
    @Provides
    fun provideHttpClient() = HttpClient(OkHttp) {
        expectSuccess = true
        install(Logging) {
            level = LogLevel.ALL
        }
        install(ContentNegotiation) {
            json()
        }
    }

    @Provides
    @Singleton
    fun provideDatabase(
        app: Application,
        callback: NetKeepDatabase.Callback
    ) = Room.databaseBuilder(app, NetKeepDatabase::class.java, "netKeep_database")
        .fallbackToDestructiveMigration()
        .addCallback(callback)
        .build()

    @Provides
    fun provideNoteDao(database: NetKeepDatabase) = database.noteDao()

    @NetKeepScope
    @Provides
    @Singleton
    fun provideNetKeepScope() = CoroutineScope(SupervisorJob())
}