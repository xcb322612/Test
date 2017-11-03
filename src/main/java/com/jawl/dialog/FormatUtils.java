package com.jawl.dialog;

import android.content.Context;
import android.text.format.Formatter;

import java.text.SimpleDateFormat;

/**
 * Created by SKT on 2017/10/24.
 */

public class FormatUtils {

    /**
     * 格式化工具类
     */

    public static SimpleDateFormat mFormat = new SimpleDateFormat("yyyy-MM-dd");//格式化年月日

    /**
     * 日期格式化
     *
     * @param date 长整形时间
     * @return 格式化后的日期
     */
    public static String getDate(long date) {
        return mFormat.format(date);
    }

    /**
     * 格式化文件大小
     *
     * @param context 上下文
     * @param sizt    长整形大小
     * @return 格式化后的文件大小
     */
    public static String getFileSize(Context context, long sizt) {
        return Formatter.formatFileSize(context, Long.valueOf(sizt));
    }

}
