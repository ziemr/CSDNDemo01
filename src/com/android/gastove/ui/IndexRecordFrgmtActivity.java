package com.android.gastove.ui;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.AdapterView;
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


public class IndexRecordFrgmtActivity extends FragmentActivity {
	
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
    static final int INDEXSTATUS_COLUMN_INDEX = 5;
    static final int TODAY_COLUMN_INDEX = 8;
    
    private Context mContext;
	private DBOperator mDbOperator;
    
	private static int  PageIndex = 0;
	
	private String _TextArray[];
	
	private ImageButton dateStart;
	
	 private TextView headTV;
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
	   @Override
	    protected void onCreate(Bundle state) {
	        super.onCreate(state);

	        requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.dt_frgmtaty_recordin_layout);
	        mContext = getApplicationContext();
	        // Typing here goes to the dialer
	        setDefaultKeyMode(DEFAULT_KEYS_DIALER);
	        headTV = (TextView)findViewById(R.id.headTV);
	        
//	        dateStart = (ImageButton) findViewById(R.id.ibt_date_start);
//	        dateStart.setOnClickListener(new OnClickListener() {
//				@Override
//				public void onClick(View v) {
//					DateDialog(false);
//				}
//			});
//	        dateStart.setOnLongClickListener(new OnLongClickListener() {
//				
//				@Override
//				public boolean onLongClick(View v) {
//					DateDialog(true);
//					return true;
//				}
//			});
            
	        mAdapter = new RecentCallsAdapter();
	        mListView = (ListView) findViewById(R.id.callslist);
	        mListView.setAdapter(mAdapter);
	        this.registerForContextMenu(mListView);
	        mListView.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> arg0, View view,
						int position, long id) {
					// TODO 帺摦惗惉偝傟偨儊僜僢僪丒僗僞僽
					Cursor cursor = (Cursor) mAdapter.getItem(position);
					String recordid = cursor.getString(1);
					String number = cursor.getString(2);
					String name = mDbOperator.getContactName(number);
					
					Intent intent = new Intent();
					intent.setClass(IndexRecordFrgmtActivity.this,DtRecordInFrgmtActivity.class);
					intent.putExtra(Const.BUNDLE_RECORD_ID, recordid);
					intent.putExtra(Const.BUNDLE_NAME, name);
					intent.putExtra(Const.BUNDLE_TEL_NUMBER, number);
					//add item use gesture(pupwindow)
					startActivity(intent);
				}
			});

	        mQueryHandler = new QueryHandler(this);
	        mDbOperator = new DBOperator(mContext);
	        
	        Intent intent = getIntent();
	        Bundle_telnumber = intent.getStringExtra(Const.BUNDLE_TEL_NUMBER);
	        Bundle_today = intent.getStringExtra(Const.BUNDLE_TOADY);
	        
	        if (!"ALL".equals(Bundle_today)) {
//	        	dateStart.setVisibility(View.INVISIBLE);
	        }
	        //-------------------------------------------------------
	        String contact = mDbOperator.getContactName(Bundle_telnumber);
	        headTV.setText(contact+Const.KEY_DELIMITER_ +Bundle_today);
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
				IndexRecordFrgmtActivity.this,
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

		RecordTodayUri = mDbOperator.getTableUri(Const.TABLE_RecordToday);
		
		String selection = null;
		String[] selectionArgs =null;
        //from ContactsInfo query  
		if ("ALL".equals(Bundle_today)) {
			// for contactgroup
			String groupID = mDbOperator.getContactGROUPID(Bundle_telnumber);
			if (Const.NO_DATA.equals(groupID)) {
				selection = "used=? and telphone=?";
				selectionArgs = new String[] { Const.USED, Bundle_telnumber };
			} else {
				
				selection = "used=? and telphone in"+mDbOperator.getGruopMemberString(groupID);
				selectionArgs = new String[] { Const.USED };
			}

			if (Dialog_dateStart != null) {
				selection += " and today >= '" + Dialog_dateStart + "'";
				// selectionArgs = new String[] {Const.USED,Bundle_telnumber};
			}
			if (Dialog_dateMonth != null) {
				String monthS = Dialog_dateMonth.substring(0, 8) + "01";
				String monthE = Dialog_dateMonth.substring(0, 8) + "31";
				selection += " and today >= '" + monthS + "' and today <= '" + monthE + "'";
			}
		} else {
	       selection = "used=? and today=? and telphone=?";
		   selectionArgs = new String[] { Const.USED,Bundle_today,Bundle_telnumber};
        }
		mQueryHandler.startQuery(QUERY_TOKEN, null, RecordTodayUri,
					RECORD_TODAY_PROJECTION, selection, selectionArgs,
					"date ASC");

	}
	   
	    private static final int MENU_ITEM_INTENT = 1;
	    private static final int MENU_ITEM_DELETE_ALL = 2;
	    
	    public static final int STATUS_Record= 0; 
	    public static final int STATUS_NOTE = 1; 
	    public static final int STATUS_DONING_MAKE = 2;
	    public static final int STATUS_DOING_FIRE =3;
	    public static final int STATUS_DOING_DELIVERY = 4;
	    public static final int STATUS_PAYING = 5;
	    public static final int STATUS_END = 6;
	    public static final int STATUS_scrap = -1;
	    public static final int STATUS_REMARK = 7;
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
            menu.add(0, STATUS_NOTE, 0, R.string.contextmenu_recordTodayNOTE);
            menu.add(0, STATUS_DONING_MAKE, 0, R.string.contextmenu_recordTodayDONING_MAKE);
            menu.add(0, STATUS_DOING_FIRE, 0, R.string.contextmenu_recordTodayDOING_FIRE);
            menu.add(0, STATUS_DOING_DELIVERY, 0, R.string.contextmenu_recordTodayDOING_DELIVERY);
            menu.add(0, STATUS_PAYING, 0, R.string.contextmenu_recordTodayPAYING);
            menu.add(0, STATUS_END, 0, R.string.contextmenu_recordTodayEND);
            menu.add(0, STATUS_scrap, 0, R.string.contextmenu_recordTodayscrap);
            menu.add(0, STATUS_REMARK, 0, R.string.contextmenu_recordTodayREMARK);
            
	    }
	    
	    @Override
	public boolean onContextItemSelected(MenuItem item) {
		// Convert the menu info to the proper type
		AdapterView.AdapterContextMenuInfo menuInfo;
		try {
			menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
		} catch (ClassCastException e) {
			Log.e(TAG, "bad menuInfoIn", e);
			return false;
		}

		Cursor cursor = (Cursor) mAdapter.getItem(menuInfo.position);
		String recordid = cursor.getString(RECORDID_COLUMN_INDEX);
        String strstatus = Const.STATUS_Record;
//		Dialog mDialog = CreateDialog(recordid, pay);
//		mDialog.show();
		switch (item.getItemId()) {

		case STATUS_NOTE: {
			strstatus = Const.STATUS_NOTE;
			break;
		}
		case STATUS_DONING_MAKE: {
			strstatus = Const.STATUS_DONING_MAKE;
			break;
		}
		case STATUS_DOING_FIRE: {
			strstatus = Const.STATUS_DOING_FIRE;
			break;
		}

		case STATUS_DOING_DELIVERY: {
			strstatus = Const.STATUS_DOING_DELIVERY;
			break;
		}
		case STATUS_PAYING: {
			strstatus = Const.STATUS_PAYING;
			break;
		}
		case STATUS_END: {
			strstatus = Const.STATUS_END;
			break;
		}
		case STATUS_scrap: {
			strstatus = Const.STATUS_scrap;
			break;
		}
		case STATUS_REMARK: {
//			strstatus = Const.STATUS_REMARK;
			CreateReMarkDialog(recordid);
			return super.onContextItemSelected(item);
		}
		}
//		if(!strstatus.endsWith(Const.STATUS_REMARK))
	    mDbOperator.updateRecordTodayStatus(recordid,strstatus);
//	    if(strstatus.equals(Const.STATUS_REMARK))
//			CreateReMarkDialog(recordid);
		return super.onContextItemSelected(item);
	}
	    
		private void CreateReMarkDialog(final String recordid) {
			final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(IndexRecordFrgmtActivity.this);	// 系统默认Dialog没有输入框
			View alertDialogView = View.inflate(IndexRecordFrgmtActivity.this, R.layout.dialog_rename, null);
			final EditText et_dialog_confirmphoneguardpswd = (EditText) alertDialogView.findViewById(R.id.edit_rename);
//			if (!(oldname == null || oldname.length() <= 0)) {
//				et_dialog_confirmphoneguardpswd.setText(oldname);
//			}
			TextWatcher txtwatcher = new TextWatcher() {
				public void onTextChanged(CharSequence s, int start, int before,
						int count) {
				}

				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}

				public void afterTextChanged(Editable s) {
					if (s.toString().length() > 6) {
						Utils.showToastLong(mContext, mContext.getString(R.string.contextmenu_recordTodayREMARKLeng));
					} 
				}
			};
			
			et_dialog_confirmphoneguardpswd.addTextChangedListener(txtwatcher);
			alertDialog.setTitle(getResources().getString(R.string.contextmenu_recordTodayREMARK));
			alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
			alertDialog.setPositiveButton(android.R.string.ok,
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
//							String str = et_dialog_confirmphoneguardpswd.getText().toString().trim();
							Log.i(TAG, "getReNameDialogPositiveButton start");
							mDbOperator.updateRecordTodayStatus_Remark(recordid,et_dialog_confirmphoneguardpswd.getText().toString().trim());
					    	Log.i(TAG, "getReNameDialogPositiveButton end");
						
						}
					});
			alertDialog.setNegativeButton(android.R.string.cancel, null);
			AlertDialog tempDialog = alertDialog.create();
			tempDialog.setView(alertDialogView, 0, 0, 0, 0);
			
			tempDialog.show();
		}
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
	    	View mView = View.inflate(IndexRecordFrgmtActivity.this, R.layout.dialog_piece, null);
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
	    	
	    	
	        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(IndexRecordFrgmtActivity.this);
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
	        private final WeakReference<IndexRecordFrgmtActivity> mActivity;

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
	            mActivity = new WeakReference<IndexRecordFrgmtActivity>(
	                    (IndexRecordFrgmtActivity) context);
	        }

	        @Override
	        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
	            final IndexRecordFrgmtActivity activity = mActivity.get();
	            if (activity != null && !activity.isFinishing()) {
	                final IndexRecordFrgmtActivity.RecentCallsAdapter callsAdapter = activity.mAdapter;
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
	            super(IndexRecordFrgmtActivity.this);
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
			if (sum != null && mDbOperator.isPieceNoZero(recordid)) {
			    views.line2View.setText("$"+ sum);
			} else {
				views.line2View.setText("$0");
			}
			
			int status = Integer.parseInt(c.getString(INDEXSTATUS_COLUMN_INDEX));
			String str_status = null;
			String readd = null;
			switch (status) {
			case STATUS_Record:
				str_status = "";
				break;
			case STATUS_NOTE:
				str_status = context.getString(R.string.contextmenu_recordTodayNOTE);
				break;
			case STATUS_DONING_MAKE:
				str_status = context.getString(R.string.contextmenu_recordTodayDONING_MAKE);
				break;
			case STATUS_DOING_FIRE:
				str_status = context.getString(R.string.contextmenu_recordTodayDOING_FIRE);
				break;
			case STATUS_DOING_DELIVERY:
				str_status = context.getString(R.string.contextmenu_recordTodayDOING_DELIVERY);
				break;
			case STATUS_PAYING:
				str_status = context.getString(R.string.contextmenu_recordTodayPAYING);
				break;
			case STATUS_END:
				str_status = context.getString(R.string.contextmenu_recordTodayEND);
				break;
			case STATUS_scrap:
				str_status = context.getString(R.string.contextmenu_recordTodayscrap);
				break;
			case STATUS_REMARK:
//				str_status = c.getString(NAME_COLUMN_INDEX);
				break;
			}
//			views.ImageView.setVisibility(View.VISIBLE);
//			if (!"0".equals(pay)) {
//			    views.ImageView.setImageResource(R.drawable.ic_tab_selected_recent_pay);
//			}
			
			String num = c.getString(7);
			if (num != null)
			   views.dateView.setText(num+" 台");
			readd = c.getString(NAME_COLUMN_INDEX);
			String today = c.getString(TODAY_COLUMN_INDEX); // date 9
            if (readd != null && readd.length() > 0)
            	if (str_status != null )
            	    today = today  +" "+str_status+" ("+readd+")";
            	else
            		today = today + " ("+readd+")";
            else
            	if(str_status!= null)
            	    today = today + "    "+str_status;
			if (today != null)
				views.line1View.setText(today); // yyyy/MM/dd
																// HH:mm:ss
			
			if(today.length()>20)
				views.line1View.setTextSize(TypedValue.COMPLEX_UNIT_PX,50);
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
	        "data5",  //sum(num)
	        "today"
	    };
	    
}
