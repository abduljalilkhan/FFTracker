package com.khan.fftracker.di;


import com.khan.fftracker.MainActivity;
import com.khan.fftracker.tracker.landing.TrackerLandingEx;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = {AppModule.class, NetworkModule.class, DaggerVmFactory.class})
public interface RetrofitComponent {

    void inject(TrackerLandingEx trackerLandingEx);
    void inject(MainActivity drawer);

  //  void inject(AutoVerseAlert autoVerseAlert);
}
