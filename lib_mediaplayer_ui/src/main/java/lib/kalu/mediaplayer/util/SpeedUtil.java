//package lib.kalu.mediaplayer.util;
//
//import android.content.Context;
//import android.content.pm.ApplicationInfo;
//import android.content.pm.PackageManager;
//import android.net.TrafficStats;
//
//public final class SpeedUtil {
//
//    private static String UNIT_DOT = ".";
//    private static String UNIT_KB = "Kb/s";
//    private static String UNIT_MB = "Mb/s";
//    private static long lastTotalRxBytes = 0;
//    private static long lastTimeStamp = 0;
//
//    public static String getNetSpeed(Context context) {
//        int uid = getUid(context);
//        long total = getTotalRxBytes(uid);
//        long time = System.currentTimeMillis();
//        long speed = ((total - lastTotalRxBytes) * 1000 / (time - lastTimeStamp));//毫秒转换
//        lastTimeStamp = time;
//        lastTotalRxBytes = total;
//        if (speed > 1000) {
//            long a = speed / 1000;
//            long b = speed % 1000;
//            if (b <= 0) {
//                return a + UNIT_MB;
//            } else if (b >= 100) {
//                return a + UNIT_DOT + 99 + UNIT_MB;
//            } else {
//                return a + UNIT_DOT + b + UNIT_MB;
//            }
//        } else {
//            return speed + UNIT_KB;
//        }
//    }
//
//    private static long getTotalRxBytes(int uid) {
//        return TrafficStats.getUidRxBytes(uid) == TrafficStats.UNSUPPORTED ? 0 : (TrafficStats.getTotalRxBytes() / 1024);//转为KB
//    }
//
//    private static int getUid(Context context) {
//        try {
//            String packageName = context.getPackageName();
//            PackageManager pm = context.getPackageManager();
//            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
//            return ai.uid;
//        } catch (Exception e) {
//            return 0;
//        }
////        try {
////
////            String packageName = context.getPackageName();
////            PackageManager pm = context.getPackageManager();
////            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_ACTIVITIES);
////            return ai.uid;
////        } catch (Exception e) {
////            return -1;
////        }
//    }
//}
