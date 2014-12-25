package cn.minelock.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import cn.minelock.android.R;
import android.content.Context;
import android.widget.Toast;


public class StringUtil {

	private static Toast mToast;
	/**
	 * 快速切换toast
	 */
    public static void showToast(Context context, String msg, int duration) {
            // if (mToast != null) {
            // mToast.cancel();
            // }
            // mToast = Toast.makeText(context, msg, Toast.LENGTH_SHORT);
            if (mToast == null) {
                    mToast = Toast.makeText(context, msg, duration);
            } else {
                    mToast.setText(msg);
            }
            mToast.show();
    }
	/**
	 * 
	 * @param s
	 * @return
	 */
	public static String makeFileName() {		
		Calendar mCalendar = Calendar.getInstance();
		// 年月日
		String year = String.valueOf(mCalendar.get(Calendar.YEAR));
		String month = String.valueOf(mCalendar.get(Calendar.MONTH)+1);
		String day = String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH));				
		// 时分秒
		String hour = String.valueOf(mCalendar.get(Calendar.HOUR_OF_DAY));
		String minute = String.valueOf(mCalendar.get(Calendar.MINUTE));
		String second = String.valueOf(mCalendar.get(Calendar.SECOND));

		return year + month + day + hour + minute + second;
	} 
    /**
	 * 字符长度不足9位，补足
	 * 
	 * @param s
	 * @return
	 */
	public static String getNineStr(String s) {		
		int len = s.length();
		for(int i=0; i<9-len; i++){
			s = s+" ";
		}
		return s;
	}    
	/**
	 * 是否不为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNotEmpty(String s) {
		return s != null && !"".equals(s.trim());
	}

	/**
	 * 是否为空
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || "".equals(s.trim());
	}

	/**
	 * 通过{n},格式化.
	 * 
	 * @param src
	 * @param objects
	 * @return
	 */
	public static String format(String src, Object... objects) {
		int k = 0;
		for (Object obj : objects) {
			src = src.replace("{" + k + "}", obj.toString());
			k++;
		}
		return src;
	}
}
