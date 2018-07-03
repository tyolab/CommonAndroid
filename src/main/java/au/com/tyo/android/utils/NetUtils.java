package au.com.tyo.android.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.util.List;

import static android.content.Context.WIFI_SERVICE;

public class NetUtils extends au.com.tyo.network.NetUtils {

    public static List scanWifiNetwork(Context context, int port, NetworkScanListener networkScanListener) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(WIFI_SERVICE);
        @SuppressLint("MissingPermission") WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        int ip = wifiInfo.getIpAddress();
        byte[] bytes = toIP(ip); // BigInteger.valueOf(ip).toByteArray();
        /*
        String ipAddress = Formatter.formatIpAddress(ip);
         */
        List<byte[]> list = getPeerIPsFast(null, bytes);

       return scanLocalNetwork(list, port, networkScanListener);
    }
}
