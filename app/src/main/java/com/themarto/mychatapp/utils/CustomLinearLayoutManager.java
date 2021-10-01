package com.themarto.mychatapp.utils;

import android.content.Context;

import androidx.recyclerview.widget.LinearLayoutManager;

/*
 This class was created to solve a problem related
 with the recycler view items animations
 */
public class CustomLinearLayoutManager extends LinearLayoutManager {
    public CustomLinearLayoutManager(Context context) {
        super(context);
    }

    @Override
    public boolean supportsPredictiveItemAnimations() {
        return false;
    }
}
