package deviceinfo;

import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.os.Build;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import java.net.InetAddress;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class echoes a string called from JavaScript.
 */
public class DeviceInfo extends CordovaPlugin {

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
		TelephonyManager tm = (TelephonyManager) this.cordova.getActivity().getSystemService(Context.TELEPHONY_SERVICE);
		WifiManager wifiManager = (WifiManager) cordova.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (action.equals("getDeviceInfo")) {
            String result = this.getTelephone(tm);
			String ipAddress = this.getNet(wifiInfo);
			String macAddress = this.getMacAddress();
            if (result != null) {
				callbackContext.success("{" + result + "," + ipAddress + "," + macAddress + "}");
				return true;
			}
        }
		if (action.equals("getImei")) {
			String result = this.getImei(tm);
            if (result != null) {
				callbackContext.success(result);
				return true;
			}
		}
        return false;
    }
	
	private String checkValue(String str) {
        if ((str == null) || (str.length() == 0)) {
            return "\"TM.ERROR\"";
        }

        return "\"" + str + "\"";
    }
	
	private String getTelephone(TelephonyManager tm) {
        String str = "";

        if (tm != null) {
            str = "\"deviceID\": " + checkValue(tm.getDeviceId()) + ","
                    + "\"phoneNo\": " + checkValue(tm.getLine1Number()) + ","
                    + "\"netCountry\": " + checkValue(tm.getNetworkCountryIso()) + ","
                    + "\"netName\": " + checkValue(tm.getNetworkOperatorName()) + ","
                    + "\"simNo\": " + checkValue(tm.getSimSerialNumber()) + ","
                    + "\"simCountry\": " + checkValue(tm.getSimCountryIso()) + ","
                    + "\"simName\": " + checkValue(tm.getSimOperatorName()) + ","
                    + "\"userAgent\": " + checkValue(System.getProperty("http.agent")) + ","
                    + "\"systemBoardID\": " + checkValue(Build.BOARD) + ","
                    + "\"systemManufacturer\": " + checkValue(Build.MANUFACTURER) + ","
                    + "\"systemVersion\": " + checkValue(Build.VERSION.RELEASE) + ","
                    + "\"deviceModel\": " + checkValue(Build.MODEL) + ","
			+ "\"systemName\": " + checkValue("Android");
        }

        return str;
    }
	
	private String getImei(TelephonyManager tm) {
		String str = "";
		if (tm != null) {
			str = tm.getDeviceId();
		}
		return str;
	}
	
	private String getNet(WifiInfo wi) {
		int ip = wi.getIpAddress();
		String ipString = String.format(
			"%d.%d.%d.%d",
			(ip & 0xff),
			(ip >> 8 & 0xff),
			(ip >> 16 & 0xff),
			(ip >> 24 & 0xff)
			);
		return "\"ipAddress\": " + checkValue(ipString);
	}
	
	/**
     * Gets the mac address on version >= Marshmallow.
     * 
     * @return the mac address
     */
	private String getMMacAddress() {

        try {
            List<NetworkInterface> all = Collections.list(NetworkInterface.getNetworkInterfaces());

            for (NetworkInterface nif : all) {
                if (!nif.getName().equalsIgnoreCase("wlan0")) continue;

                byte[] macBytes = nif.getHardwareAddress();
                if (macBytes == null) {
                    return "";
                }

                StringBuilder res1 = new StringBuilder();
                for (byte b : macBytes) {
                    res1.append(String.format("%02x", (b & 0xFF)) + ":");
                }

                if (res1.length() > 0) {
                    res1.deleteCharAt(res1.length() - 1);
                }

                return res1.toString();
            }
        } catch (Exception ex) { }

        return "02:00:00:00:00:00";
    }
	
	/**
     * Gets the mac address on version < Marshmallow.
     * 
     * @return the mac address
     */
    private String getLegacyMacAddress() {

        String macAddress = null;

        WifiManager wm = (WifiManager) this.cordova.getActivity().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        macAddress = wm.getConnectionInfo().getMacAddress();

        if (macAddress == null || macAddress.length() == 0) {
            macAddress = "02:00:00:00:00:00";
        }

        return macAddress;

    }
	
	/**
     * Gets the mac address.
     * 
     * @return the mac address
     */
    private String getMacAddress() {
		String result = "";
        if (Build.VERSION.SDK_INT >= 23) { // Build.VERSION_CODES.M
            result = getMMacAddress();
        } else {
			result = getLegacyMacAddress();
        }
        return "\"macAddress\": " + checkValue(result);
    }
}
