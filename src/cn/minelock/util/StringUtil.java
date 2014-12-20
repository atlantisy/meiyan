package cn.minelock.util;

import cn.minelock.android.R;
import android.content.Context;
import android.widget.Toast;


public class StringUtil {

	private static Toast mToast;
	/**
	 * �����л�toast
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
	 * �ַ����Ȳ���9λ������
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
	 * �Ƿ�Ϊ��
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isNotEmpty(String s) {
		return s != null && !"".equals(s.trim());
	}

	/**
	 * �Ƿ�Ϊ��
	 * 
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		return s == null || "".equals(s.trim());
	}

	/**
	 * ͨ��{n},��ʽ��.
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
