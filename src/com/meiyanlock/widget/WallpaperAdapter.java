package com.meiyanlock.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import com.meiyanlock.android.R;
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
		image.setLayoutParams(new GridView.LayoutParams(80, 80));
		image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		//
		image.setImageResource(wallpaper[position]);
		return image;				
	}

	public static int[] wallpaper = { 		
/*		R.drawable.wallpaper00,R.drawable.wallpaper01,R.drawable.wallpaper02,
		R.drawable.wallpaper03,R.drawable.wallpaper04,R.drawable.wallpaper05,
		R.drawable.wallpaper06,R.drawable.wallpaper07,R.drawable.wallpaper08,
		R.drawable.wallpaper09,R.drawable.wallpaper10,R.drawable.wallpaper11,
		R.drawable.wallpaper12,R.drawable.wallpaper13,R.drawable.wallpaper14,
		R.drawable.wallpaper15,R.drawable.wallpaper16,R.drawable.wallpaper17*/
		
		R.color.red,R.color.orange,R.color.orange1,
		R.color.yellow,R.color.green,R.color.green1,
		R.color.blue,R.color.blue1,R.color.violet,
		R.color.app_color
	};

}
