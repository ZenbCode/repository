package com.example.one;

import com.example.one.Show.image_listener;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class showimage extends Activity {

	private ImageView imageView=null;
	private ZoomImageView zoomImageView;
	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		// TODO Auto-generated method stub
		return super.dispatchKeyEvent(event);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.imageshow);
		zoomImageView = (ZoomImageView) findViewById(R.id.zoom_image_view); 
		Intent data = getIntent();
     	String imagePath = data.getExtras().getString("imagePath");
     	Bitmap  bm=BitmapTool.getBitmap(imagePath,650,850);
        zoomImageView.setImageBitmap(bm);  
        Toast.makeText(getApplicationContext(), "按返回键退出大图模式",Toast.LENGTH_SHORT).show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		return super.onKeyDown(keyCode, event);
	}
	

}
