package org.liushui.iphone;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class UnlockFragment extends Fragment {
    private static final String TAG = "UnlockFragment";
    private Bitmap unlockImage;    

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "TestFragment-----onCreate");
        //Bundle args = getArguments();
        Resources res = getResources();  
        unlockImage = BitmapFactory.decodeResource(res, R.drawable.unlock);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        Log.d(TAG, "TestFragment-----onCreateView");
        View view = inflater.inflate(R.layout.lay0, container, false);
        ImageView viewhello = (ImageView) view.findViewById(R.id.iv_unlock);               
        viewhello.setImageBitmap(unlockImage);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "TestFragment-----onDestroy");
    }

}
