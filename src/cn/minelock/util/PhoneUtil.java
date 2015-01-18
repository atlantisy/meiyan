package cn.minelock.util;

import android.os.Build;

import java.io.IOException;
import java.lang.reflect.Method;

public final class PhoneUtil {

	private static final String KEY_MIUI_VERSION_CODE = "ro.miui.ui.version.code";
	private static final String KEY_MIUI_VERSION_NAME = "ro.miui.ui.version.name";
	private static final String KEY_MIUI_INTERNAL_STORAGE = "ro.miui.internal.storage";

	public static boolean isMIUI() {
		try {
			final BuildProperties prop = BuildProperties.newInstance();
			return prop.getProperty(KEY_MIUI_VERSION_CODE, null) != null
					|| prop.getProperty(KEY_MIUI_VERSION_NAME, null) != null
					|| prop.getProperty(KEY_MIUI_INTERNAL_STORAGE, null) != null;
		} catch (final IOException e) {
			return false;
		}
	}
	
	public static String getMIUIVersion() {
		try {
			final BuildProperties prop = BuildProperties.newInstance();
			if(isMIUI())
				return prop.getProperty(KEY_MIUI_VERSION_NAME, null);
			else
				return "";
		} catch (IOException e) {
			return "";
		}					
	}
	
	public static boolean isFlyme() {
		try {
/*			final Method method = Build.class.getMethod("hasSmartBar");			
			//final Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
			if(method != null){
				return true;
			}*/
			
			return Build.MANUFACTURER.equals("Meizu")||Build.MANUFACTURER.equals("MEIZU")
					||Build.MANUFACTURER.equals("meizu");
			
		} catch (final Exception e) {
			return false;
		}
	}
	
	public static boolean isHuawei() {
		try {
			return Build.MANUFACTURER.equals("Huawei")||Build.MANUFACTURER.equals("HUAWEI")
					||Build.MANUFACTURER.equals("huawei");
			
		} catch (final Exception e) {
			return false;
		}
	}
	


}


