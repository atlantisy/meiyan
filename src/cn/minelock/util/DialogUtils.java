package cn.minelock.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;

/** 
 * @version 1.0
 */
public class DialogUtils 
{
	/**
	 * @param
	 * @param
	 */
	public static void dialogBuilder( Activity instance, String title, 
			String message,
			final DialogCallBack callBack)
	{
        AlertDialog.Builder builder = new Builder(instance);
        builder.setMessage( message );  
        builder.setTitle( title );  
        builder.setPositiveButton("确认",  
        new android.content.DialogInterface.OnClickListener() 
        {  
            public void onClick(DialogInterface dialog, int which) {  
                dialog.dismiss();  
                callBack.callBack();
            }  
        }); 
        
        builder.setNegativeButton("取消",  
        new android.content.DialogInterface.OnClickListener() 
        {  
            public void onClick(DialogInterface dialog, int which) { 
                dialog.dismiss();  
            }  
        }); 
        
        builder.create().show(); 
	}
	
	public interface DialogCallBack
	{
		public void callBack();
	}
}