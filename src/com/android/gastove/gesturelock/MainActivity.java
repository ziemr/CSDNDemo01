package com.android.gastove.gesturelock;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.gastove.R;


public class MainActivity extends Activity {

	private GestureLockView gv;
	private ImageView imageView1,imageView2;
	private Animation animation;
	private RelativeLayout layout;
	private float x,y,currentX,tempX;
	private FrameLayout.LayoutParams params;
	private FrameLayout.LayoutParams params1,params2;
	private int width,height;
	private FrameLayout root;
	private GestureLockView view;
	private View firstView;
	private static ArrayList<String> list1 = new ArrayList<String>();
	private static ArrayList<String> list2 = new ArrayList<String>();
	private static String string;
	private final String FILENAME = "longzu.txt";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		root = (FrameLayout) findViewById(R.id.root);
		view = new GestureLockView(this);
		
		Display display = getWindowManager().getDefaultDisplay();
		width = display.getWidth();
		height = display.getHeight();
		Log.e("width", width+"");
		Log.e("height", height+"");
		
		params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
		params.gravity = Gravity.CENTER_VERTICAL;
		view.setLayoutParams(params);
		view.setKey("0124678");  //Z
		root.addView(view);
		/*params1 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
		params2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.FILL_PARENT, FrameLayout.LayoutParams.FILL_PARENT);
		imageView1 = new ImageView(this);
		Bitmap bitmap1 = ImageTool.writeDataToImage(list1, width, height, Color.WHITE, Color.BLACK, 25, 10, 20);
		imageView1.setImageBitmap(bitmap1);
		imageView1.setLayoutParams(params1);
		
		imageView2 = new ImageView(this);
		Bitmap bitmap2 = ImageTool.writeDataToImage(list2, width, height, Color.GRAY, Color.BLACK, 25, 10, 20);
		imageView2.setImageBitmap(bitmap2);
		imageView2.setLayoutParams(params2);
		
		TextView textView = new TextView(this);
		textView.setBackgroundColor(Color.GRAY);
		textView.setTextColor(Color.BLACK);
		textView.setText(string);
		textView.setLayoutParams(params2);
		
		//root.addView(imageView2);
		//root.addView(imageView1);
		root.addView(view);
		
		animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.out_left);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				Log.e("aaa", "aaa");
				root.removeView(firstView);
				//root.addView(firstView,0);
				ImageView imageView = new ImageView(MainActivity.this);
				Bitmap bitmap2 = ImageTool.writeDataToImage(list2, width, height, Color.GRAY, Color.BLACK, 25, 10, 20);
				imageView.setImageBitmap(bitmap2);
				imageView.setLayoutParams(params2);
				root.addView(imageView, 0);
				params.leftMargin = 0;
				params.rightMargin = 0;
				root.getChildAt(0).setLayoutParams(params);
				
			}
		});*/
		//new MyThread().start();
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.e("action", "down");
			Log.e("count", root.getChildCount()+"");
			x = event.getX();
			y = event.getY();
			firstView = root.getChildAt(1);
			Log.e("view", firstView.toString());
			Log.e("imageView1", imageView1.toString());
			Log.e("imageView2", imageView2.toString());
			//Log.e("x", x+"");
			//Log.e("y", y+"");
			break;
		case MotionEvent.ACTION_MOVE:
			currentX = event.getX();
			tempX = currentX - x;
			if (tempX < 0) {
				params1.leftMargin = (int) tempX;
			}else {
				params1.rightMargin = (int) -tempX;
			}
			firstView.setLayoutParams(params1);
			break;
		case MotionEvent.ACTION_UP:
			Log.e("action", "up");
			animation.reset();
			firstView.startAnimation(animation);
			break;
		}
		return true;
	}
	
	class MyThread extends Thread{
		@Override
		public void run() {
			// TODO Auto-generated method stub
			super.run();
			try {
				InputStream in = getAssets().open(FILENAME);
				File file = new File(getFilesDir(), FILENAME);
				if (!file.exists()) {
					file.createNewFile();
				
					FileOutputStream out = new FileOutputStream(file);
					int index = -1;
					byte[] temp = new byte[1024];
					while((index=in.read(temp)) != -1){
						out.write(temp, 0, index);
					}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	
	
	
	static{
		for(int i=0;i<10;i++){
			list1.add("11111111111111111111111111111111111111111");
			list2.add("22222222222222222222222222222222222222222");
			string = "22222222222222222222222222222222222222222";
		}
	}
}







































