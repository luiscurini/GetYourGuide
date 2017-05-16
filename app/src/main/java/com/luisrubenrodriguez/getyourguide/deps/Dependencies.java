package com.luisrubenrodriguez.getyourguide.deps;

import com.luisrubenrodriguez.getyourguide.MainActivity;
import com.luisrubenrodriguez.getyourguide.service.GYGApiModule;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by GamingMonster on 15.05.2017.
 */


@Singleton
@Component(modules = {GYGApiModule.class})
public interface Dependencies {
    void inject(MainActivity mainActivity);

}
