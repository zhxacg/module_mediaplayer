package lib.kalu.mediaplayer;

import android.app.Activity;
import android.app.Application;
import android.content.ComponentCallbacks;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import lib.kalu.mediaplayer.util.LogUtil;

public class PlayerInitProvider extends ContentProvider implements Application.ActivityLifecycleCallbacks {


    private static Activity mA = null;

    public static final Activity getCurrentActivity() {
        return mA;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        mA = activity;
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        mA = null;
    }

    @Override
    public boolean onCreate() {
        try {
            ((Application) getContext()).registerActivityLifecycleCallbacks(this);
        } catch (Exception e) {
        }

        ((Application) getContext()).registerComponentCallbacks(new ComponentCallbacks() {
            @Override
            public void onConfigurationChanged(@NonNull Configuration newConfig) {
                if (LogUtil.DEBUG) {
                    LogUtil.log("PlayerInitProvider -> onConfigurationChanged -> newConfig.orientation = " + newConfig.orientation);
                }
            }

            @Override
            public void onLowMemory() {
            }
        });

        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] strings, @Nullable String s, @Nullable String[] strings1, @Nullable String s1) {
        return null;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return "";
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        return null;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
