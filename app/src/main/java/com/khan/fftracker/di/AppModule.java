package com.khan.fftracker.di;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    Application mApplication;

    public AppModule(Application application) {
        mApplication = application;
    }
    @Singleton
    @Provides
    public Application provideApp(){
        return mApplication;
    }
}
