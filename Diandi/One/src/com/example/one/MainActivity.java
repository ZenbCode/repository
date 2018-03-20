package com.example.one;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.example.one.LazyScrollView.OnScrollListener;
import com.example.one.Constants;
import com.example.one.LazyScrollView;
import com.example.waterfallmaster.widget.FlowView;
import android.app.Activity;
import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import android.net.Uri;
import android.provider.MediaStore;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("SimpleDateFormat")
public class MainActivity extends Activity {

	int count=0;	
	SQLiteDatabase db = null;
	Cursor cursor = null;
	String content = null;
	ImageView btn_carema = null;
	private File path = null; // 数据库文件目录
	private File f_db = null;
	private MySQLiteOpenHelper myOpenHelper;
	private SQLiteDatabase sqlitedb;
	private File imagePath = null;
	private String imageName = "";
	final int GET_IMAGE_VIA_CAMERA = 0;
	private static boolean isExit = false;
	private static boolean hasTask = false;
	Timer tExit = new Timer();

	private LazyScrollView waterfall_scroll;
	private LinearLayout waterfall_container;
	private ArrayList<LinearLayout> waterfall_items;
	private Display display;
	private AssetManager asset_manager =null;
	private List<String> image_filenames;

	private String image_path = "images";
	private Handler handler;
	private int item_width;

	private int column_count = Constants.COLUMN_COUNT;// 显示列数
	private int page_count = Constants.PICTURE_COUNT_PER_LOAD;// 每次加载30张图片

	private int current_page = 0;// 当前页数
	private int[] topIndex;
	private int[] bottomIndex;
	private int[] lineIndex;
	private int[] column_height;// 每列的高度

	private HashMap<Integer, String> pins;

	private int loaded_count = 0;// 已加载数量

	private HashMap<Integer, Integer>[] pin_mark = null;

	private Context context;

	private HashMap<Integer, FlowView> iviews;
	int scroll_height;

	private String sdPath = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		display = this.getWindowManager().getDefaultDisplay();
		item_width = display.getWidth() / column_count;// 根据屏幕大小计算每列大小
		asset_manager = getAssets();
		column_height = new int[column_count];
		context = this;
		iviews = new HashMap<Integer, FlowView>();
		pins = new HashMap<Integer, String>();
		pin_mark = new HashMap[column_count];
		this.lineIndex = new int[column_count];
		this.bottomIndex = new int[column_count];
		this.topIndex = new int[column_count];

		for (int i = 0; i < column_count; i++) {
			lineIndex[i] = -1;
			bottomIndex[i] = -1;
			pin_mark[i] = new HashMap();
		}

		sdPath = getSDPath();
		path = new File(sdPath + "/dbfile"); // 数据库文件目录
		f_db = new File(sdPath + "/dbfile/DIANDI.db");
		imagePath = new File(sdPath + "/myImage");

		initializeViews();
		create_file();
		creatDb();
		fileName();
		
		System.out.println(image_path);
		btn_carema.setOnClickListener(new btn_listener());
		InitLayout();

	}// end onCreate();

	private void InitLayout() {
		waterfall_scroll = (LazyScrollView) findViewById(R.id.waterfall_scroll);
		waterfall_scroll.getView();
		waterfall_scroll.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onBottom() {
				// 滚动到最低端
				AddItemToContainer(++current_page, page_count);
			}

			@Override
			public void onTop() {
				// 滚动到最顶端
				Log.d("LazyScroll", "Scroll to top");
			}

			@Override
			public void onScroll() {
				// TODO Auto-generated method stub

			}

			@Override
			public void onAutoScroll(int l, int t, int oldl, int oldt) {
				scroll_height = waterfall_scroll.getMeasuredHeight();
				Log.d("MainActivity", "scroll_height:" + scroll_height);
				if (t > oldt) {// 向下滚动
					if (t > 2 * scroll_height) {// 超过两屏幕后

						for (int k = 0; k < column_count; k++) {

							LinearLayout localLinearLayout = waterfall_items
									.get(k);

							if (pin_mark[k].get(Math.min(bottomIndex[k] + 1,
									lineIndex[k])) <= t + 3 * scroll_height) {// 最底部的图片位置小于当前t+3*屏幕高度

								((FlowView) waterfall_items.get(k).getChildAt(
										Math.min(1 + bottomIndex[k],
												lineIndex[k]))).Reload();

								bottomIndex[k] = Math.min(1 + bottomIndex[k],
										lineIndex[k]);

							}
							Log.d("MainActivity",
									"headIndex:" + topIndex[k] + "  footIndex:"
											+ bottomIndex[k] + "  headHeight:"
											+ pin_mark[k].get(topIndex[k]));
							if (pin_mark[k].get(topIndex[k]) < t - 2
									* scroll_height) {// 未回收图片的最高位置<t-两倍屏幕高度

								int i1 = topIndex[k];
								topIndex[k]++;
								((FlowView) localLinearLayout.getChildAt(i1))
										.recycle();
								Log.d("MainActivity", "recycle,k:" + k
										+ " headindex:" + topIndex[k]);

							}
						}

					}
				} else {// 向上滚动
					if (t > 2 * scroll_height) {// 超过两屏幕后
						for (int k = 0; k < column_count; k++) {
							LinearLayout localLinearLayout = waterfall_items
									.get(k);
							if (pin_mark[k].get(bottomIndex[k]) > t + 3
									* scroll_height) {
								((FlowView) localLinearLayout
										.getChildAt(bottomIndex[k])).recycle();

								bottomIndex[k]--;
							}

							if (pin_mark[k].get(Math.max(topIndex[k] - 1, 0)) >= t
									- 2 * scroll_height) {
								((FlowView) localLinearLayout.getChildAt(Math
										.max(-1 + topIndex[k], 0))).Reload();
								topIndex[k] = Math.max(topIndex[k] - 1, 0);
							}
						}
					}

				}

			}
		});
		waterfall_container = (LinearLayout) findViewById(R.id.waterfall_container);
		handler = new Handler() {

			@Override
			public void dispatchMessage(Message msg) {
				// TODO Auto-generated method stub
				super.dispatchMessage(msg);
			}

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				
				switch (msg.what) {
				case Constants.HANDLER_WHAT:

					FlowView v = (FlowView) msg.obj;
					int w = msg.arg1;
					int h = msg.arg2;

					String f = v.getFileName();

					// 此处计算列值
					int columnIndex = GetMinValue(column_height);

					v.setColumnIndex(columnIndex);

					column_height[columnIndex] += h;

					pins.put(v.getId(), f);
					iviews.put(v.getId(), v);
					waterfall_items.get(columnIndex).addView(v);

					lineIndex[columnIndex]++;

					pin_mark[columnIndex].put(lineIndex[columnIndex],
							column_height[columnIndex]);
					bottomIndex[columnIndex] = lineIndex[columnIndex];
					break;
				}
			}

			@Override
			public boolean sendMessageAtTime(Message msg, long uptimeMillis) {
				// TODO Auto-generated method stub
				return super.sendMessageAtTime(msg, uptimeMillis);
			}
		};
		waterfall_items = new ArrayList<LinearLayout>();
		for (int i = 0; i < column_count; i++) {
			LinearLayout itemLayout = new LinearLayout(this);
			LinearLayout.LayoutParams itemParam = new LinearLayout.LayoutParams(
					item_width, LayoutParams.WRAP_CONTENT);

			
			itemLayout.setPadding(4, 2, 2,2);
			itemLayout.setOrientation(LinearLayout.VERTICAL);
			itemLayout.setLayoutParams(itemParam);
			waterfall_items.add(itemLayout);
			waterfall_container.addView(itemLayout);
		}
		// 加载所有图片路径
		File file = new File(sdPath+"/myImage");
		int i = 0;
		String test[];
		test = file.list();
		for (i = 0; i < test.length; i++) {
			System.out.println("文件名fileName:" + test[i]);

		}
		List<String> ll = array2List(test);

		image_filenames = ll;
		System.out.println("image_filenames:" + image_filenames);
		// 第一次加载
		AddItemToContainer(current_page, page_count);

	}

	public static List<String> array2List(String[] strs) {
		List<String> list = new ArrayList<String>();
		for (String str : strs) {
			list.add(str);
		}
		return list;
	}



	private void AddItemToContainer(int pageindex, int pagecount) {

		int currentIndex = pageindex * pagecount;
		int imagecount = Constants.PICTURE_TOTAL_COUNT;// image_filenames.size();

		int j=fileName().length;
		if(j==0){
			return;
		}	
		for (int i = currentIndex; i < pagecount * (pageindex + 1)&& i < imagecount; i++) {
			loaded_count++;
		
			if(count == j){
			  break;
			}
			AddImage(fileName()[count],(int) Math.ceil(loaded_count / (double) column_count),loaded_count);
			count++;
		}

	}
	
  
   
   
	private void AddImage(String filename, int rowIndex, int id) {
		
		FlowView item = new FlowView(context);

		item.setRowIndex(rowIndex);
		item.setId(id);
		item.setViewHandler(handler);
		item.setFileName(sdPath + "/myImage/" + filename);
		item.setItemWidth(item_width);
		item.LoadImage();

	}

	private int GetMinValue(int[] array) {
		int m = 0;
		int length = array.length;
		for (int i = 0; i < length; ++i) {

			if (array[i] < array[m]) {
				m = i;
			}
		}
		return m;
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

	public void initializeViews() {

		TextView textView_diandi = (TextView) findViewById(R.id.textView_diandi);
		btn_carema = (ImageView) findViewById(R.id.btn_carema);
		// 设置字体
		Typeface tf = Typeface.createFromAsset(getAssets(), "carton.ttf");
		textView_diandi.setTypeface(tf);
		
	}

	public String[] fileName() {
		int i = 0;
		String test[];
		test = imagePath.list();
		for (i = 0; i < test.length; i++) {
		}
		return test;
	}

	public String checkDb() {
		Cursor cur = sqlitedb.rawQuery("SELECT * FROM "
				+ MySQLiteOpenHelper.TABLE_NAME, null);
		String temp = "";
		String path = "";
		if (cur != null) {
			int i = 0;
			while (cur.moveToNext()) {
				temp += cur.getString(1); // 0代表数据列的第一列,即id
				path += cur.getString(2); // 1代表数据列的第二列,即text
				i++;
				// 定义显示数据的格式，一行一个数据
			}

		}

		return path;
	}

	class btn_listener implements OnClickListener {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if (!imagePath.exists()) {
				imagePath.mkdirs();
			}

			Intent intent = new Intent(
					android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
			imageName = getImageName();
			System.out.println("之前imageName" + imageName);
			imageName = imageName.replace("-", "");
			imageName = imageName.replace(":", "");
			System.out.println("之后imageName" + imageName);
			File f = new File(imagePath, imageName);
			System.out.println("f:" + f);
			Uri u = Uri.fromFile(f);
			System.out.println("U:" + u);
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 1);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, u);
			startActivityForResult(intent, GET_IMAGE_VIA_CAMERA);

		}

	}

	private String getImageName() {
		Date date = new Date(System.currentTimeMillis());
		
		SimpleDateFormat dateFormat = new SimpleDateFormat(
				"yyyy-MM-dd_HH:mm:ss");
		return "Diandi_" + dateFormat.format(date) + ".jpg";

	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == RESULT_OK) {
			switch (requestCode) {

			case GET_IMAGE_VIA_CAMERA: {

				File file = new File("/sdcard/myImage");
				File[] f_CurrentImage = file.listFiles();
				String Path[] = new String[f_CurrentImage.length];
				int i = 0;
				for (i = 0; i < f_CurrentImage.length; i++) {

					Path[i] = f_CurrentImage[i].getPath();

				}

				
				String imagePath = Path[i - 1];
				Intent intentChange = new Intent(MainActivity.this, Edit.class);
				intentChange.putExtra("imagePath", imagePath);
				startActivity(intentChange);
				finish();
				break;
			}
			}
		}

	}// end onActivityResult

	TimerTask task = new TimerTask() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			isExit = false;
			hasTask = true;
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (isExit == false) {
				isExit = true;
				Toast.makeText(this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
				if (!hasTask) {
					tExit.schedule(task, 2000);
				}
			} else {
				finish();
				System.exit(0);
			}
		}
		return false;
	}// end onKeyDown

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
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
	public ImageView getImageview() {
		// 创建显示图片的对象
		ImageView imageView = new ImageView(this);
		LayoutParams layoutParams = new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT);
		imageView.setLayoutParams(layoutParams);
		imageView.setPadding(2, 0, 2, 2);// 设置间距
		return imageView;
	}


}// end activity
