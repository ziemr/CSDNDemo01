package com.android.gastove.warehouse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnCreateContextMenuListener;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gastove.R;
import com.android.gastove.adapter.PupWidowsAdapter;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.util.BodyLine;
import com.android.gastove.util.Const;
import com.android.gastove.util.GroupingListAdapter;
import com.android.gastove.util.Utils;


public class IndexWHRecordFrgmtActivity extends FragmentActivity implements OnClickListener {
	
	RecentCallsAdapter mAdapter;
    private QueryHandler mQueryHandler;
    private boolean mScrollToTop;
    private static final String TAG = "RecordListActivity";
    private static final int QUERY_TOKEN = 55;
    
    public static final class RecentCallsListItemViews {
        TextView line1View;
        TextView line2View;
        TextView labelView;
        TextView typeView;
        TextView dateView;
        TextView rownumView;
        ImageView ImageView;
//        View callView;
        ImageView groupIndicator;
        TextView groupSize;
    }
    
    static final int ID_COLUMN_INDEX = 0;
    static final int RECORDID_COLUMN_INDEX = 1;
    static final int PHONENUMBER_COLUMN_INDEX = 2;
    static final int NAME_COLUMN_INDEX = 3;
    static final int DATE_COLUMN_INDEX = 4;
    static final int PAY_COLUMN_INDEX = 5;
    
    private Context mContext;
	private DBOperator mDbOperator;
    
	private static int  PageIndex = 0;
	
	private String _TextArray[];
	
	private ImageButton dateStart;
	
    private TextView headTV;
	private Button head_btn;
	public String calc(int position) {
		String[] calcArray = new String[] {
			//   0    1    2    3
				"1", "2", "3", "0",
			//   4    5    6    7
                "4", "5", "6", "C",
            //   8    9    10   11
                "7", "8", "9", "" };
		switch(position) {
		    case 7 :
		    	NumStr = "0";
			    break;
		    case 11 :
			    break;
			default:
				NumStr += calcArray[position];
				break;
		}
		return NumStr;
	}
	
	private String NumStr = "0";
	String[][] val = new String[Const.DB_PUPPART_TYPE][Const.PUPWIN_CONTENT_NUM];

	
    private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
    	
        @Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			startQuery();
		}
    };
    
    private ListView mListView;
    
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
	    protected void onCreate(Bundle state) {
	        super.onCreate(state);

	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.dt_frgmtaty_recordin_layout);
	        mContext = getApplicationContext();
	        // Typing here goes to the dialer
	        setDefaultKeyMode(DEFAULT_KEYS_DIALER);
//	        headTV = (TextView)findViewById(R.id.headTV);
	        inithead();
	        mAdapter = new RecentCallsAdapter();
	        mListView = (ListView) findViewById(R.id.callslist);
	        mListView.setAdapter(mAdapter);
	        this.registerForContextMenu(mListView);
	        mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long id) {
					Cursor cursor = (Cursor) mAdapter.getItem(position);
					String recordid = cursor.getString(1);
					String date = cursor.getString(DATE_COLUMN_INDEX);
//					String number = cursor.getString(2);
//					String name = mDbOperator.getContactName(number);
					
					Intent intent = new Intent();
					intent.setClass(IndexWHRecordFrgmtActivity.this,DtWHRecordInFrgmtActivity.class);
					intent.putExtra(Const.BUNDLE_RECORD_ID, recordid);
					intent.putExtra(Const.BUNDLE_TOADY, date.substring(0, 10));
//					intent.putExtra(Const.BUNDLE_NAME, name);
//					intent.putExtra(Const.BUNDLE_TEL_NUMBER, number);
					//add item use gesture(pupwindow)
					startActivity(intent);
				}
			});

	        mQueryHandler = new QueryHandler(this);
	        mDbOperator = new DBOperator(mContext);
	        
//	        Intent intent = getIntent();
//	        Bundle_telnumber = intent.getStringExtra(Const.BUNDLE_TEL_NUMBER);
	        Bundle_today = Utils.Today();
	        
	        if (!"ALL".equals(Bundle_today)) {
//	        	dateStart.setVisibility(View.INVISIBLE);
	        }
	        //-------------------------------------------------------
//	        String contact = mDbOperator.getContactName(Bundle_telnumber);
	        
	     // 实例化按钮并设立监听
	        button  = (ImageButton)findViewById(R.id.button);
	        button.setOnClickListener(this);
	        button1 = (ImageButton)findViewById(R.id.button1);
	        button1.setOnClickListener(this);
	        button1.setOnCreateContextMenuListener(new OnCreateContextMenuListener() {
				@Override
				public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
					// TODO Auto-generated method stub
					menu.add(0,1,0,"十路桥库");  
					  
					menu.add(0,2,0,"祥和屯库"); 
				}
			});
	        
	        button2 = (ImageButton)findViewById(R.id.button2);
	        button3 = (ImageButton)findViewById(R.id.button3);
	        button3.setOnClickListener(this);
//	        button4 = (ImageButton)findViewById(R.id.button4);

	        // 将子按钮们加入列表中
	        buttonItems.add(button1);
	        buttonItems.add(button2);
	        buttonItems.add(button3);
//	        buttonItems.add(button4);  
	    }

	private void inithead() {
		OnClickListener mSettingsListener = new OnClickListener() {
			Intent intent = new Intent();
			@Override
			public void onClick(View v) {
				intent.setClass(mContext, WarHosDataTree.class);
				startActivity(intent);
			}
		};

		headTV = (TextView) findViewById(R.id.headTV);
		headTV.setText(getString(R.string.head_name_wartree));
		head_btn = (Button) findViewById(R.id.head_btn);
		head_btn.setVisibility(View.VISIBLE);
		head_btn.setOnClickListener(mSettingsListener);
	}
	   private String Bundle_telnumber = null;
	   private String Dialog_dateStart = null;
	   private String Dialog_dateMonth = null;
	   private String Bundle_today = null;
	   ArrayList<BodyLine> mBodyArrays;
	   
	public void DateDialog(final boolean longClick) {
		Calendar calendar = Calendar.getInstance();
		int year = calendar.get(Calendar.YEAR);
		int monthOfYear = calendar.get(Calendar.MONTH);
		
		int dayOfMonth = longClick ? calendar.get(Calendar.DAY_OF_MONTH) : 1;
		DatePickerDialog datePickerDialog = new DatePickerDialog(
				IndexWHRecordFrgmtActivity.this,
				new DatePickerDialog.OnDateSetListener() {
					@Override
					public void onDateSet(DatePicker view, int year,
							int monthOfYear, int dayOfMonth) {
						String month = String.valueOf(monthOfYear+1);
						String day = String.valueOf(dayOfMonth);
						if ( monthOfYear < 10) {
							month ="0"+ month;
						}
						if (dayOfMonth < 10 ) {
							day = "0" + day;
						}
						String date = year + "/" + month + "/"+ day;
						if (longClick) {
							Dialog_dateMonth = date;
							Dialog_dateStart = null;
						}
						else {
							Dialog_dateStart = date;
							Dialog_dateMonth = null;
						}
						Toast.makeText(mContext, date, Toast.LENGTH_SHORT).show();
						startQuery();
					}
				}, year, monthOfYear, dayOfMonth);
		datePickerDialog.setTitle(longClick ? R.string.dialog_date_end
				: R.string.dialog_date_start);
		datePickerDialog.show();
	}
	
	   @Override
	    protected void onStart() {
	        mScrollToTop = true;
	        super.onStart();
	    }

	    @Override
	    protected void onResume() {
	        startQuery();
	        super.onResume();
	        mAdapter.mPreDrawListener = null; // Let it restart the thread after next draw
	        
	        Uri AUTHORITY_URI = Uri.parse("content://" + Const.AUTHORITY +"/" + Const.TABLE_RecordIN);
	        mContext.getContentResolver().registerContentObserver(AUTHORITY_URI, true, mContentObserver);
	    }
	    
	    @Override
	    protected void onPause() {
	        super.onPause();
	        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
	    }

	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        mAdapter.changeCursor(null);
	    }
	    
	    private Uri RecordTodayUri = null;

	private void startQuery() {
		mAdapter.setLoading(true);

		// Cancel any pending queries
		mQueryHandler.cancelOperation(QUERY_TOKEN);

		RecordTodayUri = mDbOperator.getTableUri(Const.TABLE_WHRecordIndex);
		
		String selection = null;
		String[] selectionArgs =null;
        //from ContactsInfo query  
        if ("ALL".equals(Bundle_today)) {
        	selection = "used=? and telphone=?";
    		selectionArgs = new String[] { Const.USED,Bundle_telnumber};
    		if (Dialog_dateStart!=null ) {
    			selection += " and today >= '"+Dialog_dateStart+"'" ;
//    			selectionArgs = new String[] {Const.USED,Bundle_telnumber};
    		}
    		if (Dialog_dateMonth != null) {
    			String monthS = Dialog_dateMonth.substring(0, 8) + "01";
    			String monthE = Dialog_dateMonth.substring(0, 8) + "31";
    			selection += " and today >= '"+monthS+"' and today <= '" + monthE +"'" ;
    		}
        } else {
	       selection = "used=?";
		   selectionArgs = new String[] { Const.USED};
        }
		mQueryHandler.startQuery(QUERY_TOKEN, null, RecordTodayUri,
					RECORD_TODAY_PROJECTION, selection, selectionArgs,
					"date ASC");

	}
	   
	    private static final int MENU_ITEM_INTENT = 1;
	    private static final int MENU_ITEM_DELETE_ALL = 2;
	    
	    private static final int DIALOG_CONFIRM_INTENT = 1;
	    private static final int CONTEXT_MENU_ITEM_DELETE = 1;
	    
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
//	        menu.add(0, MENU_ITEM_INTENT, 0, R.string.recentCalls_deleteAll)
//	                .setIcon(android.R.drawable.ic_menu_close_clear_cancel);
//	        menu.add(0, MENU_ITEM_DELETE_ALL, 0, R.string.recentCalls_deleteAll)
//            .setIcon(android.R.drawable.ic_menu_close_clear_cancel);
	        return true;
	    }
	   
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case MENU_ITEM_INTENT: 
//	            	Intent intent = new Intent(RecordListActivity.this,RecordActivity.class);
//	            	startActivity(intent);
	                return true;
	            case MENU_ITEM_DELETE_ALL: 
	                return true;
	        }
	        return super.onOptionsItemSelected(item);
	    }
	   
	    
	    @Override
	    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
	        AdapterView.AdapterContextMenuInfo menuInfo;
	        try {
	             menuInfo = (AdapterView.AdapterContextMenuInfo) menuInfoIn;
	        } catch (ClassCastException e) {
	            Log.e(TAG, "bad menuInfoIn", e);
	            return;
	        }
	        Cursor cursor = (Cursor) mAdapter.getItem(menuInfo.position);
	        String rownum = cursor.getString(1);
	        
	        //2104/06/17 command
	        menu.setHeaderTitle("单据"+rownum.substring(11));
//            menu.add(0, CONTEXT_MENU_ITEM_DELETE, 0, R.string.contextmenu_recordTodayPay);
	    }
	    
//	    @Override
//	    public boolean onContextItemSelected(MenuItem item) {
//	        switch (item.getItemId()) {
//	            case CONTEXT_MENU_ITEM_DELETE: {
//	                // Convert the menu info to the proper type
//	                AdapterView.AdapterContextMenuInfo menuInfo;
//	                try {
//	                     menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
//	                } catch (ClassCastException e) {
//	                    Log.e(TAG, "bad menuInfoIn", e);
//	                    return false;
//	                }
//
//	                Cursor cursor = (Cursor)mAdapter.getItem(menuInfo.position);
//                    String recordid = cursor.getString(RECORDID_COLUMN_INDEX);
//                    String pay = cursor.getString(PAY_COLUMN_INDEX);
//                    Dialog mDialog = CreateDialog(recordid,pay);
//                    mDialog.show();
//	                return true;
//	            }
//	        }
//			return super.onContextItemSelected(item);
//	    }
		private OnItemClickListener mPieceGridViewListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
//					calc(position);
//					mMenuWindow.setNum(NumStr);

				String[] calcArray = new String[] {
					//      
						"1","2","3",
						"4","5","6",
						"7","8","9",
						".","0","C"
					//   0    1    2    3
//						"1", "2", "3", "0",
					//   4    5    6    7
//		                "4", "5", "6", "C",
		            //   8    9    10   11
//		                "7", "8", "9", "" 
		                };
				switch(position) {
				    case 11 :
				    	mPay = "0";
					    break;
					default:
						mPay += calcArray[position];
						break;
				}
				mPayEdit.setText(mPay.substring(1, mPay.length()));
			}
		};
	    
       private String mPay = null;
		private EditText mPayEdit = null;
		private String mRecordid = null;
	    protected Dialog CreateDialog(String recordid,String pay) {
	    	mRecordid = recordid;
	    	mPay = pay;
	    	View mView = View.inflate(IndexWHRecordFrgmtActivity.this, R.layout.dialog_piece, null);
	    	mPayEdit = (EditText) mView.findViewById(R.id.piece);
	    	mPayEdit.setEnabled(false);
	    	mPayEdit.setText(mPay);
	    	
	    	GridView mCalcGridView = (GridView) mView.findViewById(R.id.gridview_layout_calc);
			mCalcGridView.setAdapter(new PupWidowsAdapter(getApplicationContext(), new String[]{
				"1","2","3",
				"4","5","6",
				"7","8","9",
				".","0","C"
				},true));
	    	
			mCalcGridView.setOnItemClickListener(mPieceGridViewListener);
	    	
	    	
	        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(IndexWHRecordFrgmtActivity.this);
			dialogBuilder.setTitle("付款");
			dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
			dialogBuilder.setView(mView);
//			dialogBuilder.setCancelable(true);
			dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					//insert DB(Recordin)
//					mDbOperator.updateRecordTodayPay(mRecordid,mPay);
					
					mPayEdit = null;
					mPay = null;
					mRecordid = null;
				}
			});
			dialogBuilder.setNegativeButton(android.R.string.cancel, null);
			
			return dialogBuilder.create();
	    }
	   /*
	    * Adapter
	    */
	   private static final class QueryHandler extends AsyncQueryHandler {
	        private final WeakReference<IndexWHRecordFrgmtActivity> mActivity;

	        /**
	         * Simple handler that wraps background calls to catch
	         * {@link SQLiteException}, such as when the disk is full.
	         */
	        protected class CatchingWorkerHandler extends AsyncQueryHandler.WorkerHandler {
	            public CatchingWorkerHandler(Looper looper) {
	                super(looper);
	            }

	            @Override
	            public void handleMessage(Message msg) {
	                try {
	                    // Perform same query while catching any exceptions
	                    super.handleMessage(msg);
	                } catch (SQLiteDiskIOException e) {
	                    Log.w(TAG, "Exception on background worker thread", e);
	                } catch (SQLiteFullException e) {
	                    Log.w(TAG, "Exception on background worker thread", e);
	                } catch (SQLiteDatabaseCorruptException e) {
	                    Log.w(TAG, "Exception on background worker thread", e);
	                }
	            }
	        }

	        @Override
	        protected Handler createHandler(Looper looper) {
	            // Provide our special handler that catches exceptions
	            return new CatchingWorkerHandler(looper);
	        }

	        public QueryHandler(Context context) {
	            super(context.getContentResolver());
	            mActivity = new WeakReference<IndexWHRecordFrgmtActivity>(
	                    (IndexWHRecordFrgmtActivity) context);
	        }

	        @Override
	        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
	            final IndexWHRecordFrgmtActivity activity = mActivity.get();
	            if (activity != null && !activity.isFinishing()) {
	                final IndexWHRecordFrgmtActivity.RecentCallsAdapter callsAdapter = activity.mAdapter;
	                callsAdapter.setLoading(false);
	                callsAdapter.changeCursor(cursor);
	                if (activity.mScrollToTop) {
	                    activity.mScrollToTop = false;
	                }
	            } else {
	                cursor.close();
	            }
	        }
	    }
	   
	   
		   /*
		    * Adapter
		    */
	    final class RecentCallsAdapter extends GroupingListAdapter
        implements ViewTreeObserver.OnPreDrawListener {
	        private boolean mLoading = true;
	        ViewTreeObserver.OnPreDrawListener mPreDrawListener;
	        private static final int REDRAW = 1;
	        private static final int START_THREAD = 2;
	        private boolean mFirst;


	        public boolean onPreDraw() {
	            if (mFirst) {
	                mHandler.sendEmptyMessageDelayed(START_THREAD, 1000);
	                mFirst = false;
	            }
	            return true;
	        }

	        private Handler mHandler = new Handler() {
	            @Override
	            public void handleMessage(Message msg) {
	                switch (msg.what) {
	                    case REDRAW:
	                        notifyDataSetChanged();
	                        break;
	                    case START_THREAD:
//	                        startRequestProcessing();
	                        break;
	                }
	            }
	        };

	        public RecentCallsAdapter() {
	            super(IndexWHRecordFrgmtActivity.this);
	            mPreDrawListener = null;
	        }

	        /**
	         * Requery on background thread when {@link Cursor} changes.
	         */
	        @Override
	        protected void onContentChanged() {
	            // Start async requery
	            startQuery();
	        }

	        void setLoading(boolean loading) {
	            mLoading = loading;
	        }

	        @Override
	        public boolean isEmpty() {
	            if (mLoading) {
	                // We don't want the empty state to show when loading.
	                return false;
	            } else {
	                return super.isEmpty();
	            }
	        }

	        @Override
	        protected View newStandAloneView(Context context, ViewGroup parent) {
	            LayoutInflater inflater =
	                    (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	            View view = inflater.inflate(R.layout.activtiy_index_record_list_item_layout, parent, false);
	            findAndCacheViews(view);
	            return view;
	        }

	        @Override
	        protected void bindStandAloneView(View view, Context context, Cursor cursor) {
	            bindView(context, view, cursor);
	        }

	        private void findAndCacheViews(View view) {

	            // Get the views to bind to
	            RecentCallsListItemViews views = new RecentCallsListItemViews();
	            views.line1View = (TextView) view.findViewById(R.id.line1);
	            views.line2View = (TextView) view.findViewById(R.id.line2);
	            views.labelView = (TextView) view.findViewById(R.id.label);
	            views.typeView = (TextView) view.findViewById(R.id.type);
	            views.dateView = (TextView) view.findViewById(R.id.date);
	            views.rownumView = (TextView) view.findViewById(R.id.rownum);
//	            views.ImageView = (ImageView) view.findViewById(R.id.rowimage);
//	            views.callView = (TextView)view.findViewById(R.id.call_icon);
//	            views.callView = view.findViewById(R.id.call_icon);
//	            views.callView.setOnClickListener(this);
	            view.setTag(views);
	        }

		public void bindView(Context context, View view, Cursor c) {
			final RecentCallsListItemViews views = (RecentCallsListItemViews) view.getTag();

//			views.callView.setVisibility(View.INVISIBLE);
			String recordid = c.getString(RECORDID_COLUMN_INDEX);// 1
			String start = recordid.substring(recordid.length()-12);
			String end = start.substring(8);
			views.labelView.setText(start+"-"+end);
			
			String sum = c.getString(6);
			if (sum != null) {
			    views.line2View.setText("$"+ sum);
			} else {
				views.line2View.setText("$0");
			}
			if (mDbOperator.isWHPieceNoZero(recordid)) 
				views.line2View.setTextColor(Color.RED);
			String pay = c.getString(PAY_COLUMN_INDEX);
//			views.ImageView.setVisibility(View.VISIBLE);
//			if (!"0".equals(pay)) {
//			    views.ImageView.setImageResource(R.drawable.ic_tab_selected_recent_pay);
//			}
			
			String num = c.getString(7);
			if (num != null)
			   views.dateView.setText(num+" 台");

			String date = c.getString(DATE_COLUMN_INDEX); // date 9
			if (date != null)
				views.line1View.setText(date.substring(0, 10)); // yyyy/MM/dd
																// HH:mm:ss

			// Listen for the first draw
			if (mPreDrawListener == null) {
				mFirst = true;
				mPreDrawListener = this;
				view.getViewTreeObserver().addOnPreDrawListener(this);
			}
		}

	    }
	    /** The projection to use when querying the call log table */
	    static final String[] RECORD_TODAY_PROJECTION = new String[] {
	        "_id",   //0
	        "recordid",//1
	        "telphone",//2
	        "name",//3
	        "date",//4
	        "pay",
	        "data4", //sum
	        "data5"  //sum(num)
	    };
	    public void menuClick() {
	    	Utils.buttonAnimation(buttonItems,200,flag);
			flag= flag == 1? -1 : 1;
	    }
		// 选择上下文菜单  
	@Override  
	public boolean onContextItemSelected(MenuItem item) {
		switch (item.getGroupId()) {
		case 0: {
			switch (item.getItemId()) {
			case 1: {
				// 点击选项1
				Intent intent = new Intent();
				intent.setClass(mContext, WarHosDataTree.class);
				intent.putExtra(Const.BUNDLE_STORE,item.getTitle());
				startActivity(intent);
			}
				break;
			case 2: {
				// 点击选项2
			}
				break;

			case 3: {
				
			}
				break;
			} // end get item id
		}
		default:
			break;
		}
		return true;
	}   
	
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			switch (v.getId()) {
			case R.id.button:
				menuClick();
				break;
			case R.id.button1:
				button1.showContextMenu();
				break;
			case R.id.button3:
				Intent intent = new Intent();
				intent.setClass(mContext, WarHosDataTree.class);
				intent.putExtra(Const.BUNDLE_STORE,"Pading");
				startActivity(intent);
				break;
			}
		}
	    
}
