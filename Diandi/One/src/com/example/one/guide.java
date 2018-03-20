package com.example.one;

import java.util.Timer;
import java.util.TimerTask;

import com.example.one.introduction;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

public class guide extends Activity {


	private TextView diandi=null;
	private TextView left=null;
	private TextView right=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.guide);
		
		diandi = (TextView)findViewById(R.id.diandi);
		left = (TextView)findViewById(R.id.left);
		right = (TextView)findViewById(R.id.right);
		
	    	 //设置字体
			 Typeface tff=Typeface.createFromAsset(getAssets(), "xingkai.ttf");
			 diandi.setTypeface(tff);
			 Typeface tfff=Typeface.createFromAsset(getAssets(), "kai.ttf");
			 left.setTypeface(tfff);
			 right.setTypeface(tfff);
		
		 SharedPreferences preferences = getSharedPreferences("data", 0);
         int count = preferences.getInt("count", 0);
         if (count == 0) {
         	Editor sharedata = getSharedPreferences("data", 0).edit();
             sharedata.putInt("count", 1);
             sharedata.commit();
             Intent intent1= new Intent();
             intent1.setClass(this, introduction.class);
             startActivity(intent1); 
            finish();
         } 
     else if (count!=0) {
    	 Timer timer = new Timer();//timer中有一个线程,这个线程不断执行task
 		
		  //The TimerTask class represents a task to run at a specified time. The task may be run once or repeatedly.
		     TimerTask task = new TimerTask() { //timertask实现runnable接口,TimerTask类就代表一个在指定时间内执行的task
		     @Override
		     public void run() {
		      // System.out.println("3");
		      Intent intent = new Intent();
		      //System.out.println("4");
		      intent.setClass(guide.this, MainActivity.class);
		      //System.out.println("5");
		      startActivity(intent);
		      //System.out.println("6");
		      guide.this.finish();
		      //System.out.println("7");
		     }
		    };
		 
		     timer.schedule(task, 1000 * 5);
	    }
	
    }
		
}
		
