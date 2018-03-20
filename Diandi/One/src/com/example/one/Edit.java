package com.example.one;


import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


public class Edit extends Activity {
	
	
	String mess="确定放弃编辑吗？";
	String sure ="确定";
	String cancle ="取消";
    ImageView imageView=null;
    Bitmap image=null;
    EditText  editText=null;
    Bitmap Bm =null;
    int i=0;
    Button Btn_abandon=null;
    TextView textView_diandi2=null;
    Button btn_save =null;  
    private SQLiteDatabase sqlitedb;
    private MySQLiteOpenHelper myOpenHelper;
    private File path = new File("/sdcard/dbfile"); //数据库文件目录   
	private File f = new File("/sdcard/dbfile/DIANDI.db");
	private File imagePath = new File("/sdcard/myImage");
	  
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit);
		  textView_diandi2 = (TextView)findViewById(R.id.textView_diandi2);
	      imageView=(ImageView)findViewById(R.id.imageView);
	      btn_save=(Button)findViewById(R.id.Btn_save);
	      Btn_abandon=(Button)findViewById(R.id.Btn_abandon);
		  editText = (EditText)findViewById(R.id.editText);
	      create_file();
	      creatDb();
		  setTypeface();
		  showIntentImage();
		  imageView.setOnClickListener(new imageListener());
		  
		  
	     
		 btn_save.setOnClickListener(new Btn_save_ClickListener());
		 
	     Btn_abandon.setOnClickListener(new Btn_abandon_listener());

	     
	}//end OnCreat()
	
	public void showIntentImage(){
           String imagePath = getImagePath();
           BitmapFactory.Options BfOptions= new BitmapFactory.Options();
           BfOptions.inJustDecodeBounds = true;
           Bm = BitmapFactory.decodeFile(imagePath, BfOptions);
           BfOptions.inJustDecodeBounds = false;
           int be = (int) (BfOptions.outHeight / (float) 200);
           if (be <=0){
        	   be=1;
           }
           BfOptions.inSampleSize=be;
           Bm=BitmapFactory.decodeFile(imagePath, BfOptions);
		   Handler h=new Handler();
		   h.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			imageView.setImageBitmap(Bm);
			}
		});
	}
     
     private String getImagePath(){
    	 File file=new File("/sdcard/myImage"); 
         File[] f_CurrentImage = file.listFiles();
 	         String Path[] = new String[f_CurrentImage.length];
 	         int i=0;
 	         for ( i = 0; i < f_CurrentImage.length; i++){

 	                 Path[i] = f_CurrentImage[i].getPath();
 	                 
 	         }
 	         return Path[i-1];
     }
     public void setTypeface(){
    	 //设置字体
		 Typeface tf=Typeface.createFromAsset(getAssets(), "carton.ttf");
	     textView_diandi2.setTypeface(tf);
     }
   
     public void creatDb(){
 		sqlitedb = SQLiteDatabase.openOrCreateDatabase(f, null);   
 		String TABLE_NAME = "diandi_one";  
         String ID = "id";  
         String CONTENT = "content";
         String PATH = "path";   
         String str_sql2 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ID    
             + " INTEGER PRIMARY KEY AUTOINCREMENT," + CONTENT + " text ,"+ PATH +"String);";  
         sqlitedb.execSQL(str_sql2);   
 	 }
     
     public void create_file(){
         myOpenHelper = new MySQLiteOpenHelper(this);  
   		 
   		 if(!path.exists()){   //判断目录是否存在   
   		         path.mkdirs();    //创建目录   
   		     }   
   		if(!f.exists()){      //判断文件是否存在   
   		  try{   
   		    f.createNewFile();  //创建文件   
   		    }catch(IOException e){   
   		     e.printStackTrace();   
   		   }   
   		} 
       }
     
  
     
     class SureButtonlistener implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.cancel();
	        Intent intent2 = new Intent(Edit.this, MainActivity.class);
	        startActivity(intent2);
			finish();
		}

     }
     class Empty_NoButtonListener implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			saveInfo();
		}
    	 
     }
     class  Empty_YesButtonlistener implements DialogInterface.OnClickListener{

		@Override
		public void onClick(DialogInterface dialog, int which) {
			// TODO Auto-generated method stub
			dialog.cancel();
		}
    	 
     }
     public void  isEmpty(){
    	 String user_content = editText.getText().toString();
    	 if(user_content.equals("")){
    		 AlertDialog alertDialogg = new AlertDialog.Builder(Edit.this).setMessage( "输入为空，不想说点什么吗？").setNegativeButton("算了", new Empty_NoButtonListener()).setPositiveButton("重写", new Empty_YesButtonlistener()).create();  
			  Window window =alertDialogg.getWindow(); 
	          WindowManager.LayoutParams lp = window.getAttributes();  
		      lp.alpha = 0.8f;  
		      window.setAttributes(lp); 
		      alertDialogg.show();
    	 }
    	 
     }
     public void saveInfo(){
    	 String imagePath =getImagePath();
    	 System.out.println("sndeujbhjhbdhbdj::!!::"+imagePath);
         ContentValues newValues=new ContentValues();
         newValues.put(myOpenHelper.CONTENT, editText.getText().toString());
         newValues.put(myOpenHelper.PATH,imagePath);
         sqlitedb.insert(myOpenHelper.TABLE_NAME,null,newValues);
    	 imageView.setOnClickListener(new imageListener());
         
        
         AlertDialog alertDialog = new AlertDialog.Builder(Edit.this).setMessage( "保存成功").setPositiveButton("确定", new SureButtonlistener()).create();  


         Window window =alertDialog.getWindow(); 
         WindowManager.LayoutParams lp = window.getAttributes();  
		 lp.alpha = 0.8f;  
         window.setAttributes(lp); 
         alertDialog.show();
     }
     class Btn_save_ClickListener implements OnClickListener{

 		
 		public void onClick(View v) {
 			
 			String user_content = editText.getText().toString();
             
             if(user_content.equals("")){
            	 isEmpty();
             }
             else{	 
            	 saveInfo();
           }
 		}
 	}
	 
     class anandon_YesButtonlistener implements DialogInterface.OnClickListener{
	     public void onClick(DialogInterface dialog, int which) {
	    	 
	    	String Delete_path =getImagePath();
	    	File file = new File(Delete_path);
	    	file.delete();	
	    	Intent intent =new Intent(Edit.this, MainActivity.class);
	    	startActivity(intent);
	    	finish();
	       
	       }
	}

	class anandon_NoButtonlistener implements DialogInterface.OnClickListener{
	     public void onClick(DialogInterface dialog, int which) {
	            dialog.cancel();
	            System.out.println("check2")  ;
	       }
	}

	class Btn_abandon_listener implements OnClickListener{

		@Override
		public void onClick(View v) {

			  AlertDialog anandon_alertDialog = new AlertDialog.Builder(Edit.this).setMessage( "确定不保存了吗？").setNegativeButton("确定", new anandon_YesButtonlistener()).setPositiveButton("取消", new anandon_NoButtonlistener()).create();  
			  Window window = anandon_alertDialog.getWindow(); 
	          WindowManager.LayoutParams lp = window.getAttributes();  
		      lp.alpha = 0.8f;  
		      window.setAttributes(lp); 
		      anandon_alertDialog.show();
				

		}	
	}

	

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		super.onBackPressed();
		String Delete_path =getImagePath();
    	File file = new File(Delete_path);
    	file.delete();	
    	Intent intent = new Intent(Edit.this, MainActivity.class);
    	startActivity(intent);
		finish();
		
	}

	 class imageListener implements OnClickListener{

			@Override
			public void onClick(View v) {
				
				Intent intent = new Intent(Edit.this, showimage.class);
				String image_path =getImagePath();
				intent.putExtra("imagePath", image_path);
				startActivity(intent);
			}
			  
		  }
 

}
		