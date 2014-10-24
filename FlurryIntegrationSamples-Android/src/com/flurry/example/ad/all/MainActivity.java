package com.flurry.example.ad.all;

import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.flurry.android.FlurryAgent;

public class MainActivity extends Activity {

	private final String kLogTag = "FlurryAdServingAPI_A";
	public static String apiKey;

	private Context mContext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mContext = this;
		apiKey = getResources().getString(R.string.flurry_api_key);

		FlurryAgent.setLogEnabled(true);
		FlurryAgent.setLogLevel(Log.VERBOSE);
		
		
		//Selection of banner ad spaces. A number of differently configured ad spaces, 
		//the integration code is the same, the distinct configuration is done on the 
		//dev.flurry.com under Publishers tab, Inventory / Ad Spaces
		Spinner banner_spinner = (Spinner) findViewById(R.id.flurry_ad_spaces_banner);
		ArrayAdapter<CharSequence> banner_adapter = ArrayAdapter.createFromResource(this,
		        R.array.FlurryBannerAdSpaces, android.R.layout.simple_spinner_item);
		
		banner_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		banner_spinner.setAdapter(banner_adapter);
		banner_spinner.setOnItemSelectedListener(new OnItemSelectedListener() { 

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				if (pos != 0) {
				String name = parent.getItemAtPosition(pos).toString();
				Intent intent = new Intent();
				intent.setClass(mContext, BannerAdsActivity.class);
				intent.putExtra("AD_SPACE_NAME", name);
				startActivity(intent);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
				
			}
		});

		//Selection of takeover ad spaces. A number of differently configured ad spaces, 
		//the integration code is the same, the distinct configuration is done on the 
		//dev.flurry.com under Publishers tab, Inventory / Ad Spaces
		Spinner takeover_spinner = (Spinner) findViewById(R.id.flurry_ad_spaces_takeover);
		
		ArrayAdapter<CharSequence> takeover_adapter = ArrayAdapter.createFromResource(this,
		        R.array.FlurryInterstitialAdSpaces, android.R.layout.simple_spinner_item);
		
		takeover_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		takeover_spinner.setAdapter(takeover_adapter);
		
		takeover_spinner.setOnItemSelectedListener(new OnItemSelectedListener() { 
		 

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,
					int pos, long id) {
				if (pos != 0) {
				String name = parent.getItemAtPosition(pos).toString();
				Intent intent = new Intent();
				intent.setClass(mContext, TakeoverAdsActivity.class);
				intent.putExtra("AD_SPACE_NAME", name);
				startActivity(intent);
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});
	}

	//To test your configuration open strings.xml file and: 
	 
	//replace the API key in the flurry_api_key string with your api key
	//replace the ad spaces listed in the FlurryBannerAdSpaces and FlurryInterstitialAdSpaces string-array
	//with your ad space names
		
	@Override
	public void onStart() {
		super.onStart();
		Log.d(kLogTag, "onStartSession:  " + apiKey);
		FlurryAgent.onStartSession(this, apiKey);
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d(kLogTag, "onEndSession ");
		FlurryAgent.onEndSession(this);
	}
}
