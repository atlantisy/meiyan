package cn.minelock.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import cn.minelock.android.R;
/**
 * ±ÚÖ½
 * */
public class WallpaperAdapter extends BaseAdapter {
	private Context context;

	public WallpaperAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return wallpaper.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return wallpaper[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView image = new ImageView(context);
		image.setLayoutParams(new GridView.LayoutParams(96, 96));
		image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		image.setImageResource(wallpaper[position]);
		
		return image;				
	}

	public static int[] wallpaper = { 		
		R.color.red3,R.color.orange3,R.color.yellow2,R.color.green3,R.color.green2,
		R.color.green4,R.color.blue2,R.color.app_color,R.color.pink4,R.color.violet,
		R.color.dark1,
/*		R.color.gray_white1,R.color.dark,R.color.brown,R.color.brown1,R.color.dark_black,*/
				
	};
	
	public static int[] _wallpaper = { 		
		R.color.red3,R.color.orange3,R.color.yellow2,R.color.green3,R.color.green2,
		R.color.green4,R.color.blue2,R.color.app_color,R.color.pink4,R.color.violet,
		R.color.dark1,
/*		R.color.gray_white1,R.color.dark,R.color.brown,R.color.brown1,R.color.dark_black,*/
				
	};

}
