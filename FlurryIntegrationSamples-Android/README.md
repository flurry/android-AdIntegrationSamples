FlurryIntegrationSamples-Android
================================
Detailed integration instructions available on 
http://support.flurry.com/index.php?title=Publisher/Code/Android
and
http://support.flurry.com/index.php?title=Publisher/GettingStarted

This sample app lists a number  of banner and  takeover ad spaces. Each ad space is configured differently on the dev.flurry.com under Publishers tab, Inventory / Ad Spaces.

The integration code is the same regardless of the ad space configuration.


To test your configuration open strings.xml file and: 
     
* replace the API key in the flurry_api_key string with your API key
* replace the ad spaces listed in the FlurryBannerAdSpaces and FlurryInterstitialAdSpaces string-array with your ad space name(s). Once you replace the API key, please make sure to remove all the ad spaces listed in the string array too.
        