package com.android.gastove.warehouse;
import java.util.ArrayList;
import java.util.List;

import com.android.gastove.R;
import com.android.gastove.util.Utils;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
public class menuActivity extends Activity implements OnClickListener{
	    // 中心按钮
	    private ImageButton button;

	    // 四个子按钮
	    private ImageButton button1;
	    private ImageButton button2;    
	    private ImageButton button3;    
	    private ImageButton button4;

	    // 子按钮列表
	    private List<ImageButton> buttonItems = new ArrayList<ImageButton>(3);

	    // 标识当前按钮弹出与否，1代表已经未弹出，-1代表已弹出
	    private int flag = 1;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.menu_main);
	        // 实例化按钮并设立监听
	        button  = (ImageButton)findViewById(R.id.button);
	        button.setOnClickListener(this);
	        button1 = (ImageButton)findViewById(R.id.button1);
	        button2 = (ImageButton)findViewById(R.id.button2);
	        button3 = (ImageButton)findViewById(R.id.button3);
	        button4 = (ImageButton)findViewById(R.id.button4);

	        // 将子按钮们加入列表中
	        buttonItems.add(button1);
	        buttonItems.add(button2);
	        buttonItems.add(button3);
	        buttonItems.add(button4);   
	    }

	    /**
	     * 按钮移动动画
	     * @params 子按钮列表
	     * @params 弹出时圆形半径radius
	     */
	    public void buttonAnimation(List<ImageButton> buttonList,int radius){

	        for(int i=0;i<buttonList.size();i++){

	            ObjectAnimator objAnimatorX;
	            ObjectAnimator objAnimatorY;
	            ObjectAnimator objAnimatorRotate;

	            // 将按钮设为可见
	            buttonList.get(i).setVisibility(View.VISIBLE);

	            // 按钮在X、Y方向的移动距离
	            float distanceX = (float) (flag*radius*(Math.cos(Utils.getAngle(buttonList.size(),i))));
	            float distanceY = -(float) (flag*radius*(Math.sin(Utils.getAngle(buttonList.size(),i))));

	            // X方向移动
	            objAnimatorX = ObjectAnimator.ofFloat(buttonList.get(i), "x", buttonList.get(i).getX(),buttonList.get(i).getX()+distanceX);
	            objAnimatorX.setDuration(200);
	            objAnimatorX.setStartDelay(100);
	            objAnimatorX.start();

	            // Y方向移动
	            objAnimatorY = ObjectAnimator.ofFloat(buttonList.get(i), "y", buttonList.get(i).getY(),buttonList.get(i).getY()+distanceY);
	            objAnimatorY.setDuration(200);
	            objAnimatorY.setStartDelay(100);
	            objAnimatorY.start();

	            // 按钮旋转
	            objAnimatorRotate = ObjectAnimator.ofFloat(buttonList.get(i), "rotation", 0, 360);
	            objAnimatorRotate.setDuration(200);
	            objAnimatorY.setStartDelay(100);
	            objAnimatorRotate.start();

	            if(flag==-1){
	                objAnimatorX.addListener(new AnimatorListener() {

	                    @Override
	                    public void onAnimationStart(Animator animation) {
	                        // TODO Auto-generated method stub
	                    }

	                    @Override
	                    public void onAnimationRepeat(Animator animation) {
	                        // TODO Auto-generated method stub
	                    }

	                    @Override
	                    public void onAnimationEnd(Animator animation) {
	                        // TODO Auto-generated method stub
	                        // 将按钮设为可见
	                        for (int i = 0; i < buttonItems.size(); i++) {
	                            buttonItems.get(i).setVisibility(View.INVISIBLE);
	                        }
	                    }

	                    @Override
	                    public void onAnimationCancel(Animator animation) {
	                        // TODO Auto-generated method stub
	                    }
	                });
	            }

	        }
	    }

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}

	}
