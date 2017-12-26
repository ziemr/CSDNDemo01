package com.android.gastove.warehouse;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
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
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gastove.R;
import com.android.gastove.adapter.PupWidowsAdapter;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.service.BluetoothMsg;
import com.android.gastove.settings.PoPupWidowsDataLeaf;
import com.android.gastove.settings.PoPupWidowsDataTree;
import com.android.gastove.util.BodyLine;
import com.android.gastove.util.Const;
import com.android.gastove.util.GroupingListAdapter;
import com.android.gastove.util.PopupWidows;
import com.android.gastove.util.Utils;

public class DtWHRecordInFrgmtActivity extends FragmentActivity{
    private static final String TAG = "WHRecordListActivity";
	RecentCallsAdapter mAdapter;
    private QueryHandler mQueryHandler;
    private DBOperator mDbOperator;
    private Context mContext;
    
    private String Bundle_recordid = null;
	private String Bundle_telnumber = null;
	private String Bundle_ID ;
	
    private boolean mScrollToTop;
    private static final int QUERY_TOKEN = 55;
    
    private ListView mListView;
    private static int  PageIndex = 0;
    
    private String _TextArray[];
    private PupWidowsAdapter mPupAdapter;
    //gesture and pupwindow
    //for list
	private GestureDetector mDetector;
    //flag for add/view to limit gesture
    private Boolean Bundle_Gesture = false;
    private Boolean editEnable = false;
    private TextView SUM_HEAD = null;
    private TextView NUM_HEAD = null;
	private Button headNewRecord;
	private LinearLayout mLayoutHide;
	
    private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
    	
        @Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
//			Toast.makeText(mContext, "observer", Toast.LENGTH_SHORT).show();
			startQuery();
		}
    };
	private Handler popupHandler = new Handler(){  
	    @Override  
	    public void handleMessage(Message msg) {
	        switch (msg.what) {  
	        case 0:  
	        	PopupWindow();
	            break;  
	        }  
	    }  
	      
	};
    
	//for list
	OnTouchListener mListTouchListener = new OnTouchListener() {
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if (mDetector != null && Bundle_Gesture || editEnable)
			   return mDetector.onTouchEvent(event);
			return false;
		}
	};
	
	//for no list
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (mDetector != null && Bundle_Gesture) {
			return mDetector.onTouchEvent(event);
		}
		return super.onTouchEvent(event);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dt_frgmtaty_recordin_layout);
		init();

		mLayoutHide = (LinearLayout)findViewById(R.id.head_hide);
		headNewRecord = (Button)findViewById(R.id.head_btn);

		mListView = (ListView) findViewById(R.id.callslist);
		mListView.setOnCreateContextMenuListener(this);
		mListView.setAdapter(mAdapter);
        mListView.setOnTouchListener(mListTouchListener);

		
    	Intent intent = getIntent();
        Bundle_recordid = intent.getStringExtra(Const.BUNDLE_RECORD_ID);
//        Bundle_telnumber = intent.getStringExtra(Const.BUNDLE_TEL_NUMBER);
//        Bundle_Date = intent.getStringExtra(Const.BUNDLE_TOADY);
        
//        String name = mDbOperator.getContactName(Bundle_telnumber);
        setActivityTitle(intent.getStringExtra(Const.BUNDLE_TOADY));
        
        Bundle_Gesture = intent.getBooleanExtra(Const.BUNDLE_Gesture, false);
        mLayoutHide.setVisibility(View.VISIBLE);
//        if (Bundle_ID != null) {
//    		headNewRecord.setVisibility(View.VISIBLE);
    		headNewRecord.setOnClickListener(mNewRecordListener);
//        }
        
	}
	
	 private void init() {
			mContext= getApplicationContext();
			mAdapter = new RecentCallsAdapter();
			
	        mQueryHandler = new QueryHandler(this);
	        mDbOperator = new DBOperator(mContext);
	        
	    	//--reCall
//	    	mDetector = new GestureDetector(this,new GestureListener());
	 }
	
	   private void setActivityTitle(String name) {
		   TextView txtHead = (TextView) findViewById(R.id.headTV);
		   SUM_HEAD = (TextView) findViewById(R.id.txt_sum);
		   NUM_HEAD = (TextView) findViewById(R.id.txt_num);
		   NUM_HEAD.setVisibility(View.GONE);
		   txtHead.setText(name);
//		   telView.setText(telnumber);
	   }
	   
		@Override
		protected void onResume() {
	        startQuery();
	        super.onResume();
	        mAdapter.mPreDrawListener = null; // Let it restart the thread after next draw
	        
	        Uri AUTHORITY_URI = Uri.parse("content://" + Const.AUTHORITY +"/" + Const.TABLE_WHRecordIN);
	        mContext.getContentResolver().registerContentObserver(AUTHORITY_URI, true, mContentObserver);
	        TypeCount = mDbOperator.gettableCount(Const.TABLE_PupWinMage);
//	        if (Bundle_Gesture)
//	            popupHandler.sendEmptyMessageDelayed(0, 100);
	        SUM_HEAD.setText("$"+ Float.toString(mDbOperator.queryWHRecordINSum(Bundle_recordid)));
		}
		
	@Override
	protected void onPause() {
		super.onPause();
//		clearPopWindow();
//        mDbOperator.updateRecordTodaySum(Bundle_recordid);
		mDbOperator.updateWHRecordTodaySum(Bundle_recordid);
	}
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.changeCursor(null);
    }
    
	private Uri WHRecordinUri = null;
    /** The projection to use when querying the record in table */
    static final String[] RECORD_IN_PROJECTION = new String[] {
    	"_id",      //0
    	"recordid", //1
        "phone",    //2
        "num",      //3
        "data1",    //4
        "data2" ,   //5
        "data3" ,   //6
        "data4" ,   //7
        "data5",    //8
        "date",      //9
        "data6"
    };
    
    private void startQuery() {
        mAdapter.setLoading(true);

        // Cancel any pending queries
        mQueryHandler.cancelOperation(QUERY_TOKEN);
        WHRecordinUri = mDbOperator.getTableUri(Const.TABLE_WHRecordIN);
        
        String order = "date ASC";
        if (Bundle_Gesture) order = "date DESC";
        mQueryHandler.startQuery(QUERY_TOKEN, null, WHRecordinUri,
        		RECORD_IN_PROJECTION, Const.RECORDIN_COLUMN_RECORDID+"=? and data7 is not -1", new String[]{Bundle_recordid}, order);
    }
    
    private static final int MENU_ITEM_EDIT = 1;
    private static final int CONTEXT_MENU_ITEM_DELETE = 3;
    private static final int CONTEXT_MENU_ITEM_REMARK = 4;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (!Bundle_Gesture) {
        menu.add(0, MENU_ITEM_EDIT, 0, R.string.open)
        .setIcon(R.drawable.ic_menu_optionmenu_money);
    	}
        return true;
    }
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_EDIT:
            	editEnable = true;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {

        menu.setHeaderTitle(getString(R.string.contextmenu_title));
        menu.setHeaderIcon(R.drawable.ic_dialog_icon);
        
        menu.add(0, CONTEXT_MENU_ITEM_DELETE, 0, R.string.recentCalls_removeFromRecentList);
//        menu.add(0, CONTEXT_MENU_ITEM_REMARK, 0, R.string.recentCalls_remarkFromRecentList);
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case CONTEXT_MENU_ITEM_DELETE: {
                // Convert the menu info to the proper type
                AdapterView.AdapterContextMenuInfo menuInfo;
                try {
                     menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                } catch (ClassCastException e) {
                    Log.e(TAG, "bad menuInfoIn", e);
                    return false;
                }

                Cursor cursor = (Cursor)mAdapter.getItem(menuInfo.position);
                StringBuilder sb = new StringBuilder();
                    long id = cursor.getLong(COLUMN_INDEX_ID);
                    sb.append(id);

                boolean flag = mDbOperator.deleteWHRecordLOC(sb.toString());
                if(flag) {
                	Toast.makeText(mContext, getString(R.string.recordin_delete_ok), Toast.LENGTH_SHORT).show();
                }
                
            }
            break;
            case CONTEXT_MENU_ITEM_REMARK: {
            	AdapterView.AdapterContextMenuInfo menuInfo;
                try {
                     menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
                } catch (ClassCastException e) {
                    Log.e(TAG, "bad menuInfoIn", e);
                    return false;
                }

                Cursor cursor = (Cursor)mAdapter.getItem(menuInfo.position);
                final int rownumber = cursor.getInt(COLUMN_INDEX_ID);
                map.clear();
            	new AlertDialog.Builder(this)
	             .setTitle(R.string.dialog_title_remark)
	             .setIcon(R.drawable.ic_dialog_icon)
	             .setMultiChoiceItems(langs, null,
	                     new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,int which, boolean isChecked) {
									map.put(which, isChecked);
								}
	                     })
	             .setPositiveButton("OK",
	                     new DialogInterface.OnClickListener() {
	                         public void onClick(DialogInterface dialog,int which) {
	                        	 remarkStr="";
	                        	 for(int i = 0 ;i < langs.length;i++) {
	                        		 if(map.containsKey(i)) {
	                        			 if((Boolean) map.get(i)) remarkStr += langs[i];
	                        		 }
	                        	 }
	                        	 mDbOperator.updateRecordRemark(rownumber,remarkStr);
	                        	 Toast.makeText(mContext, remarkStr,Toast.LENGTH_LONG).show();
	                         }
	                     }).show();
            }
            
            return true;
        }
		return super.onContextItemSelected(item);
    }
	private static final class QueryHandler extends AsyncQueryHandler {
	        private final WeakReference<DtWHRecordInFrgmtActivity> mActivity;

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
	            mActivity = new WeakReference<DtWHRecordInFrgmtActivity>(
	                    (DtWHRecordInFrgmtActivity) context);
	        }

	        @Override
	        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
	            final DtWHRecordInFrgmtActivity activity = mActivity.get();
	            if (activity != null && !activity.isFinishing()) {
	                final DtWHRecordInFrgmtActivity.RecentCallsAdapter callsAdapter = activity.mAdapter;
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
     *  pup dialog for piece
     */  
    private String mPiece = "0";
	private EditText mPieceEdit = null;
//	private String mPieceID = null;
    protected Dialog CreateDialog(final String _id,final String hintpiece) {
//    	 mPieceID = _id;
//    	mPiece = piece;
    	final String hintPiece = hintpiece;
    	View mView = View.inflate(this, R.layout.dialog_piece, null);
    	mPieceEdit = (EditText) mView.findViewById(R.id.piece);
    	mPieceEdit.setInputType(InputType.TYPE_NULL);
    	if (!Const.NO_DATA.equals(hintpiece))
    	    mPieceEdit.setHint(Const.HINT  + hintpiece);
//    	mPieceEdit.clearFocus();
//    	InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//    	im.hideSoftInputFromWindow(mPieceEdit.getWindowToken(), 0);
//    	mPieceEdit.setEnabled(false);
//    	mPieceEdit.setText(mPiece);
    	
    	GridView mCalcGridView = (GridView) mView.findViewById(R.id.gridview_layout_calc);
		mCalcGridView.setAdapter(new PupWidowsAdapter(getApplicationContext(), new String[]{
			"1","2","3",
			"4","5","6",
			"7","8","9",
			".","0","C"
			},true));
    	
		mCalcGridView.setOnItemClickListener(mPieceGridViewListener);
    	
    	
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle("价格");
		dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setView(mView);
//		dialogBuilder.setCancelable(true);
		dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//insert DB(Recordin)
				try {
					Float.parseFloat(mPiece);
				} catch(NumberFormatException e) {
					mPiece = "0";
					Toast.makeText(mContext, getString(R.string.number_format_error), Toast.LENGTH_SHORT).show();
					return;
				}
				mDbOperator.updateWHRecordinPiece(_id,mPiece);
				mDbOperator.updateWHRecordinProduct(_id,mPiece);
				mPiece = "0";
				SUM_HEAD.setText("$"+ Float.toString(mDbOperator.queryWHRecordINSum(Bundle_recordid)));
//				NUM_HEAD.setText("  "+Integer.toString(mDbOperator.queryRecordINNum(Bundle_recordid))+"台");
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPiece = "0";
			}
		});
		
		return dialogBuilder.create();
    }
	private OnItemClickListener mPieceGridViewListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {

			String[] calcArray = new String[] {
				//      
					"1","2","3",
					"4","5","6",
					"7","8","9",
					".","0","C"
				//   0    1    2    3
//					"1", "2", "3", "0",
				//   4    5    6    7
//	                "4", "5", "6", "C",
	            //   8    9    10   11
//	                "7", "8", "9", "" 
	                };
			switch(position) {
			    case 11 :
			    	mPiece = "0";
			    	mPieceEdit.setText(mPiece);
				    break;
				default:
					mPiece += calcArray[position];
					mPieceEdit.setText(mPiece.substring(1, mPiece.length()));
					break;
			}
		}
	};
	   final class RecentCallsAdapter extends GroupingListAdapter
       implements ViewTreeObserver.OnPreDrawListener, View.OnClickListener {
	        private boolean mLoading = true;
	        ViewTreeObserver.OnPreDrawListener mPreDrawListener;
	        private static final int REDRAW = 1;
	        private static final int START_THREAD = 2;
	        private boolean mFirst;

	        /**
	         * Reusable char array buffers.
	         */

	        public void onClick(View view) {
	            String data = view.getTag().toString();
	            String hintPiece = mDbOperator.queryRecordINhintPiece(data);
	            //step1 --->
	            AlertDialog dialog = (AlertDialog) CreateDialog(data,hintPiece);
	            dialog.show();
	        }

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
	            super(DtWHRecordInFrgmtActivity.this);
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
	            View view = inflater.inflate(R.layout.record_calls_list_item, parent, false);
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
	            views.remark = (TextView) view.findViewById(R.id.remark);
	            views.line2View = (TextView) view.findViewById(R.id.line2);
	            views.labelView = (TextView) view.findViewById(R.id.label);
	            views.typeView = (TextView) view.findViewById(R.id.type);
	            views.dateView = (TextView) view.findViewById(R.id.date);
	            views.rownumView = (TextView) view.findViewById(R.id.rownum);
	            views.callView = (TextView)view.findViewById(R.id.call_icon);
	            views.callView.setOnClickListener(this);
	            view.setTag(views);
	        }

	        public void bindView(Context context, View view, Cursor c) {
	            final RecentCallsListItemViews views = (RecentCallsListItemViews) view.getTag();
	            //
	            int rownumber = c.getInt(COLUMN_INDEX_ID); //rownum 0
	            views.callView.setTag(rownumber);
	            
	            //No.
	            int No = c.getCount() - c.getPosition();
	            if(!Bundle_Gesture) {
	            	No = c.getPosition() + 1;
	            }
	            views.rownumView.setText(Integer.toString(No));
	            
	            //piece
	            String mPiece = c.getString(COLUMN_INDEX_PIECE); //data5  8
	            mPiece = mPiece.trim();
               views.callView.setText(mPiece);
	           
//               String data2 = mDbOperator.getDataLeafName( c.getString(COLUMN_INDEX_DATA2)); //data1  4
               String data3 =  c.getString(COLUMN_INDEX_DATA3);
               views.labelView.setText(data3+"  "+getString(R.string.str_spec));
	            
               String remark = c.getString(COLUMN_INDEX_REMARK);
	            views.remark.setText(remark);
	            
	            String leafname = mDbOperator.getDataLeafName(Const.TABLE_warhosleaf,  c.getString(COLUMN_INDEX_DATA1)); //name 5
	            String[] treeid = c.getString(COLUMN_INDEX_DATA1).split(Const.KEY_DELIMITER_AND);
	            String treename = mDbOperator.getWarhosTreeName(treeid[0]);
	            if (!TextUtils.isEmpty(leafname)) {
	                views.line1View.setText(leafname +"  - "+ treename);
	                
	                int num = c.getInt(COLUMN_INDEX_NUM); //num 3
	                views.line2View.setText(Integer.toString(num));
	                
	                // "type" and "label" are currently unused for SIP addresses.
	                CharSequence numberLabel = null;
	                String product = c.getString(COLUMN_INDEX_SUM);
	                views.dateView.setText("$ "+product);
	                
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

	            // Listen for the first draw
	            if (mPreDrawListener == null) {
	                mFirst = true;
	                mPreDrawListener = this;
	                view.getViewTreeObserver().addOnPreDrawListener(this);
	            }
	        }

	    }
	   
	    public static final class RecentCallsListItemViews {
	        TextView line1View;
	        TextView remark;
	        TextView line2View;
	        TextView labelView;
	        TextView typeView;
	        TextView dateView;
	        TextView rownumView;
	        TextView callView;
	        ImageView groupIndicator;
	        TextView groupSize;
	    }
	    
	    static final int COLUMN_INDEX_ID = 0;
	    static final int COLUMN_INDEX_PHONENUMBER = 1;
	    static final int COLUMN_INDEX_NUM = 3;
	    static final int COLUMN_INDEX_DATA1 = 4;
	    static final int COLUMN_INDEX_DATA2 = 5;
	    static final int COLUMN_INDEX_DATA3 = 6;
	    static final int COLUMN_INDEX_SUM = 7;
	    static final int COLUMN_INDEX_PIECE = 8;
	    static final int COLUMN_INDEX_DATE = 9;
	    static final int COLUMN_INDEX_REMARK = 10;
	    
	    private PopupWidows mMenuWindow;
	    
		public void PopupWindow() {/*
			if (mMenuWindow == null) {
				mMenuWindow = new PopupWidows(this);
				initPupWidowsAdapter();
			    mMenuWindow.setPopupWindowListener(PoPupGridViewListener,mPopClearButtonListener,
			    		mPopOkButtonListener, mPupAdapter,mChangeElementSortListener);
			}
//			View layout = RecordListActivity.this.findViewById(R.id.recordbase);
			mMenuWindow.showAtLocation(this.findViewById(R.id.linerbase),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			mMenuWindow.update();*/
		}
		public String calc(int position) {
			String[] calcArrays = new String[] {
				//   0    1    2    3
					"1", "2", "3", "C",
					
				//   4    5    6    7
	                "4", "5", "6", "PS.",
	            //   8    9    10   11
	                "7", "0", "8", "9" };
			String[] calcArray = new String[] {
					//   0    1    2 
						"1", "2", "3",
						
					//   3    4    5 
		                "4", "5", "6", 
		            //   6    7    8 
		                "7", "8", "9",
		            //   9    10   11
		                "C", "0", "Ps."};
			switch(position) {
			    case 9 :
			    	NumStr = "0";
			    	mMenuWindow.setNum(NumStr);
				    break;
			    case 11 :
			    	PSDialog();
				    break;
			    case 12:
			    case 13:
			    case 14:
			    case 15:
			    case 16:
			    case 17:
			    case 18:
			        break;
				default:
					NumStr += calcArray[position];
					mMenuWindow.setNum(NumStr.substring(1,NumStr.length()));
					if (Integer.parseInt(NumStr)>=9999)
						Toast.makeText(mContext, getString(R.string.num_limit), Toast.LENGTH_SHORT).show();
					break;
			}
			return NumStr;
		}
	    private void PSDialog() {
	    	map.clear();
	     new AlertDialog.Builder(this)
	             .setTitle(R.string.dialog_title_remark)
	             .setIcon(R.drawable.ic_dialog_icon)
	             .setMultiChoiceItems(langs, null,
	                     new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,int which, boolean isChecked) {
									map.put(which, isChecked);
								}
	                     })
	             .setPositiveButton("OK",
	                     new DialogInterface.OnClickListener() {
	                         public void onClick(DialogInterface dialog,int which) {
	                        	 remarkStr="";
	                        	 for(int i = 0 ;i < langs.length;i++) {
	                        		 if(map.containsKey(i)) {
	                        			 if((Boolean) map.get(i)) remarkStr += langs[i];
	                        		 }
	                        	 }
	                        	 mBodyLine.setRemark(remarkStr);
	                        	 Toast.makeText(mContext, remarkStr,Toast.LENGTH_LONG).show();
	                         }
	                     }).show();
	    }
		final String[] langs = { "天然气", "无炉头", "大架","中铜","双头" };
		Map map = new TreeMap<Integer, Boolean>();
		String remarkStr = "";
		private int TypeCount;
		// PopupWindow gridview
		private OnItemClickListener PoPupGridViewListener = new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				if (PageIndex == TypeCount && PageIndex != 0) {
					calc(position);
				} else {
					String tmpData = _TextArray[position];
					int nextPage = PageIndex + 1;
					if (tmpData != null) {
						String dataLeafID = mDbOperator.getDataLeafID(Const.TABLE_PupWinContent,tmpData);
						tmpData = dataLeafID;
						mBodyLine.setData(PageIndex, tmpData);
						if (nextPage < TypeCount) {
							setNextGirdDate(nextPage);
						} else {
							mMenuWindow.calcVisibility(true);
						}
						PageIndex++;
					}
				}
			}
		};
		private OnClickListener mPopClearButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				clearPopWindow();
				//test for sort element successful
//				startPupWinContent(PageIndex);
			}
		};
		private OnClickListener mChangeElementSortListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (PageIndex < TypeCount)
				    startPupWinContent(PageIndex);
			}
		};
		private OnClickListener mNewRecordListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
					 final String items[]={"补录"};  
				        //dialog参数设置  
				        AlertDialog.Builder builder=new AlertDialog.Builder(DtWHRecordInFrgmtActivity.this);  //先得到构造器  
				        builder.setTitle("提示"); //设置标题  
				        //builder.setMessage("是否确认退出?"); //设置内容  
//				        builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可  
				        //设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。  
				        builder.setItems(items,new DialogInterface.OnClickListener() {  
				            @Override  
				            public void onClick(DialogInterface dialog, int which) {
				    	        SUM_HEAD.setText("");
				    	        NUM_HEAD.setText("");
//				                dialog.dismiss();
				            	switch (which) {
								case 0:
									DateDialog(false);
									break;
								case 1:
									createNewRecord();
									break;
								default:
									break;
								}
				                Toast.makeText(mContext, items[which], Toast.LENGTH_SHORT).show();  
				  
				            }
				        });  
				        builder.create().show();  
			}
		};
		private void AddRecord() {
			String recordid = null;
			String time = null;
			if (!TextUtils.isEmpty(Bundle_telnumber)) {

					time =Dialog_dateBulu+ Utils.systemFrmtTime("yyMMddHHmmss").substring(6);
					recordid = Bundle_telnumber + time;
					mDbOperator.insertRecordToday(recordid, Bundle_telnumber);  //zhang  dan suo yin
					
					Bundle_recordid = recordid;
					startQuery();
					clearPopWindow();
			}
		}
		private void createNewRecord() {
			String recordid = null;
			String time = null;
			if (!TextUtils.isEmpty(Bundle_telnumber)) {

					time = Utils.systemFrmtTime("yyMMddHHmmss");
					recordid = Bundle_telnumber + time;
					mDbOperator.insertRecordToday(recordid, Bundle_telnumber);  //zhang  dan suo yin
					
					Bundle_recordid = recordid;
					startQuery();
					clearPopWindow();
			}

			// update recorid for open case
			mDbOperator.updateCallsRecordid(Bundle_ID,recordid);

			// for UI (is clicked)
			mDbOperator.updateCallsUsed(Bundle_ID,Const.USED_NEW_RECORD);
			
		}
		private String Dialog_dateBulu = null;
		public void DateDialog(final boolean longClick) {
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int monthOfYear = calendar.get(Calendar.MONTH);
			
			int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog datePickerDialog = new DatePickerDialog(
					DtWHRecordInFrgmtActivity.this,
					new DatePickerDialog.OnDateSetListener() {
						 boolean mFired = false;
						@Override
						public void onDateSet(DatePicker view, int year,
								int monthOfYear, int dayOfMonth) {
							if (mFired == true) {
								return;
						} else {
							// first time mFired
							mFired = true;
							String month = String.valueOf(monthOfYear + 1);
							String day = String.valueOf(dayOfMonth);
							if (monthOfYear < 10) {
								month = "0" + month;
							}
							if (dayOfMonth < 10) {
								day = "0" + day;
							}
							String date = year + month + day;
							Dialog_dateBulu = date;
							Toast.makeText(mContext, date, Toast.LENGTH_SHORT)
									.show();
							// startQuery();
							AddRecord();
						}}
					}, year, monthOfYear, dayOfMonth);
			datePickerDialog.setTitle(longClick ? R.string.dialog_date_end
					: R.string.dialog_date_bulu);
			datePickerDialog.show();
		}
		// PopupWindow title button
		private OnClickListener mPopOkButtonListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				//num
				if (NumStr != null || !NumStr.equals("0")) {
					try {
					   mBodyLine.setNum(Integer.parseInt(NumStr));
					}catch(Exception e) {
						Toast.makeText(mContext, "Num Error", Toast.LENGTH_SHORT).show();
						return;
					}
			    }

				mBodyLine.setPhone(Bundle_telnumber);
				mBodyLine.setRecordid(Bundle_recordid);
				mBodyLine.setDate(Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss"));
				
				mDbOperator.insertRecordin(mBodyLine);
				mBodyLine.clear();
				
				//update Today`flag(data2) to show
				mDbOperator.updateRecordTodayUsed(Bundle_recordid);
				
				//update TodayIndex(used) to show
				mDbOperator.updateRecordTodayIndexUsed(Bundle_telnumber);
				
				NumStr = "0";
				PageIndex = 0;
				mMenuWindow.calcVisibility(false);
				setNextGirdDate(PageIndex);
				mMenuWindow.setNum(NumStr);
//				startQuery();
			}
		};
		public void initPupWidowsAdapter() {
			_TextArray = new String[Const.PUPWIN_CONTENT_ARRAY_MAX];
			ArrayList<String> PupContName = mDbOperator.getPupContentNames(Utils.toTypeID(PageIndex));
			Iterator<String> ite = PupContName.iterator();
			int k = 0;
			while(ite.hasNext()) {
				if (k == Const.PUPWIN_CONTENT_ARRAY_MAX) break;
				_TextArray[k++] = ite.next();
			}
			while(k < Const.PUPWIN_CONTENT_ARRAY_MAX) {
				_TextArray[k++]=null;
			}
			mPupAdapter = new PupWidowsAdapter(mContext, _TextArray,false);
		}
		
		private void setNextGirdDate( int index) {
			ArrayList<String> PupContName = mDbOperator.getPupContentNames(Utils.toTypeID(index));
			Iterator<String> ite = PupContName.iterator();
			int k = 0;
			while(ite.hasNext()) {
				if (k == Const.PUPWIN_CONTENT_ARRAY_MAX) break;
				_TextArray[k++] = ite.next();
			}
			while(k < Const.PUPWIN_CONTENT_ARRAY_MAX) {
				_TextArray[k++]=null;
			}
			mMenuWindow.notifyDataSetChange();
		}
		private String NumStr = "0";
		private BodyLine mBodyLine = new BodyLine();
		private void clearPopWindow() {
			//num
			if (mMenuWindow == null ) return;
			mBodyLine.clear();
			
			NumStr = "0";
			PageIndex = 0;
			mMenuWindow.calcVisibility(false);
			setNextGirdDate(PageIndex);
			mMenuWindow.setNum(NumStr);
		}
	    // change element to resort 
		private void startPupWinContent(int typeid) {
			Intent intent = new Intent();
			intent.setClass(this, PoPupWidowsDataLeaf.class);
			intent.putExtra(Const.BUNDLE_TYPEID, typeid);
			intent.putExtra(Const.BUNDLE_DRAG, true);
			startActivity(intent);
		}
	    public class GestureListener extends SimpleOnGestureListener {
	    	
	    	@Override
	    	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
	    			float velocityY) {
	    		try {
	    		//left --> right
	    		if (e2.getX() - e1.getX() > 40 && Math.abs(velocityX) > 0 && (Math.abs(e2.getY() - e1.getY()) < 100) ) {
	    			 PopupWindow();
	    		}
	    		} catch(Exception e){
	    			
	    		}
	    		return false;
	    	}
	    }
}
