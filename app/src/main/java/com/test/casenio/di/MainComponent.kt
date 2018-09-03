package com.test.casenio.di

import com.test.casenio.MainActivity
import dagger.Component

@Component(modules = [MainActivityModule::class])
@ApplicationScope
interface MainComponent {
    fun inject(activity: MainActivity)
}