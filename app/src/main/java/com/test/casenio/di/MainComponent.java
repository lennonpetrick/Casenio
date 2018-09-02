package com.test.casenio.di;

import com.test.casenio.MainActivity;

import dagger.Component;

@Component(modules = MainActivityModule.class)
@ApplicationScope
public interface MainComponent {
    void inject(MainActivity activity);
}
