package com.example.modularcomposecleanarchitecture

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class
 * Entry point for Hilt dependency injection
 */
@HiltAndroidApp
class MainApplication : Application()
