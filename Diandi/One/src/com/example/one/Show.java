package com.example.one;

import java.io.File;
import java.io.IOException;

import com.example.one.R.id;

import android.R.integer;
import android.R.layout;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class Show extends Activity {

	int ID_num ;
	Boolean num=false;
	private Bitmap bm=null;
	private File f_db = null;
	private File path = null; // 数据库文件目录
	private String sdPath = null;
	private File imagePath = null;
	private SQLiteDatabase sqlitedb;
	private MySQLiteOpenHelper myOpenHelper;
	private String content=null;
	private String image_path =null;
	private Button textButton=null;
	private ImageView imageView = null;
	private TextView textView_diandi2=null;
	private TextView text_day=null;
	private TextView text_month=null;
	private TextView text_year=null;
	private TextView text_hour=null;
	private TextView text_minute=null;
	private Button Btn_back=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show);
		
		textButton = (Button)findViewById(R.id.textButton);
		imageView = (ImageView)findViewById(R.id.imageView);
		textView_diandi2 = (TextView)findViewById(R.id.textView_diandi2);
		text_day = (TextView)findViewById(R.id.text_day);
		text_month=(TextView)findViewById(R.id.text_month);
		text_year=(TextView)findViewById(R.id.text_year);
		text_hour=(TextView)findViewById(R.id.text_hour);
		text_minute=(TextView)findViewById(R.id.text_minute);
		Btn_back=(Button)findViewById(R.id.Btn_back);
		
		
		Btn_back.setOnClickListener(new Btn_back_listener());
		imageView.setOnClickListener(new image_listener());
		sdPath = getSDPath();
		path = new File(sdPath + "/dbfile"); // 数据库文件目录
		f_db = new File(sdPath + "/dbfile/DIANDI.db");
		imagePath = new File(sdPath + "/myImage");
        setTypeface();   
        receivedId();
        create_file();
		creatDb();
        checkDb();
        showInfo();
        showDate();
       
	}
   
	class Btn_back_listener implements OnClickListener{

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			finish();
		}
		
	}
	public void create_file() {
		    myOpenHelper = new MySQLiteOpenHelper(this);

		if (!path.exists()) { // 判断目录是否存在
			path.mkdirs(); // 创建目录
		}
		if (!f_db.exists()) { // 判断文件是否存在
			try {
				f_db.createNewFile(); // 创建文件
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!imagePath.exists()) {
			imagePath.mkdir();
		}
	}
	public void creatDb() {
		sqlitedb = SQLiteDatabase.openOrCreateDatabase(f_db, null);
		String TABLE_NAME = "diandi_one";
		String ID = "id";
		String CONTENT = "content";
		String PATH = "path";
		String str_sql2 = "CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "(" + ID
				+ " INTEGER PRIMARY KEY AUTOINCREMENT," + CONTENT + " text ,"
				+ PATH + "String);";
		sqlitedb.execSQL(str_sql2);
	}
	public void  checkDb() {
		
		String ID_change =Integer.toString(ID_num);
		Cursor cursor =sqlitedb.query(MySQLiteOpenHelper.TABLE_NAME, new String[]{MySQLiteOpenHelper.ID,MySQLiteOpenHelper.CONTENT,MySQLiteOpenHelper.PATH}, "id=?",new String[]{ ID_change} , null, null, null);
		System.out.println("show_ID_change11111:"+ID_change);
		 while(cursor.moveToNext()){  
             content = cursor.getString(cursor.getColumnIndex("content"));  
             image_path = cursor.getString(cursor.getColumnIndex("pathString"));  
              
         }  

		}

	
	public void receivedId(){
		Intent data = getIntent();
		ID_num = data.getExtras().getInt("ID");
		System.out.println("show_ID:"+ID_num);
		
	}
     public void setTypeface(){
    	 //设置字体
		Typeface tf=Typeface.createFromAsset(getAssets(), "carton.ttf");
		textView_diandi2.setTypeface(tf);

     }
  // SD卡路径
 	private String getSDPath() {
 		File sdDir = null;
 		boolean sdCardExist = Environment.getExternalStorageState().equals(
 				android.os.Environment.MEDIA_MOUNTED); // 判断sd卡是否存在
 		if (sdCardExist) {
 			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
 			return sdDir.toString();
 		} else {
 			return null;
 		}

 	}
    public void showInfo(){
    	if(content.equals("")){
    		textButton.setText("没有心情，亦是一种心情\n         - By Designer");
    	}
    	else{
 		textButton.setText(content);
    	}
 		BitmapFactory.Options BfOptions= new BitmapFactory.Options();
        BfOptions.inJustDecodeBounds = true;
        bm = BitmapFactory.decodeFile(image_path, BfOptions);
        BfOptions.inJustDecodeBounds = false;
        int be = (int) (BfOptions.outHeight / (float) 200);
        if (be <=0){
     	   be=1;
        }
        BfOptions.inSampleSize=be;
        bm=BitmapFactory.decodeFile(image_path, BfOptions);
		   Handler h=new Handler();
		   h.post(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
			imageView.setImageBitmap(bm);
			}
		});
 	}
    
    public String fileName() {
		int i = 0;
		String test[];
		test = imagePath.list();
		for (i = 0; i < test.length; i++) {
			System.out.println("文件名fileName:" + test[i]);
		}
		
		return test[ID_num-1];
		
	}
    public void showDate(){
    	String imageName=fileName();
    	String Cut_date_year;
    	String Cut_date_month;
    	String Cut_date_day;
    	String Cut_date_hour;
    	String Cut_date_minute;
    	Cut_date_year=imageName.substring(7, 11);
    	Cut_date_month=imageName.substring(11, 13);
    	Cut_date_day=imageName.substring(13, 15);
    	Cut_date_hour=imageName.substring(16, 18);
    	Cut_date_minute=imageName.substring(18, 20);
    	text_day.setText(Cut_date_day);
    	text_month.setText(Cut_date_month);
    	text_year.setText(Cut_date_year);
    	text_hour.setText(Cut_date_hour);
    	text_minute.setText(Cut_date_minute);
    }
    class	image_listener implements OnClickListener{

    	@Override
    	public void onClick(View v) {
    		Intent intent = new Intent(Show.this, showimage.class);
    		intent.putExtra("imagePath", image_path);
    		startActivity(intent);
    		
    	}
    	
      }
}
