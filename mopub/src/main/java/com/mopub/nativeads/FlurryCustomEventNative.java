package com.mopub.nativeads;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import com.flurry.android.ads.FlurryAdNative;
import com.mopub.mobileads.FlurryAgentWrapper;

import java.util.Map;

public class FlurryCustomEventNative extends CustomEventNative {

    private static final String kLogTag = FlurryCustomEventNative.class.getSimpleName();
    private static final String FLURRY_APIKEY = "apiKey";
    private static final String FLURRY_ADSPACE = "adSpaceName";

    @Override
    protected void loadNativeAd(@NonNull final Activity activity,
                                @NonNull final CustomEventNativeListener customEventNativeListener,
                                @NonNull final Map<String, Object> localExtras,
                                @NonNull final Map<String, String> serverExtras) {

        final String flurryApiKey;
        final String flurryAdSpace;

        //Get the FLURRY_APIKEY and FLURRY_ADSPACE from the server.
        if (validateExtras(serverExtras)) {
            flurryApiKey = serverExtras.get(FLURRY_APIKEY);
            flurryAdSpace = serverExtras.get(FLURRY_ADSPACE);

            // Not needed for Flurry Analytics users
            FlurryAgentWrapper.getInstance().onStartSession(activity, flurryApiKey);
        } else {
            customEventNativeListener.onNativeAdFailed(NativeErrorCode.NATIVE_ADAPTER_CONFIGURATION_ERROR);
            Log.i(kLogTag, "Failed Native AdFetch: Missing required server extras [FLURRY_APIKEY and/or FLURRY_ADSPACE].");
            return;
        }

        final FlurryStaticNativeAd mflurryStaticNativeAd =
                new FlurryStaticNativeAd(activity,
                        new FlurryAdNative(activity, flurryAdSpace), customEventNativeListener);
        mflurryStaticNativeAd.fetchAd();
    }

    private boolean validateExtras(final Map<String, String> serverExtras) {
        final String flurryApiKey = serverExtras.get(FLURRY_APIKEY);
        final String flurryAdSpace = serverExtras.get(FLURRY_ADSPACE);
        Log.i(kLogTag, "ServerInfo fetched from Mopub " + FLURRY_APIKEY + " : "
                + flurryApiKey + " and " + FLURRY_ADSPACE + " :" + flurryAdSpace);
        return ((flurryApiKey != null && flurryApiKey.length() > 0)
                && (flurryAdSpace != null && flurryAdSpace.length() > 0));
    }

}