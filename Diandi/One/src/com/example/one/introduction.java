package com.example.one;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Toast;

public class introduction extends Activity {
    private Gallery mGallery;
    
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.intro);
        int j=0;
        mGallery = (Gallery)findViewById(R.id.gallery);
        mGallery.setAdapter(new ImageAdapter(this));
        mGallery.getItemAtPosition(6);
        mGallery.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                 if (position==5) {
     				Intent intent1=new Intent(introduction.this,MainActivity.class);
     				startActivity(intent1);
     				finish();
     			}
            }
        });
        
    }
    
    /*
    * class ImageAdapter is used to control gallery source and operation.
    */
    public class ImageAdapter extends BaseAdapter{
        public Context mContext;
        public Integer[] mImage = {
             R.drawable.one,
             R.drawable.two,
             R.drawable.three,
             R.drawable.four,
             R.drawable.five,
             R.drawable.six,
             
        };
        
        public ImageAdapter(Context c){
            mContext = c;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
        	System.out.print("这是mImage.length返回的值:"+mImage.length);
            return mImage.length;
            
        }

       
        @Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		

        @Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}
		@Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
			
            ImageView i = new ImageView (mContext);
            i.setImageResource(mImage[position]);
            i.setScaleType(ImageView.ScaleType.FIT_XY);
            i.setLayoutParams(new Gallery.LayoutParams(480,854));
           
            return i;
        }
        
    };
}