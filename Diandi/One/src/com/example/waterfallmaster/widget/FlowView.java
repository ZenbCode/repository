package com.example.waterfallmaster.widget;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import com.example.one.BitmapTool;
import com.example.one.Constants;
import com.example.one.MainActivity;
import com.example.one.R;
import com.example.one.Show;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.AvoidXfermode.Mode;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.widget.ImageView;
import android.widget.Toast;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;

public class FlowView extends ImageView implements View.OnClickListener,
		View.OnLongClickListener {
	private int ID_number;
	private Context context;
	public Bitmap bitmap;
	public Bitmap bitmap2;
	public Bitmap bitmap3;
	private int columnIndex;// 图片属于第几列
	private int rowIndex;// 图片属于第几行
	private String fileName;
	private int ItemWidth;
	private Handler viewHandler;
	private AssetManager assetManager;

	public FlowView(Context c, AttributeSet attrs, int defStyle) {
		super(c, attrs, defStyle);
		this.context = c;
		Init();
	}

	public FlowView(Context c, AttributeSet attrs) {
		super(c, attrs);
		this.context = c;
		Init();
	}

	public FlowView(Context c) {
		super(c);
		this.context = c;
		Init();
	}

	private void Init() {

		setOnClickListener(this);
		setOnLongClickListener(this);
		setAdjustViewBounds(true);

	}

	@Override
	public boolean onLongClick(View v) {
		Log.d("FlowView", "LongClick");
		Toast.makeText(context, "长按：" + getId(),
				Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public void onClick(View v) {
		Log.d("FlowView", "Click");
		ID_number=getId();
		Intent intent = new Intent();
		intent.setClass(context, Show.class);
		intent.putExtra("ID", ID_number);
	    context.startActivity(intent);
	  }

	/**
	 * 加载图片
	 */
	public void LoadImage() {
			new LoadImageThread().start();
	}

	/**
	 * 重新加载图片
	 */
	public void Reload() {
		if (this.bitmap == null) {
			new ReloadImageThread().start();
		}
	}

	/**
	 * 回收内存
	 */
	public void recycle() {
		setImageBitmap(null);
		if ((this.bitmap == null) || (this.bitmap.isRecycled()))
			return;
		this.bitmap.recycle();
		this.bitmap = null;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public void setColumnIndex(int columnIndex) {
		this.columnIndex = columnIndex;
	}

	public int getRowIndex() {
		return rowIndex;
	}

	public void setRowIndex(int rowIndex) {
		this.rowIndex = rowIndex;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public int getItemWidth() {
		return ItemWidth;
	}

	public void setItemWidth(int itemWidth) {
		ItemWidth = itemWidth;
	}

	public Handler getViewHandler() {
		return viewHandler;
	}

	public FlowView setViewHandler(Handler viewHandler) {
		this.viewHandler = viewHandler;
		return this;
	}
	 private Bitmap setBitmapBorder(Bitmap bitmap){
		    Bitmap output = Bitmap.createBitmap(bitmap);
		    Canvas canvas = new Canvas(bitmap);
	        Rect rect = canvas.getClipBounds();
	        Paint paint = new Paint();
	        //设置边框颜色
	        paint.setColor(Color.WHITE);
	        paint.setStyle(Paint.Style.STROKE);
	        //设置边框宽度
	        paint.setStrokeWidth(20);
            canvas.drawRect(rect, paint);
            return output;
	    }
	public  Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xffffffff;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0,0,0,0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        bitmap.recycle();
        
        return output;
    }
	
	class ReloadImageThread extends Thread {

		@Override
		public void run() {
					 bitmap2=BitmapTool.getBitmap(getFileName(),200,400);
		  bitmap3=Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth()-5, bitmap2.getHeight());
		  bitmap=toRoundCorner(bitmap3, 10);
			 
             
			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					if (bitmap != null) {// 此处在线程过多时可能为null
						setImageBitmap(bitmap);
					}
				}
			});
		}
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
	 
	 
	    
	    
	    
	class LoadImageThread extends Thread {
		LoadImageThread() {
		}

		public void run() {
			
					 bitmap2=BitmapTool.getBitmap(getFileName(),200,400);
			   bitmap3=Bitmap.createBitmap(bitmap2, 0, 0, bitmap2.getWidth()-5, bitmap2.getHeight());
			   bitmap=toRoundCorner(bitmap3, 10);
			 
			

			((Activity) context).runOnUiThread(new Runnable() {
				public void run() {
					if (bitmap != null) {// 此处在线程过多时可能为null
						int width = bitmap.getWidth();// 获取真实宽高
						int height = bitmap.getHeight();
						System.out.println("height"+height);
						System.out.println("height"+width);
						LayoutParams lp = getLayoutParams();

						int layoutHeight;
					
					       layoutHeight = (height * getItemWidth()) /width;// 调整高度
						
						if (lp == null) {
							lp = new LayoutParams(getItemWidth()-8,
									layoutHeight);
						}
						setLayoutParams(lp);

						setImageBitmap(bitmap);
						Handler h = getViewHandler();
						
						Message m = h.obtainMessage(Constants.HANDLER_WHAT, width,
								layoutHeight, FlowView.this);
					
						h.sendMessage(m);
					}
				}
			});
		}
	}//end LoadImageThread
}
