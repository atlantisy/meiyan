package cn.minelock.android;

import com.meiyanlock.android.R;

//import android.app.Fragment;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class CameraFragment extends Fragment {
    private static final String TAG = "CameraFragment";
    private Bitmap cameraImage;    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "CameraFragment-----onCreate");

        Resources res = getResources();  
        cameraImage = BitmapFactory.decodeResource(res, R.drawable.blank);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.d(TAG, "CameraFragment-----onCreateView");
        View view = inflater.inflate(R.layout.frag_camera, container, false);
        ImageView viewCamera = (ImageView) view.findViewById(R.id.iv_camera);               
        viewCamera.setImageBitmap(cameraImage);
        //viewhello.setBackgroundColor(Color.argb(0xff, 0xff, 0xff, 0xff));
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "CameraFragment-----onDestroy");
    }
    

}
