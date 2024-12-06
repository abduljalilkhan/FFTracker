package com.khan.fftracker.di;


import com.khan.fftracker.Prefrences.Prefs_Operation;
import com.khan.fftracker.tracker.aApiTracker.TrackerApi;

import javax.inject.Inject;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class NetworkModule {
    private static String BASE_URL = "https://example/";
    @Inject
    HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
    @Inject
    OkHttpClient okHttpClient=new OkHttpClient();
    @Provides
    @Singleton
    HttpLoggingInterceptor provideHttpLogInt(){
        HttpLoggingInterceptor httpLoggingInterceptor=new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        return httpLoggingInterceptor;
    }

    @Provides
    @Singleton
    OkHttpClient provideOkHttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(chain -> {
            Request newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", Prefs_Operation.readPrefs("AUTH_TOKEN", ""))
                    .build();
            return  chain.proceed(newRequest);
        });

        builder.addInterceptor(interceptor );
        return builder.build();
    }

    @Provides
    @Singleton
    Retrofit provideRetrofit(){
        return new Retrofit.Builder().
                client(okHttpClient).
                baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create()).
                build();
    }

    @Provides
    @Singleton
    TrackerApi provideTrackerApi(Retrofit retrofit){
        return retrofit.create(TrackerApi.class);
    }

}




