package com.android.gastove.util;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.android.gastove.R;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.support.v4.app.FragmentActivity;
import android.animation.Animator.AnimatorListener;
import android.view.ContextMenu;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.widget.ImageButton;

public abstract class STools extends FragmentActivity {
    // ���İ�ť
    private ImageButton button;

    // �ĸ��Ӱ�ť
    private ImageButton button1;
    private ImageButton button2;    
    private ImageButton button3;
 // �Ӱ�ť�б�
    private List<ImageButton> buttonItems = new ArrayList<ImageButton>(3);
    // ��ʶ��ǰ��ť�������1�����Ѿ�δ������-1�����ѵ���
    private int flag = 1;
    
	public STools() {
		
	}
	public abstract void button1Click();
	public void init(View parent) {
		button = (ImageButton)parent.findViewById(R.id.button);
	
        button1 = (ImageButton)parent.findViewById(R.id.button1);
        button1.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				button1Click();
				menuShow();
			}
		});
        button2 = (ImageButton)parent.findViewById(R.id.button2);
        button3 = (ImageButton)parent.findViewById(R.id.button3);

        // ���Ӱ�ť�Ǽ����б���
        buttonItems.add(button1);
        buttonItems.add(button2);
        buttonItems.add(button3);
    	button.setOnClickListener(new NoDoubleClickListener() {
			@Override
			public void onNoDoubleClick(View v) {
			    	menuShow();
			}
		});
	}

	public void menuShow() {
		buttonAnimation(buttonItems,200,flag);
		flag= flag == 1? -1 : 1;
	}
	 /**
     * ��ť�ƶ�����
     * @params �Ӱ�ť�б�
     * @params ����ʱԲ�ΰ뾶radius
     */
    public void buttonAnimation(final List<ImageButton> buttonList,int radius ,int flag){

        for(int i=0;i<buttonList.size();i++){

            ObjectAnimator objAnimatorX;
            ObjectAnimator objAnimatorY;
            ObjectAnimator objAnimatorRotate;

            // ����ť��Ϊ�ɼ�
            buttonList.get(i).setVisibility(View.VISIBLE);

            // ��ť��X��Y������ƶ�����
            float distanceX = (float) (flag*radius*(Math.cos(Utils.getAngle(buttonList.size(),i))));
            float distanceY = -(float) (flag*radius*(Math.sin(Utils.getAngle(buttonList.size(),i))));

            // X�����ƶ�
            objAnimatorX = ObjectAnimator.ofFloat(buttonList.get(i), "x", buttonList.get(i).getX(),buttonList.get(i).getX()+distanceX);
            objAnimatorX.setDuration(200);
            objAnimatorX.setStartDelay(100);
            objAnimatorX.start();

            // Y�����ƶ�
            objAnimatorY = ObjectAnimator.ofFloat(buttonList.get(i), "y", buttonList.get(i).getY(),buttonList.get(i).getY()+distanceY);
            objAnimatorY.setDuration(200);
            objAnimatorY.setStartDelay(100);
            objAnimatorY.start();

            // ��ť��ת
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
                        // ����ť��Ϊ�ɼ�
                        for (int i = 0; i < buttonList.size(); i++) {
                        	buttonList.get(i).setVisibility(View.INVISIBLE);
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
    
    public abstract class NoDoubleClickListener implements OnClickListener {

        public static final int MIN_CLICK_DELAY_TIME = 500;
        private long lastClickTime = 0;

        @Override
        public void onClick(View v) {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > MIN_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                onNoDoubleClick(v);
            } 
        }

		public abstract void onNoDoubleClick(View v);
    }

}
