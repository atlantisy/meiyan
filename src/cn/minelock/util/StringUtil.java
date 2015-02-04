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
	 * 按时间生成文件名
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

		return "_" + year + addZero(month, 2) + addZero(day, 2) +
				"_" + addZero(hour, 2) + addZero(minute, 2) + addZero(second, 2);
	}
    /**
	 * 按时间生成文件名
	 * @param s
	 * @return
	 */	
	public static String makeDayName() {		
		Calendar mCalendar = Calendar.getInstance();
		// 年月日
		String year = String.valueOf(mCalendar.get(Calendar.YEAR));
		String month = String.valueOf(mCalendar.get(Calendar.MONTH)+1);
		String day = String.valueOf(mCalendar.get(Calendar.DAY_OF_MONTH));				

		return year + addZero(month, 2) + addZero(day, 2);
	}	
    /**
	 * 补零
	 * @return
	 */
	public static String addZero(String s,int num) {	
		for(int i=0; i<num-s.length(); i++)
			s="0"+s;
		return s;
	}		
    /**
	 * 字符长度不足9位，生成九宫格对称字符
	 * 
	 * @param s
	 * @return
	 */
	public static String getGridStr(String verse) {	
		String s = verse.replace(" ", "");
		s = s.replace("\n", "");
		
		int len = s.length();		
		switch (len) {
		case 0:
			s=getSpace(9);
		case 1:
			s=getSpace(4)+s+getSpace(4);
			break;
		case 2:
			s=getSpace(3)+s.substring(0, 1)+getSpace(1)+s.substring(1, 2)+getSpace(3);
			break;
		case 3:
			s=getSpace(3)+s+getSpace(3);
			break;
		case 4:
			s=getSpace(1)+s.substring(0, 1)+getSpace(1)+s.substring(1, 2)+
			  getSpace(1)+s.substring(2, 3)+getSpace(1)+s.substring(3, 4)+
			  getSpace(1);
			break;
		case 5:
			s=s.substring(0, 1)+getSpace(1)+s.substring(1, 2)+getSpace(1)+
			  s.substring(2, 3)+getSpace(1)+s.substring(3, 4)+getSpace(1)+
			  s.substring(4, 5);
			break;
		case 6:
			s=s.substring(0, 3)+getSpace(3)+s.substring(3, 6);
			break;
		case 7:
			s=s.substring(0, 3)+getSpace(1)+
			  s.substring(3, 4)+
			  getSpace(1)+s.substring(4, 7);
			break;
		case 8:
			s=s.substring(0, 4)+getSpace(1)+s.substring(4, 8);
			break;
		default:
			break;
		}

		return s;
	}
    /**
	 * @return
	 */
	public static String getSpace(int num) {	
		String s="";
		for(int i=0; i<num; i++)
			s+=" ";
		return s;
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
