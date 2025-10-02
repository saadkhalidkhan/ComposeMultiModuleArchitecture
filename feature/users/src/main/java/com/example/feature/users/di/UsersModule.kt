package com.example.feature.users.di

import com.example.feature.users.data.repository.UserRepositoryImpl
import com.example.feature.users.domain.repository.UserRepository
import com.example.network.NetworkClient
import com.example.network.api.ApiService
import com.example.network.datasource.RemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object UsersModule {
    
    @Provides
    @Singleton
    fun provideApiService(): ApiService {
        return NetworkClient.provideApiService()
    }
    
    @Provides
    @Singleton
    fun provideRemoteDataSource(apiService: ApiService): RemoteDataSource {
        return RemoteDataSource(apiService)
    }
    
    @Provides
    @Singleton
    fun provideUserRepository(remoteDataSource: RemoteDataSource): UserRepository {
        return UserRepositoryImpl(remoteDataSource)
    }
}
