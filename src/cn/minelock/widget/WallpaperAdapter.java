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
		//
		image.setImageResource(_wallpaper[position]);
		return image;				
	}

	public static int[] wallpaper = { 		
		R.color.red3,R.color.orange3,R.color.yellow2,R.color.green3,R.color.green2,
		R.color.app_color,R.color.green4,R.color.blue2,R.color.pink,R.color.violet,
		
		R.drawable.wallpaper02,R.drawable.wallpaper04,R.drawable.wallpaper00,
		R.drawable.wallpaper05,R.drawable.wallpaper03,
				
	};
	
	public static int[] _wallpaper = { 		
		R.color.red3,R.color.orange3,R.color.yellow2,R.color.green3,R.color.green2,
		R.color.app_color,R.color.green4,R.color.blue2,R.color.pink,R.color.violet,
		
		R.drawable._wallpaper02,R.drawable._wallpaper04,R.drawable._wallpaper00,
		R.drawable._wallpaper05,R.drawable._wallpaper03,
				
	};

}
