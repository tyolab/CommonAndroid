package au.com.tyo.android;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;

public class AndroidMarket {
	
	public static final String VENDOR_GOOGLE_PLAY_STORE = "com.android.vending";
	
	public static final String VENDOR_AMAZON_MARKET = "com.amazon.venezia";
	
	public static final String MARKET_URL_AMAZON = "www.amazon.com/gp/mas/dl/android?p=";
	
	public static final String MARKET_URL_GOOGLE = "play.google.com/store/apps/details?id=";
	
	public static final String MARKET_URL_DEFAULT = "market://details?id=";
	
	public static final String[] MARKET_URLS = new String[] {MARKET_URL_DEFAULT, MARKET_URL_GOOGLE, MARKET_URL_AMAZON};
	
	private Context context;
	
	private String vendorStr;
	
	private String base64PublicKey;
	
	public AndroidMarket(Context context) {
		this.context = context;
		
		PackageManager pm = context.getPackageManager();
		vendorStr = pm.getInstallerPackageName(context.getPackageName());
		if (vendorStr == null)
			vendorStr = "";
		this.setBase64PublicKey(context.getResources().getString(R.string.base_64_encoded_public_key));
	}
	
	public boolean isFromGooglePlayStore() {
		return vendorStr.equals(VENDOR_GOOGLE_PLAY_STORE);
	}
	
	public boolean isFromAmazonMarket() {
		return vendorStr.equals(VENDOR_AMAZON_MARKET);
	}
	
	public boolean isSideLoaded() {
		return vendorStr == null || vendorStr.length() == 0;
	}

	public String getBase64PublicKey() {
		return base64PublicKey;
	}

	public void setBase64PublicKey(String base64PublicKey) {
		this.base64PublicKey = base64PublicKey;
	}

	public void goToMarketById(String packageName) {
		Uri marketUri = null;
		
		try {
			marketUri = Uri.parse(MARKET_URL_DEFAULT + packageName);
		}
		catch (Exception ex) {}

		if (marketUri != null) {
			final Uri uri = marketUri;
            Intent marketIntent = new Intent(Intent.ACTION_VIEW, uri);
            marketIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(marketIntent);
		}
	}
	
	public String getMarketUrl(String packageName) {
		String url = null;
		if (isFromAmazonMarket())
			url = "http://" + MARKET_URL_AMAZON + packageName;
		else {
			url = MARKET_URL_DEFAULT + packageName;
		}
		return url;
	}
	
	public static String getGooglePlayHttpUrl(String packageName) {
		return "http://" + MARKET_URL_GOOGLE + packageName;
	}
	
	public static boolean isMarketUrl(String url) {
		for (String marketUrl : MARKET_URLS)
			if (url.contains(marketUrl))
				return true;
		return false;
	}
	
	public static String getPackageNameFromMarketUrl(String url) {
		String packageName = "";
		try {
			packageName = url.substring(url.indexOf('=') + 1);
		}
		catch (Exception ex){
			
		}
		return packageName;
	}
}
