package com.luisrubenrodriguez.getyourguide.service;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by GamingMonster on 15.05.2017.
 */
@Module
public class GYGApiModule {

    @Provides
    @Singleton
    Retrofit provideCall() {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new MockInterceptor()).build();
        return new Retrofit.Builder()
                .baseUrl(GYGApi.BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    GYGApi providesGYGApi(Retrofit retrofit) {
        return retrofit.create(GYGApi.class);
    }

    @Provides
    @Singleton
    GYGService providesService(GYGApi gygApi) {
        return new GYGService(gygApi);
    }

}
