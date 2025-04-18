package com.example.padellex.di

import com.cloudinary.Cloudinary
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CloudinaryModule {

    @Singleton
    @Provides
    fun provideCloudinary() : Cloudinary{
        val config = mapOf(
            "cloud_name" to "dey9cixgd",
            "api_key" to "977563911513672",
            "api_secret" to "2vlkcLII2snvnmwnd9w8mJwAoVM"
        )
        return Cloudinary(config)
    }

}