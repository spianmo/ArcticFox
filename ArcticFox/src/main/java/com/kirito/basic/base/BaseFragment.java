package com.kirito.basic.base;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.google.android.material.snackbar.Snackbar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Copyright (c) 2021
 *
 * @Project:Freya
 * @Author:Finger
 * @FileName:BaseFragment.java
 * @LastModified:2021-04-15T03:12:15.856+08:00
 */

public abstract class BaseFragment<T extends ViewBinding> extends Fragment {
    Handler mainHandler;
    protected T v;
    protected Context mContext;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mContext = getActivity();
        ParameterizedType type = (ParameterizedType) getClass().getGenericSuperclass();
        Class cls = (Class) type.getActualTypeArguments()[0];
        try {
            Method inflate = cls.getDeclaredMethod("inflate", LayoutInflater.class, ViewGroup.class, boolean.class);
            v = (T) inflate.invoke(null, inflater, container, false);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        inCreateView();
        return v.getRoot();
    }

    protected void inCreateView(){

    }

    @Override
    public void onDestroyView() {
        if (v != null) v = null;
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        v = null;
    }

    protected void runOnUI(Runnable runnable) {
        mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(runnable);
    }

    public void delay(Runnable runnable, int millisecond) {
        new Handler(Looper.getMainLooper()).postDelayed(runnable, millisecond);
    }

    public void toast(String message) {
        Toast toast = Toast.makeText(mContext, message, Toast.LENGTH_SHORT);
        runOnUI(toast::show);
    }

    public void LOGE(String log) {
        Log.e("==" + this.getClass().getName() + "==>", log);
    }

    public void LOGW(String log) {
        Log.w("==" + this.getClass().getName() + "==>", log);
    }


    public void jumpPage(Class<?> activity) {
        mContext.startActivity(new Intent(mContext, activity));
    }

    public void jumpPage(Class<?> activity, LinkedHashMap<String, String> map) {
        Intent intent = new Intent(mContext, activity);
        for (Map.Entry<String, String> entry : map.entrySet()) {
            intent.putExtra(entry.getKey(), entry.getValue());
        }
        mContext.startActivity(intent);
    }

    public Snackbar showSnackBar(String message) {
        Snackbar snackbar = Snackbar.make(v.getRoot(), message, Snackbar.LENGTH_SHORT);
        runOnUI(snackbar::show);
        return snackbar;
    }
}