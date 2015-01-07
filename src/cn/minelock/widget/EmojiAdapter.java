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
 * ฑํว้
 * */
public class EmojiAdapter extends BaseAdapter {
	private Context context;

	public EmojiAdapter(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return emoji.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return emoji[position];
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView image = new ImageView(context);
		image.setLayoutParams(new GridView.LayoutParams(45, 45));
		image.setScaleType(ImageView.ScaleType.FIT_CENTER);
		image.setImageResource(emoji[position]);
		
		return image;				
	}

	public static int[] emoji = {
		R.drawable.m01,R.drawable.m02,R.drawable.m03,R.drawable.m04,R.drawable.m05,R.drawable.m06,R.drawable.m07,
		R.drawable.m08,R.drawable.m09,R.drawable.m10,R.drawable.m11,R.drawable.m12,R.drawable.m13,R.drawable.m14,
		R.drawable.m15,R.drawable.m16,R.drawable.m17,R.drawable.m18,R.drawable.m19,R.drawable.m20,R.drawable.m21,	
	};

}
