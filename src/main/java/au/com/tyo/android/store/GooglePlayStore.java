/*
 * code from:
 *
 * https://stackoverflow.com/questions/34309564/how-to-get-app-market-version-information-from-google-play-store
 *
 *
 */

package au.com.tyo.android.store;

import android.content.Context;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.annotation.Nullable;

import au.com.tyo.android.AndroidMarket;
import au.com.tyo.android.services.HttpAndroid;
import au.com.tyo.services.HttpConnection;
import au.com.tyo.services.HttpPool;

public class GooglePlayStore {

    private static AndroidMarket androidMarket;

    private Context context;

    public GooglePlayStore(Context context) {
        this.context = context;

        androidMarket = new AndroidMarket(context);
    }

    /**
     * By default, we are getting this App's version
     *
     * @return
     */
    public String getAppVersion() throws Exception {
        return getAppVersion(context.getPackageName());
    }

    public String getAppVersion(String packageName) throws Exception {
        return getAppVersion(androidMarket.getMarketUrl(packageName));
    }

    /**
     *
     * @param appUrlString
     * @return
     */
    @Nullable
    public static String getAppVersionFromUrl(String appUrlString) throws Exception {
        String
                currentVersion_PatternSeq = "<div[^>]*?>Current\\sVersion</div><span[^>]*?>(.*?)><div[^>]*?>(.*?)><span[^>]*?>(.*?)</span>",
                appVersion_PatternSeq = "htlgb\">([^<]*)</s";

        HttpConnection http = HttpPool.getConnection();
        http.setHeader("User-Agent", "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6");
        String html = http.get(appUrlString);

        // Get the current version pattern sequence
        String versionString = lookupAppVersion(currentVersion_PatternSeq, html);
        if (versionString == null) return null;

        // get version from "htlgb">X.X.X</span>
        return lookupAppVersion(appVersion_PatternSeq, versionString);
    }

    @Nullable
    private static String lookupAppVersion(String patternString, String input) {
        try {
            Pattern pattern = Pattern.compile(patternString);
            if (pattern == null) return null;
            Matcher matcher = pattern.matcher(input);
            if (matcher.find()) return matcher.group(1);
        } catch (PatternSyntaxException e) {
            Log.e("GooglePlayStore", "failed to match up the version regrex", e);
        }
        return null;
    }
}
