package com.example.padellexadmin.di

import com.example.padellexadmin.Repositories.CourtsRepository
import com.example.padellexadmin.Repositories.TimeSlotsRepository
import com.example.padellexadmin.Repositories.UserBookingRepository
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
    fun provideCourtsRepository(db : FirebaseDatabase) : CourtsRepository = CourtsRepository(db)

    @Singleton
    @Provides
    fun provideTimeSlotsRepository(db: FirebaseDatabase) : TimeSlotsRepository = TimeSlotsRepository(db)

    @Singleton
    @Provides
    fun provideUserBookingRepository(db: FirebaseDatabase) : UserBookingRepository = UserBookingRepository(db)
}