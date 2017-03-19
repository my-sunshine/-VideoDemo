package com.hqgj.myapplication.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.telephony.TelephonyManager;

/**
 * 网络相关工具栏
 *
 * Created by bumu-zhz on 2015/12/11.
 */
public class NetWorkUtil {

	public static final int  TYPE_UNKNOWN = 0;//未知上网模式
	public static final int  TYPE_WIFI = 1;//WIFI上网模式
	public static final int  TYPE_2G = 2;//2G上网模式
	public static final int  TYPE_3G = 3;//3G上网模式
	public static final int  TYPE_4G = 4;//4G上网模式
	/**
	 * @return 网络是否连接可用
	 */
	public static boolean isNetworkConnected(Context context) {
		ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkinfo = connManager.getActiveNetworkInfo();
		if (networkinfo != null) {
			return networkinfo.isConnected();
		}
		return false;
	}

	/**
	 * 判断当前网络是否是wifi网络
	 * @param ctx
	 * @return
	 */
	public static boolean isNetWifi(Context ctx){
		int netType = getNetworkClass(ctx);
		if(netType == TYPE_WIFI){
			return true;
		}
		return false;
	}

	/**
	 * 获取网络类型
	 *
	 * @return
	 */
	public static int getNetworkClass(Context ctx) {
		int networkType = TYPE_UNKNOWN;
		try {
			NetworkInfo network = ((ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
			if (network != null && network.isAvailable() && network.isConnected()) {
				int type = network.getType();
				if (type == ConnectivityManager.TYPE_WIFI) {
					networkType = TYPE_WIFI;
				} else if (type == ConnectivityManager.TYPE_MOBILE) {
					String strSubTypeName = network.getSubtypeName();
					int subtype = network.getSubtype();
					switch (subtype) {
						case TelephonyManager.NETWORK_TYPE_GPRS:
						case TelephonyManager.NETWORK_TYPE_EDGE:
						case TelephonyManager.NETWORK_TYPE_CDMA:
						case TelephonyManager.NETWORK_TYPE_1xRTT:
						case TelephonyManager.NETWORK_TYPE_IDEN: //api<8 : replace by 11
							networkType = TYPE_2G;
							break;
						case TelephonyManager.NETWORK_TYPE_UMTS:
						case TelephonyManager.NETWORK_TYPE_EVDO_0:
						case TelephonyManager.NETWORK_TYPE_EVDO_A:
						case TelephonyManager.NETWORK_TYPE_HSDPA:
						case TelephonyManager.NETWORK_TYPE_HSUPA:
						case TelephonyManager.NETWORK_TYPE_HSPA:
						case TelephonyManager.NETWORK_TYPE_EVDO_B: //api<9 : replace by 14
						case TelephonyManager.NETWORK_TYPE_EHRPD:  //api<11 : replace by 12
						case TelephonyManager.NETWORK_TYPE_HSPAP:  //api<13 : replace by 15
							networkType = TYPE_3G;
							break;
						case TelephonyManager.NETWORK_TYPE_LTE:    //api<11 : replace by 13
							networkType = TYPE_4G;
							break;
						default:
							//中国移动 联通 电信 三种3G制式
							if ("TD-SCDMA".equalsIgnoreCase(strSubTypeName) || "WCDMA".equalsIgnoreCase(strSubTypeName) || "CDMA2000".equalsIgnoreCase(strSubTypeName)){
								networkType = TYPE_3G;
							}
							break;
					}
				}
			}
		} catch (Exception ex) {
		}
		return networkType;
	}
}
