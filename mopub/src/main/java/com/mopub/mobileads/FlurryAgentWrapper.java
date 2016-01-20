package com.mopub.mobileads;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import com.flurry.android.FlurryAgent;

import java.util.WeakHashMap;

/**
 * PLEASE NOTE:
 *
 * This class is not required for Flurry Analytics users and may safely be deleted
 * for apps that already integrate Analytics. However, if removing this file, please
 * add FlurryAgent.addOrigin("Flurry_Mopub_Android", "6.1.0") before calling
 * {@link FlurryAgent.init(Context, String)} in your code.
 */
public final class FlurryAgentWrapper {
    private static FlurryAgentWrapper sWrapper;

    public static synchronized FlurryAgentWrapper getInstance() {
        if (sWrapper == null) {
            sWrapper = new FlurryAgentWrapper();
        }

        return sWrapper;
    }

    private final WeakHashMap<Context, Integer> mContextMap = new WeakHashMap<>();

    private FlurryAgentWrapper() {
        FlurryAgent.setLogEnabled(false);
        FlurryAgent.setLogLevel(Log.INFO);
        FlurryAgent.addOrigin("Flurry_Mopub_Android", "6.2.0");
    }

    public synchronized void onStartSession(Context context, String apiKey) {
        // validate parameters
        if (context == null || TextUtils.isEmpty(apiKey)) {
            return;
        }

        // init
        FlurryAgent.init(context, apiKey);

        // sessions are automatic on ICS+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return;
        }

        if (mContextMap.get(context) != null) {
            int refCount = mContextMap.get(context);
            mContextMap.put(context, ++refCount);
        }
        else {
            mContextMap.put(context, 1);
            FlurryAgent.onStartSession(context);
        }
    }

    public synchronized void onEndSession(Context context) {
        // validate parameters
        if (context == null) {
            return;
        }

        // sessions are automatic on ICS+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return;
        }

        if (mContextMap.get(context) != null) {
            int refCount = mContextMap.get(context);
            if (--refCount == 0) {
                mContextMap.remove(context);
                FlurryAgent.onEndSession(context);
            }
            else {
                mContextMap.put(context, refCount);
            }
        }
    }
}