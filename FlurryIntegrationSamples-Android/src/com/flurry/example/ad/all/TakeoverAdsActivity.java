package com.flurry.example.ad.all;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.CheckedTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.flurry.android.FlurryAdType;
import com.flurry.android.FlurryAds;
import com.flurry.android.FlurryAdSize;
import com.flurry.android.FlurryAgent;
import com.flurry.android.FlurryAdListener;


public class TakeoverAdsActivity extends Activity implements
		FlurryAdListener {
	FrameLayout adLayout;
	private final String kLogTag = "FlurryAdServingAPI";
	public static String apiKey;
	private String adSpace;
	private Context mContext;
	private Button fetchAd;
	private Button displayAd;
	
	private CheckBox isReady;
	
	
	
	
	//To test your configuration open strings.xml file and: 
	 
	//replace the API key in the flurry_api_key string with your api key
	//replace the ad spaces listed in the FlurryBannerAdSpaces and FlurryInterstitialAdSpaces string-array
	//with your ad space names 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getIntent().getExtras();
		setContentView(R.layout.takeover_ads_activity);
		mContext = TakeoverAdsActivity.this;
		apiKey = getResources().getString(R.string.flurry_api_key);
		 if(bundle.getString("AD_SPACE_NAME")!= null){
		    	adSpace = bundle.getString("AD_SPACE_NAME");
		    } else  adSpace = getResources().getString(R.string.adSpaceName);
		 
		 TextView name = (TextView)findViewById(R.id.intro);
		    name.setText(getResources().getString(R.string.takeover_title) + ": " + adSpace);
		adLayout = (FrameLayout) findViewById(R.id.adLayout);
		isReady = (CheckBox)findViewById(R.id.isAdReady);
		fetchAd = (Button) findViewById(R.id.fetch);
		fetchAd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				FlurryAds.fetchAd(mContext, adSpace, (ViewGroup) findViewById(android.R.id.content),
						FlurryAdSize.FULLSCREEN);
				Log.d(kLogTag, "fetchAd( " + adSpace + " )");
				fetchAd.setVisibility(View.VISIBLE);
				displayAd.setVisibility(View.INVISIBLE);
				
				Log.d(kLogTag, "displayAd Requested ( " + adSpace + " )");
				displayAd.setVisibility(View.VISIBLE);
				isReady.setChecked(true);
						
				}
		});
		displayAd = (Button) findViewById(R.id.display);
		displayAd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				if (FlurryAds.isAdReady(adSpace)) {
					Log.d(kLogTag, "displayAd Requested ( " + adSpace + " )");
					
					FlurryAds.displayAd(mContext, adSpace, (ViewGroup) findViewById(android.R.id.content));
					
					fetchAd.setVisibility(View.VISIBLE);
					displayAd.setVisibility(View.VISIBLE);
				}
			}
		});
		
	}

	@Override
	public void onStart() {
		super.onStart();
		try {
			Log.d(kLogTag, "onStartSession:  "+ apiKey);
			FlurryAgent.onStartSession(mContext, apiKey);
			
			FlurryAds.setAdListener(this);
		 
			FlurryAgent.setLogEnabled(true);
			FlurryAgent.setLogLevel(2);
			
			//FlurryAds.enableTestAds(true);
			adLayout = (FrameLayout) findViewById(R.id.adLayout);
			
			FlurryAds.fetchAd(mContext, adSpace, adLayout,
					FlurryAdSize.FULLSCREEN);
			Log.d(kLogTag, "fetchAd( " + adSpace + " )");
			fetchAd.setVisibility(View.VISIBLE);
			displayAd.setVisibility(View.INVISIBLE);
			isReady.setChecked(false);
		} catch (Exception e) {
			Log.e(kLogTag, e.getMessage());
		}
	}

	public void spaceDidReceiveAd(String adSpace) {
		// called when the ad has been prepared, ad can be displayed:
		Log.d(kLogTag, "spaceDidReceiveAd( " + adSpace + " )");
		
		fetchAd.setVisibility(View.VISIBLE);
		displayAd.setVisibility(View.VISIBLE);
		isReady.setChecked(true);
		
		Toast toast = Toast.makeText(mContext, "spaceDidReceiveAd",
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(kLogTag, "onEndSession");
		
		FlurryAgent.onEndSession(mContext);
	}

	@Override
	public void onAdClicked(String arg0) {
		Log.d(kLogTag, "onAdClicked( " + arg0 + " )");
		Toast toast = Toast.makeText(mContext, "onAdClicked",
				Toast.LENGTH_SHORT);
		toast.show();

	}

	@Override
	public void onAdClosed(String arg0) {
		Log.d(kLogTag, "onAdClosed( " + arg0 + " )");
		Toast toast = Toast
				.makeText(mContext, "onAdClosed", Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onAdOpened(String arg0) {
		Log.d(kLogTag, "onAdOpened( " + arg0 + " )");
		Toast toast = Toast
				.makeText(mContext, "onAdOpened", Toast.LENGTH_SHORT);
		toast.show();
		
		isReady.setChecked(false);
	}

	@Override
	public void onApplicationExit(String arg0) {
		Log.d(kLogTag, "onApplicationExit( " + arg0 + " )");
		Toast toast = Toast.makeText(mContext, "onApplicationExit",
				Toast.LENGTH_SHORT);
		toast.show();
	}

	@Override
	public void onRenderFailed(String arg0) {
		Log.d(kLogTag, "onRenderFailed( " + arg0 + " )");
		Toast toast = Toast.makeText(mContext, "onRenderFailed",
				Toast.LENGTH_SHORT);
		toast.show();

		isReady.setChecked(false);
		displayAd.setVisibility(View.INVISIBLE);
		
	}

	@Override
	public void onVideoCompleted(String arg0) {
		Log.d(kLogTag, "onVideoCompleted( " + arg0 + " )");
		Toast toast = Toast.makeText(mContext, "onVideoCompleted",
				Toast.LENGTH_SHORT);
		toast.show();

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
		isReady.setChecked(false);
		displayAd.setVisibility(View.INVISIBLE);
	}

	@Override
	public void onRendered(String arg0) {
		Log.d(kLogTag, "onRendered( " + arg0 + " )");

		
	}

}
