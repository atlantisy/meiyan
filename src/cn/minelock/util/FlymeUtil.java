package cn.minelock.util;

import android.os.Build;

import java.lang.reflect.Method;

public final class FlymeUtil {

	public static boolean isFlyme() {
		try {
			// Invoke Build.hasSmartBar()
			final Method method = Build.class.getMethod("hasSmartBar");
			//final Method method = Class.forName("android.os.Build").getMethod("hasSmartBar");
			if(method != null){
				return true;
			}
			else if(Build.DEVICE.equals("mx2")||Build.DEVICE.equals("mx")||Build.DEVICE.equals("m9")){
				return true;
			}
			
			return false;
		} catch (final Exception e) {
			return false;
		}
	}

}


