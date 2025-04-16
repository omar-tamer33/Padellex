package com.example.padellex.di

import com.example.padellex.Repositories.TimeSlotsRepository
import com.example.padellex.Repositories.UserBookingRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoriesModule {

    @Singleton
    @Provides
    fun provideTimeSlotsRepository(db : FirebaseDatabase) : TimeSlotsRepository = TimeSlotsRepository(db)

    @Singleton
    @Provides
    fun provideUserBookingRepository(db: FirebaseDatabase) : UserBookingRepository = UserBookingRepository(db)
}