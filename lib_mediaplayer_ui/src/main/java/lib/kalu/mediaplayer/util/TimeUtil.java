package lib.kalu.mediaplayer.util;

public final class TimeUtil {

    public static String formatTimeMillis(long v) {
        if (v <= 3600000) {
            return formatTimeMillis1(v);
        } else {
            return formatTimeMillis2(v);
        }
    }

    public static String formatTimeMillis(long v, long max) {
        if (max <= 3600000) {
            return formatTimeMillis1(v);
        } else {
            return formatTimeMillis2(v);
        }
    }

    private static String formatTimeMillis1(long v) {
        try {
            if (v < 1000) {
                return "00:00";
            }
            StringBuilder builder = new StringBuilder();
            // min
            long min = v / 60000;
            if (min < 10) {
                builder.append("0");
            }
            builder.append(min);
            builder.append(":");
            // second
            long second = (v % 60000) / 1000;
            if (second < 10) {
                builder.append("0");
            }
            builder.append(second);
            return builder.toString();
        } catch (Exception e) {
            return "00:00";
        }
    }

    private static String formatTimeMillis2(long v) {
        try {
            if (v < 1000) {
                return "00:00:00";
            }
            StringBuilder builder = new StringBuilder();
            // hour
            long hour = v / 3600000;
            if (hour < 10) {
                builder.append("0");
            }
            builder.append(hour);
            builder.append(":");
            // min
            long min = (v % 3600000) / 60000;
            if (min < 10) {
                builder.append("0");
            }
            builder.append(min);
            builder.append(":");
            // second
            long second = ((v % 3600000) % 60000) / 1000;
            if (second < 10) {
                builder.append("0");
            }
            builder.append(second);
            return builder.toString();
        } catch (Exception e) {
            return "00:00:00";
        }
    }

    /**
     * 格式化视频时长
     *
     * @param duration 视频时长，支持毫秒（如MediaMetadataRetriever获取的时长）或秒
     * @param isMillis 是否为毫秒数（true：毫秒，false：秒）
     * @return 格式化后的字符串（如02:00 或 01:02:00）
     */
    public static String formatTime(long duration, boolean isMillis) {
        // 1. 统一转换为总秒数，处理负数和异常值（避免出现-01:-20这种错误格式）
        long totalSeconds = isMillis ? duration / 1000 : duration;
        totalSeconds = Math.max(totalSeconds, 0); // 确保秒数非负

        // 2. 计算小时、分钟、剩余秒数
        long hours = totalSeconds / 3600; // 1小时=3600秒
        long minutes = (totalSeconds % 3600) / 60; // 剩余秒数转分钟
        long seconds = totalSeconds % 60; // 最终的秒数

        // 3. 根据小时是否大于0，选择不同的格式化模板（%02d表示补零为两位数字）
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
//        if (hours > 0) {
//            return String.format("%02d:%02d:%02d", hours, minutes, seconds);
//        } else {
//            return String.format("%02d:%02d", minutes, seconds);
//        }
    }

    public static String formatTime(long position, long duration, boolean isMillis) {
        return new StringBuilder()
                .append(formatTime(position, isMillis))
                .append("/")
                .append(formatTime(duration, isMillis))
                .toString();
    }
}
