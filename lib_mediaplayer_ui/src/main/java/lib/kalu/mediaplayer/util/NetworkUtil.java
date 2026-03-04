package lib.kalu.mediaplayer.util;

import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class NetworkUtil {
    public static final String NETWORK_TYPE_WIFI = "wifi";
    public static final String NETWORK_CLASS_DISCONNECTED = "disconnected";
    public static final String NETWORK_CLASS_UNKNOWN = "unknown";
    public static final String NETWORK_CLASS_DENIED = "denied";
    public static final String NETWORK_CLASS_2G = "2g";
    public static final String NETWORK_CLASS_3G = "3g";
    public static final String NETWORK_CLASS_4G = "4g";
    public static final String NETWORK_CLASS_5G = "5g";
    public static final String UNKNOW = "";
    public static final String WIFI = "Wi-Fi";
    public static final String MOBILE_NETWORK = "2G/3G";

    public NetworkUtil() {
    }

    public static NetworkInfo getActiveNetworkInfo(Context var0) {
        NetworkInfo var1 = null;

        try {
            ConnectivityManager var4;
            if ((var4 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE)) == null) {
                // Log.w("efs.base", "get CONNECTIVITY_SERVICE is null");
                return null;
            }

            NetworkInfo[] var5;
            if (((var1 = var4.getActiveNetworkInfo()) == null || !var1.isConnected()) && (var5 = var4.getAllNetworkInfo()) != null) {
                for (int var2 = 0; var2 < var5.length; ++var2) {
                    if (var5[var2] != null && var5[var2].isConnected()) {
                        var1 = var5[var2];
                        break;
                    }
                }
            }
        } catch (Throwable var3) {
            // Log.e("efs.base", "get network info error", var3);
        }

        return var1;
    }

    public static boolean isConnected(Context var0) {
        NetworkInfo var1;
        if ((var1 = getActiveNetworkInfo(var0)) != null && var1.isConnected()) {
            return var1.getState() == NetworkInfo.State.CONNECTED;
        } else {
            return false;
        }
    }

    public static boolean isWifi(Context var0) {
        if (isRejectAccessNetworkState(var0)) {
            return false;
        } else {
            NetworkInfo var1;
            if ((var1 = getActiveNetworkInfo(var0)) != null && var1.isConnected()) {
                return var1.getType() == 1;
            } else {
                return false;
            }
        }
    }

    public static boolean hasAccessNetworkState(Context var0) {
        try {
            return var0.getPackageManager().checkPermission("android.permission.ACCESS_NETWORK_STATE", var0.getPackageName()) == PackageManager.PERMISSION_GRANTED;
        } catch (Throwable var1) {
            return false;
        }
    }

    public static boolean isRejectAccessNetworkState(Context var0) {
        return !hasAccessNetworkState(var0);
    }

    public static String getNetworkType(Context var0) {
        if (isRejectAccessNetworkState(var0)) {
            return "denied";
        } else {
            NetworkInfo var1;
            if ((var1 = getActiveNetworkInfo(var0)) == null) {
                return "disconnected";
            } else if (var1.getType() == 1) {
                return "wifi";
            } else {
                switch (var1.getSubtype()) {
                    case 1:
                    case 2:
                    case 4:
                    case 7:
                    case 11:
                        return "2g";
                    case 3:
                    case 5:
                    case 6:
                    case 8:
                    case 9:
                    case 10:
                    case 12:
                    case 14:
                    case 15:
                        return "3g";
                    case 13:
                        return "4g";
                    case 16:
                    case 17:
                    case 18:
                    case 19:
                    default:
                        String var2;
                        if (TextUtils.isEmpty(var2 = var1.getSubtypeName())) {
                            return "unknown";
                        } else {
                            if (!var2.equalsIgnoreCase("TD-SCDMA") && !var2.equalsIgnoreCase("WCDMA") && !var2.equalsIgnoreCase("CDMA2000")) {
                                return var2;
                            }

                            return "3g";
                        }
                    case 20:
                        return "5g";
                }
            }
        }
    }

    public static boolean checkPermission(Context var0, String var1) {
        boolean var2 = false;
        if (var0 == null) {
            return false;
        } else {
            if (Build.VERSION.SDK_INT >= 23) {
                try {
                    if ((Integer) Class.forName("android.content.Context").getMethod("checkSelfPermission", String.class).invoke(var0, var1) == 0) {
                        var2 = true;
                    } else {
                        var2 = false;
                    }
                } catch (Throwable var3) {
                    var2 = false;
                }
            } else if (var0.getPackageManager().checkPermission(var1, var0.getPackageName()) == PackageManager.PERMISSION_GRANTED) {
                var2 = true;
            }

            return var2;
        }
    }

    public static String[] getNetworkAccessMode(Context var0) {
        String[] var1 = new String[]{"", ""};
        if (var0 == null) {
            return var1;
        } else {
            try {
                if (!checkPermission(var0, "android.permission.ACCESS_NETWORK_STATE")) {
                    var1[0] = "";
                    return var1;
                }

                ConnectivityManager var4;
                if ((var4 = (ConnectivityManager) var0.getSystemService(Context.CONNECTIVITY_SERVICE)) == null) {
                    var1[0] = "";
                    return var1;
                }

                NetworkInfo var2;
                if ((var2 = var4.getNetworkInfo(1)) != null && var2.getState() == NetworkInfo.State.CONNECTED) {
                    var1[0] = "Wi-Fi";
                    return var1;
                }

                NetworkInfo var5;
                if ((var5 = var4.getNetworkInfo(0)) != null && var5.getState() == NetworkInfo.State.CONNECTED) {
                    var1[0] = "2G/3G";
                    var1[1] = var5.getSubtypeName();
                    return var1;
                }
            } catch (Throwable var3) {
            }

            return var1;
        }
    }
}

//public final class NetworkUtil {
//
//    public static final int NO_NETWORK = 0;
//    public static final int NETWORK_CLOSED = 1;
//    public static final int NETWORK_ETHERNET = 2;
//    public static final int NETWORK_WIFI = 3;
//    public static final int NETWORK_MOBILE = 4;
//    public static final int NETWORK_UNKNOWN = -1;
//
//    public static boolean isConnected(Context context) {
//        try {
//            ConnectivityManager connectMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//            NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
//            return networkInfo.isConnected();
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public static boolean isMobileConnected(Context context) {
//        return isConnected(context, ConnectivityManager.TYPE_MOBILE);
//    }
//
//    public static boolean isWifiConnected(Context context) {
//        return isConnected(context, ConnectivityManager.TYPE_WIFI);
//    }
//
//    public static boolean isConnected(Context context, int type) {
//        try {
//            ConnectivityManager connectMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//            if (null == connectMgr)
//                throw new Exception();
//            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                for (NetworkInfo info : connectMgr.getAllNetworkInfo()) {
//                    if (info.getType() == type) {
//                        return info.isAvailable();
//                    }
//                }
//            } else {
//                for (Network network : connectMgr.getAllNetworks()) {
//                    NetworkInfo networkInfo = connectMgr.getNetworkInfo(network);
//                    if (networkInfo.getType() == type) {
//                        return networkInfo.isAvailable();
//                    }
//                }
//            }
//            throw new Exception();
//        } catch (Exception e) {
//            return false;
//        }
//    }
//
//    public static int getNetworkType(Context context) {
//        //改为context.getApplicationContext()，防止在Android 6.0上发生内存泄漏
//        ConnectivityManager connectMgr = (ConnectivityManager) context.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
//        if (connectMgr == null) {
//            return NO_NETWORK;
//        }
//        NetworkInfo networkInfo = connectMgr.getActiveNetworkInfo();
//        if (networkInfo == null) {
//            // 没有任何网络
//            return NO_NETWORK;
//        }
//        if (!networkInfo.isConnected()) {
//            // 网络断开或关闭
//            return NETWORK_CLOSED;
//        }
//        if (networkInfo.getType() == ConnectivityManager.TYPE_ETHERNET) {
//            // 以太网网络
//            return NETWORK_ETHERNET;
//        } else if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
//            // wifi网络，当激活时，默认情况下，所有的数据流量将使用此连接
//            return NETWORK_WIFI;
//        } else if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
//            // 移动数据连接,不能与连接共存,如果wifi打开，则自动关闭
//            switch (networkInfo.getSubtype()) {
//                // 2G
//                case TelephonyManager.NETWORK_TYPE_GPRS:
//                case TelephonyManager.NETWORK_TYPE_EDGE:
//                case TelephonyManager.NETWORK_TYPE_CDMA:
//                case TelephonyManager.NETWORK_TYPE_1xRTT:
//                case TelephonyManager.NETWORK_TYPE_IDEN:
//                    // 3G
//                case TelephonyManager.NETWORK_TYPE_UMTS:
//                case TelephonyManager.NETWORK_TYPE_EVDO_0:
//                case TelephonyManager.NETWORK_TYPE_EVDO_A:
//                case TelephonyManager.NETWORK_TYPE_HSDPA:
//                case TelephonyManager.NETWORK_TYPE_HSUPA:
//                case TelephonyManager.NETWORK_TYPE_HSPA:
//                case TelephonyManager.NETWORK_TYPE_EVDO_B:
//                case TelephonyManager.NETWORK_TYPE_EHRPD:
//                case TelephonyManager.NETWORK_TYPE_HSPAP:
//                    // 4G
//                case TelephonyManager.NETWORK_TYPE_LTE:
//                    // 5G
//                    return NETWORK_MOBILE;
//            }
//        }
//        // 未知网络
//        return NETWORK_UNKNOWN;
//    }
//}
