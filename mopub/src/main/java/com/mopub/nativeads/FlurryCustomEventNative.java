package com.mopub.nativeads;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.flurry.android.ads.FlurryAdNative;
import com.mopub.mobileads.FlurryAgentWrapper;

import java.util.Map;

public class FlurryCustomEventNative extends CustomEventNative {

    private static final String kLogTag = FlurryCustomEventNative.class.getSimpleName();
    private static final String FLURRY_APIKEY = "apiKey";
    private static final String FLURRY_ADSPACE = "adSpaceName";

    @Override
    protected void loadNativeAd(final Context context,
                                final CustomEventNativeListener customEventNativeListener,
                                final Map<String, Object> localExtras,
                                final Map<String, String> serverExtras) {

        final String flurryApiKey;
        final String flurryAdSpace;

        if (context == null) {
            Log.e(kLogTag, "Context cannot be null.");
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (customEventNativeListener == null) {
            Log.e(kLogTag, "CustomEventNativeListener cannot be null.");
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }

        if (!(context instanceof Activity)) {
            Log.e(kLogTag, "Ad can be rendered only in Activity context.");
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            return;
        }


        //Get the FLURRY_APIKEY and FLURRY_ADSPACE from the server.
        if (validateExtras(serverExtras)) {
            flurryApiKey = serverExtras.get(FLURRY_APIKEY);
            flurryAdSpace = serverExtras.get(FLURRY_ADSPACE);

            // Not needed for Flurry Analytics users
            FlurryAgentWrapper.getInstance().onStartSession(context, flurryApiKey);
        } else {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            Log.i(kLogTag, "Failed Native AdFetch: Missing required server extras [FLURRY_APIKEY and/or FLURRY_ADSPACE].");
            return;
        }

        final FlurryForwardingNativeAd mflurryForwardingNativeAd =
                new FlurryForwardingNativeAd(context, new FlurryAdNative(context, flurryAdSpace), customEventNativeListener);
        mflurryForwardingNativeAd.fetchAd();
    }

    private boolean validateExtras(final Map<String, String> serverExtras) {
        final String flurryApiKey = serverExtras.get(FLURRY_APIKEY);
        final String flurryAdSpace = serverExtras.get(FLURRY_ADSPACE);
        Log.i(kLogTag, "ServerInfo fetched from Mopub " + FLURRY_APIKEY + " : " + flurryApiKey + " and " + FLURRY_ADSPACE + " :" + flurryAdSpace);
        return ((flurryApiKey != null && flurryApiKey.length() > 0) && (flurryAdSpace != null && flurryAdSpace.length() > 0));
    }

}