package com.luisrubenrodriguez.getyourguide;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.luisrubenrodriguez.getyourguide.deps.DaggerDependencies;
import com.luisrubenrodriguez.getyourguide.deps.Dependencies;


/**
 * Created by GamingMonster on 15.05.2017.
 */

public class BaseApp extends AppCompatActivity {
    Dependencies mDependencies;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDependencies = DaggerDependencies.builder().build();
    }

    public Dependencies getDependencies() {
        return mDependencies;
    }
}
