package com.flurry.example.ad.all;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAdType;
import com.flurry.android.FlurryAds;
import com.flurry.android.FlurryAdSize;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAdListener;


public class BannerAdsActivity extends Activity implements FlurryAdListener {
	FrameLayout mBanner;
	private final String kLogTag = "FlurryAdServingAPI";
	public static String apiKey ;
	private String adSpace; 
	private Context mContext;

	
	//To test your configuration open strings.xml file and: 
	 
	//replace the API key in the flurry_api_key string with your api key
	//replace the ad spaces listed in the FlurryBannerAdSpaces and FlurryInterstitialAdSpaces string-array
	//with your ad space names 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
	   Bundle bundle = getIntent().getExtras();

	    
		setContentView(R.layout.banner_ads_activity);
		mBanner = (FrameLayout) findViewById(R.id.banner);
		mContext = BannerAdsActivity.this;
		apiKey = getResources().getString(R.string.flurry_api_key);
	    if(bundle.getString("AD_SPACE_NAME")!= null){
	    	adSpace = bundle.getString("AD_SPACE_NAME");
	    } else  adSpace = getResources().getString(R.string.adSpaceName);
	
	    TextView name = (TextView)findViewById(R.id.intro);
	    name.setText(getResources().getString(R.string.banner_title) + ": " + adSpace);
	}


	
	@Override
	public void onStart() {
		super.onStart();
		try {

			Log.d(kLogTag, "onStartSession:  "+ apiKey);
			FlurryAgent.onStartSession(mContext, apiKey);
			FlurryAgent.setLogEnabled(true);
			FlurryAgent.setLogLevel(Log.DEBUG);
			FlurryAds.setAdListener(this);
			//FlurryAds.enableTestAds(true);
			
			FlurryAds.fetchAd(mContext, adSpace, mBanner,
					FlurryAdSize.BANNER_BOTTOM);

		} catch (Exception e) {
			Log.e(kLogTag, e.getMessage());
		}
	}
    
	@Override
	public void spaceDidReceiveAd(String adSpace) {
		// called when the ad has been prepared, ad can be displayed:
		Log.d(kLogTag, "spaceDidReceiveAd( " + adSpace + " )");

		FlurryAds.displayAd(mContext, adSpace, mBanner);
		Toast toast = Toast.makeText(mContext, "Displaying Ad", Toast.LENGTH_SHORT);

		toast.show();
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(kLogTag, "onEndSession ");
		FlurryAds.removeAd(this, adSpace, mBanner);
		FlurryAds.setAdListener(null);
		FlurryAgent.onEndSession(this);
	}

	@Override
	public void onAdClicked(String arg0) {
		Log.d(kLogTag, "onAdClicked( " + arg0 + " )");

	}

	@Override
	public void onAdClosed(String arg0) {
		Log.d(kLogTag, "onAdClosed( " + arg0 + " )");
	}

	@Override
	public void onAdOpened(String arg0) {
		Log.d(kLogTag, "onAdOpened( " + arg0 + " )");

	}

	@Override
	public void onApplicationExit(String arg0) {
		Log.d(kLogTag, "onApplicationExit( " + arg0 + " )");

	}

	@Override
	public void onRenderFailed(String arg0) {
		Log.d(kLogTag, "onRenderFailed( " + arg0 + " )");
		Toast toast = Toast.makeText(mContext, "onRenderFailed",
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onVideoCompleted(String arg0) {
		Log.d(kLogTag, "onVideoCompleted( " + arg0 + " )");

	}

	@Override
	public boolean shouldDisplayAd(String arg0, FlurryAdType arg1) {
		Log.d(kLogTag, "shouldDisplayAd( " + arg0 + ", " + arg1 + " )");
		return true;
	}

	@Override
	public void spaceDidFailToReceiveAd(String arg0) {
		Log.d(kLogTag, "spaceDidFailToReceiveAd(" + arg0 + " )");
		Toast toast = Toast.makeText(mContext, "spaceDidFailToReceiveAd",
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onRendered(String arg0) {
		Log.d(kLogTag, "onRendered( " + arg0 + " )");

		
	}

}
