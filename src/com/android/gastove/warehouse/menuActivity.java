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
	    // ���İ�ť
	    private ImageButton button;

	    // �ĸ��Ӱ�ť
	    private ImageButton button1;
	    private ImageButton button2;    
	    private ImageButton button3;    
	    private ImageButton button4;

	    // �Ӱ�ť�б�
	    private List<ImageButton> buttonItems = new ArrayList<ImageButton>(3);

	    // ��ʶ��ǰ��ť�������1�����Ѿ�δ������-1�����ѵ���
	    private int flag = 1;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.menu_main);
	        // ʵ������ť����������
	        button  = (ImageButton)findViewById(R.id.button);
	        button.setOnClickListener(this);
	        button1 = (ImageButton)findViewById(R.id.button1);
	        button2 = (ImageButton)findViewById(R.id.button2);
	        button3 = (ImageButton)findViewById(R.id.button3);
	        button4 = (ImageButton)findViewById(R.id.button4);

	        // ���Ӱ�ť�Ǽ����б���
	        buttonItems.add(button1);
	        buttonItems.add(button2);
	        buttonItems.add(button3);
	        buttonItems.add(button4);   
	    }

	    /**
	     * ��ť�ƶ�����
	     * @params �Ӱ�ť�б�
	     * @params ����ʱԲ�ΰ뾶radius
	     */
	    public void buttonAnimation(List<ImageButton> buttonList,int radius){

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
