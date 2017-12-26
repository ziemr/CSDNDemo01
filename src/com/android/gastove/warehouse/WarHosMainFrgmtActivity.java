package com.android.gastove.warehouse;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.gastove.MainActivity;
import com.android.gastove.R;
import com.android.gastove.adapter.PupWidowsAdapter;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.ui.LoginFrgmtActivity;
import com.android.gastove.util.Const;
import com.android.gastove.util.Utils;
import com.android.gastove.warehouse.DtWHRecordInFrgmtActivity.RecentCallsListItemViews;

public class WarHosMainFrgmtActivity extends FragmentActivity implements 
		OnClickListener,OnTouchListener {
	private EditText edt_num,edt_spec,edt_whpiece;
	private Button btn_whok,btn_whin,bt_spec_clear,btn_store;
	private GridView mCalcGridView;
	private Context mContext;
	private DBOperator mDbOperator;
	private TextView txt_view,txt_viewSum;
	private RelativeLayout layout_view;
	private String STORE = null;
	
	private OnItemClickListener poPupGridViewListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,long arg3) {
			calc(position);
		}
	};
	private String Bundle_recordid = null;
	private ImageView headicon;
	@Override
	public void onCreate(Bundle state) {
		super.onCreate(state);
		setContentView(R.layout.activity_warhos);
		init();
		
		
		Intent intent = getIntent();
        Bundle_recordid = intent.getStringExtra(Const.BUNDLE_RECORD_ID);
//        Bundle_telnumber = intent.getStringExtra(Const.BUNDLE_TEL_NUMBER);
        Bundle_ID = intent.getStringExtra(Const.BUNDLE_ID);
        dataLeafID = mDbOperator.getDataLeafID(Const.TABLE_warhosleaf, Bundle_ID);
        STORE = intent.getStringExtra(Const.BUNDLE_STORE);
//        txt_whdata.setText(Bundle_ID);
        findViewById();
        inithead();
	}
	 private void init() {
			mContext= getApplicationContext();
	        mDbOperator = new DBOperator(mContext);
	        
	 }
	private Button bt_username_clear;
	private TextView headTV;
	private Button head_btn;
	private void inithead() {
		 OnClickListener mSettingsListener = new OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent();
					intent.setClass(mContext, DtWHRecordInFrgmtActivity.class);
					intent.putExtra(Const.BUNDLE_RECORD_ID, Bundle_recordid);
					intent.putExtra(Const.BUNDLE_Gesture,true);
					startActivity(intent);
				}
			};
		
		headTV = (TextView)findViewById(R.id.headTV);
        headTV.setText(Bundle_ID);
        head_btn = (Button) findViewById(R.id.head_btn);
	    head_btn.setVisibility(View.VISIBLE);
	    head_btn.setOnClickListener(mSettingsListener);
	    
	    
	}
	protected void findViewById() {
		txt_view = (TextView) this.findViewById(R.id.txt_view);
		txt_viewSum= (TextView) this.findViewById(R.id.txt_viewSum);
		edt_num=(EditText)this.findViewById(R.id.edt_num);
		edt_spec=(EditText)this.findViewById(R.id.edt_spec);
		edt_whpiece=(EditText)this.findViewById(R.id.edt_whpiece);
		bt_spec_clear = (Button) this.findViewById(R.id.bt_spec_clear);
		layout_view = (RelativeLayout) this.findViewById(R.id.layout_view);
		layout_view.setOnClickListener(this);
		headicon = (ImageView) findViewById(R.id.headIcon);
		headicon.setOnClickListener(this);
//		initWatcher();
//		edt_num.addTextChangedListener(num_watcher);
		
		btn_whok=(Button)this.findViewById(R.id.btn_whok);
		btn_whok.setOnClickListener(this);
		btn_whin=(Button)this.findViewById(R.id.btn_whin);
		btn_whin.setOnClickListener(this);
		btn_store=(Button)this.findViewById(R.id.btn_store);
		btn_store.setText(STORE);
		btn_store.setOnClickListener(this);
		btn_store.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
			@Override
			public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
				// TODO Auto-generated method stub
				menu.add(0,1,0,"十路桥库");  
				  
				menu.add(0,2,0,"祥和屯库"); 
			}
		});
		edt_num.setInputType(InputType.TYPE_NULL);
		edt_spec.setInputType(InputType.TYPE_NULL);
		edt_whpiece.setInputType(InputType.TYPE_NULL);
		
		edt_num.setOnTouchListener(this);
		edt_spec.setOnTouchListener(this);
		edt_whpiece.setOnTouchListener(this);
		bt_spec_clear.setOnClickListener(this);
		
		mCalcGridView = (GridView) this.findViewById(R.id.gridview_layout_calc);
		mCalcGridView.setAdapter(new PupWidowsAdapter(mContext, new String[]{"1","2","3",
				"4","5","6",
				"7","8","9",
				"C","0","."},true));
		mCalcGridView.setOnItemClickListener(poPupGridViewListener);
		
		
		 views = new RecentCallsListItemViews();
         views.line1View = (TextView) findViewById(R.id.line1);
         views.remark = (TextView) findViewById(R.id.remark);
         views.line2View = (TextView) findViewById(R.id.line2);
         views.labelView = (TextView) findViewById(R.id.label);
         views.typeView = (TextView) findViewById(R.id.type);
         views.dateView = (TextView) findViewById(R.id.date);
         views.rownumView = (TextView) findViewById(R.id.rownum);
         views.callView = (TextView)findViewById(R.id.call_icon);
		
	}
	RecentCallsListItemViews views =null;
	private void setItemViews(int num,String spec,String piece,String pro) {
		//piece
       views.callView.setText(piece);
       views.labelView.setText(spec+"  "+getString(R.string.str_spec));
       views.remark.setText("");
        
        String[] treeid = dataLeafID.split(Const.KEY_DELIMITER_AND);
        String treename = mDbOperator.getWarhosTreeName(treeid[0]);
        if (!TextUtils.isEmpty(Bundle_ID)) {
            views.line1View.setText(Bundle_ID +"  -"+ treename);
            
            views.line2View.setText(String.valueOf(num));
            // "type" and "label" are currently unused for SIP addresses.
            CharSequence numberLabel = null;
            views.dateView.setText("$ "+pro);
            
            if (!TextUtils.isEmpty(numberLabel)) {
                views.labelView.setText(numberLabel);
                views.labelView.setVisibility(View.VISIBLE);

                // Zero out the numberView's left margin (see below)
                ViewGroup.MarginLayoutParams numberLP =
                        (ViewGroup.MarginLayoutParams) views.typeView.getLayoutParams();
                numberLP.leftMargin = 0;
                views.typeView.setLayoutParams(numberLP);
            } else {

                ViewGroup.MarginLayoutParams labelLP =
                        (ViewGroup.MarginLayoutParams) views.labelView.getLayoutParams();
                ViewGroup.MarginLayoutParams numberLP =
                        (ViewGroup.MarginLayoutParams) views.typeView.getLayoutParams();
                // Equivalent to setting android:layout_marginLeft in XML
                numberLP.leftMargin = -labelLP.rightMargin;
                views.typeView.setLayoutParams(numberLP);
            }
        } else {
            views.line1View.setText("any");
        }
	}
	
	String dataLeafID = null;
	@Override
	protected void onResume() {
		super.onResume();
		edt_num.setFocusable(true);
		tempEditText = edt_num;
		try {
			
		
		String[] hint = mDbOperator.queryWarhoshint(dataLeafID).split(Const.KEY_DELIMITER_);
		if (hint.length < 2) return;
		if (checkspell(hint[0]) && checkspell(hint[1])) {
			edt_spec.setText(hint[0]);
			edt_whpiece.setText(hint[1]);
		}
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}
	
	boolean checkspell(String str) {
		if (null == str) return false;
		if (str.length()<1)
			return false;
		else
			return true;
		
	}
	private String Bundle_ID ,Num = "0",Piece= "0",Spec= "0",Pro= "0";
	private TextWatcher num_watcher;
	private void initWatcher() {
		num_watcher = new TextWatcher() {
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			public void afterTextChanged(Editable s) {
//				et_pass.setText("");
				if (s.toString().length() > 0) {
					btn_whok.setClickable(true);
				} else {
					btn_whok.setClickable(false);
				}
			}
		};

	}
	private String txtView = "";
	private int txtViewSum = 0;
	@Override
	public void onClick(View arg0) {
//        String ostr = edt_num.getText().toString();
		switch (arg0.getId()) {
		case R.id.btn_whok:
			String tempNum = edt_num.getText().toString();
			String tempSpec = edt_spec.getText().toString();
			String tempPiece = edt_whpiece.getText().toString();
//			Num = edt_num.getText().toString();
//			Spec = edt_spec.getText().toString();
//			Piece = edt_whpiece.getText().toString();
			if (checkspell(tempNum)&& checkspell(tempSpec)) {
				if(!checkspell(tempPiece))  tempPiece = "0";
				
//				txtViewSum = Integer.parseInt(tempNum);
//				txtView += tempNum + " + ";
//				mDbOperator.insertWHRecordin(Bundle_recordid,dataLeafID,Num, Piece,Spec);
//				mDbOperator.updateWHRecordIndexUsed(Bundle_recordid);
				
				Num = tempNum;
				Spec = tempSpec;
				Piece = tempPiece;
				int Sum = Integer.parseInt(tempNum) * Integer.parseInt(Spec);
				
				txtViewSum += Sum;
				txtView += Sum + " + ";
				Pro = String.valueOf(Float.parseFloat(Piece) * txtViewSum);
				
				NumStr = "";
				tempEditText = edt_num;
				tempEditText.setText(NumStr);
				btn_whok.setEnabled(false);
				btn_whin.setEnabled(false);
				txt_viewSum.setText(String.valueOf(txtViewSum)+" = ");
				txt_view.setText(txtView);
				setItemViews(txtViewSum,Spec,Piece,Pro);
				} else {
					
				}
			break;
		case R.id.btn_whin:
			Num = String.valueOf(txtViewSum);
			mDbOperator.insertWHRecordin(Bundle_recordid,dataLeafID,Num, Piece,Spec,Pro);
			mDbOperator.updateWHRecordIndexUsed(Bundle_recordid);
			Num = "0";
			Spec = "0";
			Piece = "0";
			Pro = "0";
			txtViewSum = 0;
			txtView ="";
			btn_whin.setEnabled(false);
			btn_whok.setEnabled(false);
			txt_viewSum.setText("");
			txt_view.setText("");
//			setItemViews(txtViewSum, Spec, Piece, Pro);
			
			Intent intent = new Intent();
			intent.setClass(mContext, DtWHRecordInFrgmtActivity.class);
			intent.putExtra(Const.BUNDLE_RECORD_ID, Bundle_recordid);
			intent.putExtra(Const.BUNDLE_Gesture,true);
			startActivity(intent);
			
			break;
		case R.id.btn_store:
			btn_store.showContextMenu();  
			break;
		case R.id.layout_view:
			if(!"0".equals(Num))
			   btn_whin.setEnabled(true);
			break;
		case R.id.bt_spec_clear:
			edt_spec.setText("1");
			break;
		case R.id.headIcon:
				new Handler().postDelayed(new Runnable() {
					public void run() {
						Intent intent = new Intent();
						intent = new Intent(WarHosMainFrgmtActivity.this, MainActivity.class);
						WarHosMainFrgmtActivity.this.startActivity(intent);
						// LoginFrgmtActivity.this.finish();// 结束本Activity
					}
				}, 500);
			break;
		default:
			break;
		}
	}
	  
 
		// 选择上下文菜单  
		@Override  
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getGroupId()) {
		case 0: {
			switch (item.getItemId()) {
			case 1: {
				btn_store.setText("test1");
			}
				break;
			case 2: {
				btn_store.setText("test2");
			}
				break;

			} // end get item id
		}
		default:
			break;
		}
		return true;
	}  
	
	private EditText tempEditText = null;
	private boolean flag_ten = true;
	
	
	
	@Override
	public boolean onTouch(View view, MotionEvent event) {
		if (view.equals(edt_whpiece)) {
			flag_ten = true;
		} else {
			flag_ten = false;
		}
		NumStr = "";
		tempEditText = (EditText) view;
		return false;
	}
	private String NumStr = "";
	public String calc(int position) {
		String[] calcArray = new String[] {
				//   0    1    2 
					"1", "2", "3",
					
				//   3    4    5 
	                "4", "5", "6", 
	            //   6    7    8 
	                "7", "8", "9",
	            //   9    10   11
	                "C", "0", "."};
		switch(position) {
		    case 9 :
		    	NumStr = "";
		    	tempEditText.setText(NumStr);
		    	if (tempEditText.equals(edt_num))
					btn_whok.setEnabled(false);
			    break;
		    case 11 :
		    	if (flag_ten)//  .  is enable
		    	    NumStr += calcArray[position];
		    	break;
			default:
				    NumStr += calcArray[position];
				    if (tempEditText.equals(edt_num))
						btn_whok.setEnabled(true);
				break;
		}
		tempEditText.setText(NumStr.substring(0,NumStr.length()));
		if (NumStr.length() > 6)
			Toast.makeText(mContext, getString(R.string.num_limit), Toast.LENGTH_SHORT).show();
		return NumStr;
	}
}
