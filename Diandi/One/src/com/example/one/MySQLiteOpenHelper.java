package com.example.one;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteOpenHelper extends SQLiteOpenHelper {
	public static final String DATABASE_NAME = "DIANDI.db"; //数据库名   
    public static final int VERSION = 1;   //版本号   
    public static final String TABLE_NAME = "diandi_one";  //表名   
    public static final String ID = "id";  
    public static final String CONTENT = "content";
    public static final String PATH = "pathString"; 
    
    
    public MySQLiteOpenHelper(Context context) {  
        super(context, DATABASE_NAME, null, VERSION);  
    }  

    public void onCreate(SQLiteDatabase db) {  
        //创建数据表的操作   
        String strSQL = "CREATE TABLE " + TABLE_NAME + "(" + ID    
        + " INTEGER PRIMARY KEY AUTOINCREMENT," + CONTENT + " text ,"+ PATH +"String);";    
          
        db.execSQL(strSQL);  
    }  


	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		
		 Log.e("TEST", "onUpgrade");    
	}
	
	

}
