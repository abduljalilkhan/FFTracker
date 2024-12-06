package com.khan.fftracker.di;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;


import com.khan.fftracker.di.DaggerViewModelFactory;
import com.khan.fftracker.di.ViewModelKey;
import com.khan.fftracker.tracker.landing.viewModel.TrackerLandingVM;

import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

@Module
public abstract class DaggerVmFactory {


    @Binds
    public abstract ViewModelProvider.Factory bindViewModelFactory(DaggerViewModelFactory factory );

    @Binds
    @IntoMap
    @ViewModelKey(TrackerLandingVM.class)
    public abstract ViewModel bindMainActivityViewModel(TrackerLandingVM vm); // for every viewmodel you have in your app, you need to bind them to dagger
}
