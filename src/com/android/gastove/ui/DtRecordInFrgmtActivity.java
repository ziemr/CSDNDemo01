package com.android.gastove.ui;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;

import com.android.gastove.R;
import com.android.gastove.adapter.PupWidowsAdapter;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.provider.DBServerOperator;
import com.android.gastove.settings.PoPupWidowsDataLeaf;
import com.android.gastove.util.BodyLine;
import com.android.gastove.util.Const;
import com.android.gastove.util.GroupingListAdapter;
import com.android.gastove.util.PopupWidows;
import com.android.gastove.util.STools;
import com.android.gastove.util.Utils;
import com.android.gastove.warehouse.WarHosDataTree;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.database.sqlite.SQLiteDiskIOException;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteFullException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.InputType;
import android.text.TextUtils;
import android.text.format.DateFormat;
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
//local phone 		
/**
 * @author Administrator
 *
 */
public class DtRecordInFrgmtActivity extends STools{

	private static final String TAG = "RecordListActivity";
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
    private Boolean Bundle_readd = false;
    private Boolean editEnable = false;
    private TextView SUM_HEAD = null;
    private TextView NUM_HEAD = null;
	private Button headNewRecord;
	private LinearLayout mLayoutHide;
	
	String[] remarkarr = null;
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
	/*
	public static void changeViewSize(ViewGroup viewGroup,int screenWidth,int screenHeight) {

	       int adjustFontSize = adjustFontSize(screenWidth,screenHeight);   

	       for(int i = 0; i<viewGroup.getChildCount(); i++ ){   

	           View v = viewGroup.getChildAt(i);   

	           if(v instanceof ViewGroup){   

	               changeViewSize((ViewGroup)v,screenWidth,screenHeight);   

	           }else if(v instanceof Button){

	               ( (Button)v ).setTextSize(adjustFontSize+2);   

	            }else if(v instanceof TextView){   

	                if(v.getId()== R.id.title_msg){   

	                    ( (TextView)v ).setTextSize(adjustFontSize+4);   

	                }else{   

	                    ( (TextView)v ).setTextSize(adjustFontSize);   

	                }   

	            }   

	        }   

	    }   


	public static int adjustFontSize(int screenWidth, int screenHeight) {   

	        screenWidth=screenWidth>screenHeight?screenWidth:screenHeight;   

	        int rate = (int)(5*(float) screenWidth/320);   

	        return rate<15?15:rate;
	} 
	*/
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		View rootView = View.inflate(this, R.layout.dt_frgmtaty_recordin_layout, null);
		setContentView(rootView);
		init(rootView);
		init();

		mLayoutHide = (LinearLayout)findViewById(R.id.head_hide);
		headNewRecord = (Button)findViewById(R.id.head_btn);

		mListView = (ListView) findViewById(R.id.callslist);
		mListView.setOnCreateContextMenuListener(this);
		mListView.setAdapter(mAdapter);
        mListView.setOnTouchListener(mListTouchListener);

		
    	Intent intent = getIntent();
        Bundle_recordid = intent.getStringExtra(Const.BUNDLE_RECORD_ID);
        Bundle_telnumber = intent.getStringExtra(Const.BUNDLE_TEL_NUMBER);
        Bundle_ID = intent.getStringExtra(Const.BUNDLE_ID);
        
        
        Bundle_readd = intent.getBooleanExtra(Const.BUNDLE_READD_RECORD, false);
        if (Bundle_readd) {
        	DateDialog(false);
        }
//        String name = mDbOperator.getContactName(Bundle_telnumber);
        setActivityTitle();
        
        Bundle_Gesture = intent.getBooleanExtra(Const.BUNDLE_Gesture, false);
        mLayoutHide.setVisibility(View.VISIBLE);
    		headNewRecord.setVisibility(View.VISIBLE);
//    		Drawable dr = mContext.getResources().getDrawable(R.drawable.ic_menu_recordin);
//    		headNewRecord.setBackgroundDrawable(dr);
    		headNewRecord.setOnClickListener(mNewRecordListener);
	}
	
	 private void init() {
			mContext= getApplicationContext();
			mAdapter = new RecentCallsAdapter();
			
	        mQueryHandler = new QueryHandler(this);
	        mDbOperator = new DBOperator(mContext);
	        
	    	//--reCall
	    	mDetector = new GestureDetector(this,new GestureListener());
	    	
	    	int adapterCount = mDbOperator.gettableCount(Const.TABLE_remark);
	    	remarkarr = new String[adapterCount];
	    	for (int i = 0; i < adapterCount; i++) {
	    		remarkarr[i]  = mDbOperator.getRemarkName(i);
	        }
	    	 
	 }
	
	   private void setActivityTitle() {
		   String name = mDbOperator.getContactName(Bundle_telnumber);
		   TextView txtHead = (TextView) findViewById(R.id.headTV);
		   SUM_HEAD = (TextView) findViewById(R.id.txt_num);
		   NUM_HEAD = (TextView) findViewById(R.id.txt_sum);
//		   String recordDay = mDbOperator.getCallsRecordDay(Bundle_ID);
		   
		   String start = Bundle_recordid.substring(Bundle_recordid.length()-12);
			String end = start.substring(8);
//			views.labelView.setText(start+"-"+end);
			
		   if (Bundle_readd) {
			   txtHead.setText(name+Const.KEY_NEXTLINE+"-"+end+"补");
		   }else {
		       txtHead.setText(name+Const.KEY_NEXTLINE+"-"+end);
		   }
//		   telView.setText(telnumber)
	   }
	   
		@Override
	protected void onResume() {
		super.onResume();
		if (!sortflag) {
			startQuery();
			mAdapter.mPreDrawListener = null; // Let it restart the thread after
												// next draw

			Uri AUTHORITY_URI = Uri.parse("content://" + Const.AUTHORITY + "/"
					+ Const.TABLE_RecordIN);
			mContext.getContentResolver().registerContentObserver(
					AUTHORITY_URI, true, mContentObserver);
			//for test
//			TypeCount = mDbOperator.gettableCount(Const.TABLE_PupWinMage);
			
			TypeCount = 3;
			// this.PopupWindow();
			if (Bundle_Gesture)
				popupHandler.sendEmptyMessageDelayed(0, 100);
			String sum = Float.toString(mDbOperator.queryRecordINSum(Bundle_recordid));
			if (sum != null && mDbOperator.isPieceNoZero(Bundle_recordid)) {
				SUM_HEAD.setText("$"+ sum);
			} else {
				SUM_HEAD.setText("$0");
			}
			NUM_HEAD.setText(" "+ Integer.toString(mDbOperator
							.queryRecordINNum(Bundle_recordid)) + "台");
		} else {
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
				mMenuWindow.notifyDataSetChange();
		}
		sortflag = false;
	}
		
	@Override
	protected void onPause() {
		super.onPause();
		if (!sortflag) {
			clearPopWindow();
			mContext.getContentResolver().unregisterContentObserver(
					mContentObserver);
			mDbOperator.updateRecordTodaySum(Bundle_recordid);
			mDbOperator.updateRecordTodayNum(Bundle_recordid);
		}
	}
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAdapter.changeCursor(null);
    }
    
	private Uri RecordinUri = null;
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
        RecordinUri = mDbOperator.getTableUri(Const.TABLE_RecordIN);
        
        String order = "date ASC";
        if (Bundle_Gesture) order = "date DESC";
        mQueryHandler.startQuery(QUERY_TOKEN, null, RecordinUri,
        		RECORD_IN_PROJECTION, Const.RECORDIN_COLUMN_RECORDID+"=? and data7 is not -1", new String[]{Bundle_recordid}, order);
    }
    
    private static final int MENU_ITEM_MMS = 1;
    private static final int MENU_ITEM_EDIT = 2;
    private static final int CONTEXT_MENU_ITEM_DELETE = 3;
    private static final int CONTEXT_MENU_ITEM_REMARK = 4;
    private static final int CONTEXT_MENU_ITEM_EDITNUM = 5;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//    	menu.add(0, MENU_ITEM_MMS, 0, R.string.optionmenu_mms)
//        .setIcon(R.drawable.ic_menu_mms);
//    	if (!Bundle_Gesture) {
//        menu.add(0, MENU_ITEM_EDIT, 0, R.string.optionmenu_money)
//        .setIcon(R.drawable.ic_menu_optionmenu_money);
//    	}
        return true;
    }
   
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_ITEM_MMS:
            	String mms =  mDbOperator.queryRecordINToMms(Bundle_recordid);
            	MMSDialog(mms);
                return true;
            case MENU_ITEM_EDIT:
            	editEnable = true;
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void MMSDialog(String message) {
    	final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);	// 系统默认Dialog没有输入框
		View alertDialogView = View.inflate(this, R.layout.dialog_rename, null);
		final EditText et_dialog_mmstxt = (EditText) alertDialogView.findViewById(R.id.edit_rename);
		et_dialog_mmstxt.setText(message);
		alertDialog.setTitle(getResources().getString(R.string.mms));
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.setPositiveButton(R.string.mms,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						String inputstr = et_dialog_mmstxt.getText().toString().trim();
//						BluetoothMsg.sendMsg("MMS" +Const.KEY_DELIMITER+ inputstr);
//							Toast.makeText(getApplicationContext(), "success", Toast.LENGTH_LONG).show();
						//local phone
						Intent sendIntent = new Intent(Intent.ACTION_VIEW);
					    sendIntent.putExtra("sms_body", inputstr);
					    sendIntent.setType("vnd.android-dir/mms-sms");
					    startActivity(sendIntent);
					}
				});
		alertDialog.setNeutralButton(R.string.weixin,  new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				String mms = et_dialog_mmstxt.getText().toString().trim();
				shareWeChatFriend("11",mms,1,Utils.textAsBitmap(mms, 20));				
			}
		});
		alertDialog.setNegativeButton(android.R.string.cancel, null);
		AlertDialog tempDialog = alertDialog.create();
		tempDialog.setView(alertDialogView, 0, 0, 0, 0);
		tempDialog.setCanceledOnTouchOutside(false);
		tempDialog.show();
	}
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {

        menu.setHeaderTitle(getString(R.string.contextmenu_title));
        menu.setHeaderIcon(R.drawable.ic_dialog_icon);
        if (Bundle_Gesture || editEnable) {
        
        menu.add(0, CONTEXT_MENU_ITEM_EDITNUM, 0, R.string.recentCalls_editnumFromRecentList);
        menu.add(0, CONTEXT_MENU_ITEM_DELETE, 0, R.string.recentCalls_removeFromRecentList);
        menu.add(0, CONTEXT_MENU_ITEM_REMARK, 0, R.string.recentCalls_remarkFromRecentList);
        } 
    }
    
    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
          case CONTEXT_MENU_ITEM_EDITNUM: {
        	AdapterView.AdapterContextMenuInfo menuInfo;
            try {
                 menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            } catch (ClassCastException e) {
                Log.e(TAG, "bad menuInfoIn", e);
                return false;
            }

            Cursor cursor = (Cursor)mAdapter.getItem(menuInfo.position);
            final int rownumber = cursor.getInt(COLUMN_INDEX_ID);
            AlertDialog dialog = (AlertDialog) NumDialog(rownumber);
            dialog.show();
        }
        break;
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

                boolean flag = mDbOperator.deleteRecordLOC(sb.toString(),Bundle_recordid);
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
	             .setMultiChoiceItems(remarkarr, null,
	                     new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,int which, boolean isChecked) {
									map.put(which, isChecked);
								}
	                     })
	             .setPositiveButton("OK",
	                     new DialogInterface.OnClickListener() {
	                         public void onClick(DialogInterface dialog,int which) {
	                        	 remarkStr="";
	                        	 for(int i = 0 ;i < remarkarr.length;i++) {
	                        		 if(map.containsKey(i)) {
	                        			 if((Boolean) map.get(i)) remarkStr += remarkarr[i];
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
	        private final WeakReference<DtRecordInFrgmtActivity> mActivity;

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
	            mActivity = new WeakReference<DtRecordInFrgmtActivity>(
	                    (DtRecordInFrgmtActivity) context);
	        }

	        @Override
	        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
	            final DtRecordInFrgmtActivity activity = mActivity.get();
	            if (activity != null && !activity.isFinishing()) {
	                final DtRecordInFrgmtActivity.RecentCallsAdapter callsAdapter = activity.mAdapter;
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
	   
	
    private String mDialogNum = "0";
    private EditText mNumEdit = null;
	 protected Dialog NumDialog(final int _id) {
    	View mView = View.inflate(this, R.layout.dialog_piece, null);
    	mNumEdit = (EditText) mView.findViewById(R.id.piece);
    	mNumEdit.setInputType(InputType.TYPE_NULL);
    	mNumEdit.setText("0");
    	
    	GridView mCalcGridView = (GridView) mView.findViewById(R.id.gridview_layout_calc);
		mCalcGridView.setAdapter(new PupWidowsAdapter(getApplicationContext(), new String[]{
			"1","2","3",
			"4","5","6",
			"7","8","9",
			" ","0","C"
			},true));
    	
		mCalcGridView.setOnItemClickListener(mNumGridViewListener);
    	
    	
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
		dialogBuilder.setTitle(getString(R.string.recentCalls_editnumFromRecentList));
		dialogBuilder.setIcon(android.R.drawable.ic_dialog_alert);
		dialogBuilder.setView(mView);
//		dialogBuilder.setCancelable(true);
		dialogBuilder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				//insert DB(Recordin)
				try {
					Integer.parseInt(mDialogNum);
				} catch(NumberFormatException e) {
					mDialogNum = "0";
					Toast.makeText(mContext, getString(R.string.number_format_error), Toast.LENGTH_SHORT).show();
					return;
				}
				mDbOperator.updateRecordinNum(String.valueOf(_id), Integer.valueOf(mDialogNum));
				mDbOperator.updateRecordinProduct(String.valueOf(_id),Integer.valueOf(mDialogNum));
				mDialogNum = "0";
				SUM_HEAD.setText("$"+Float.toString(mDbOperator.queryRecordINSum(Bundle_recordid)));
				NUM_HEAD.setText(" "+Integer.toString(mDbOperator.queryRecordINNum(Bundle_recordid))+"台");
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mDialogNum = "0";
			}
		});
		return dialogBuilder.create();
    }
	 
	/*
     *  pup dialog for piece
     */  
    private String mPiece = "0";
	private EditText mPieceEdit = null;
//	private String mPieceID = null;
    protected Dialog PieceDialog(final String _id,final String hintpiece) {
//    	 mPieceID = _id;
//    	mPiece = piece;
    	final String tmppiece = hintpiece;
    	View mView = View.inflate(this, R.layout.dialog_piece, null);
    	mPieceEdit = (EditText) mView.findViewById(R.id.piece);
    	mPieceEdit.setInputType(InputType.TYPE_NULL);
    	if (!Const.NO_DATA.equals(hintpiece))
    	    mPieceEdit.setHint(Const.HINT  + hintpiece);
    	else{
    		mPieceEdit.setText("0");
    		
    	}
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
				mDbOperator.updateRecordinPiece(_id,mPiece);
				mDbOperator.updateRecordinProduct(_id,mPiece);
				mPiece = "0";
				SUM_HEAD.setText("$"+Float.toString(mDbOperator.queryRecordINSum(Bundle_recordid)));
				NUM_HEAD.setText(" "+Integer.toString(mDbOperator.queryRecordINNum(Bundle_recordid))+"台");
			}
		});
		dialogBuilder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPiece = "0";
			}
		});
		dialogBuilder.setNeutralButton(Const.HINT, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				mPiece = tmppiece;
				try {
					Float.parseFloat(mPiece);
				} catch(NumberFormatException e) {
					mPiece = "0";
					Toast.makeText(mContext, getString(R.string.number_format_error), Toast.LENGTH_SHORT).show();
					return;
				}
				mDbOperator.updateRecordinPiece(_id,mPiece);
				mDbOperator.updateRecordinProduct(_id,mPiece);
				mPiece = "0";
				SUM_HEAD.setText("$"+Float.toString(mDbOperator.queryRecordINSum(Bundle_recordid)));
				NUM_HEAD.setText(" "+Integer.toString(mDbOperator.queryRecordINNum(Bundle_recordid))+"台");
			}
		});
		return dialogBuilder.create();
    }
    
	private OnItemClickListener mNumGridViewListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {

			String[] calcArray = new String[] {
				//      
					"1","2","3",
					"4","5","6",
					"7","8","9",
					" ","0","C"
				//   0    1    2    3
//					"1", "2", "3", "0",
				//   4    5    6    7
//	                "4", "5", "6", "C",
	            //   8    9    10   11
//	                "7", "8", "9", "" 
	                };
			switch(position) {
			    case 11 :
			    	mDialogNum = "0";
			    	mNumEdit.setText(mDialogNum);
				    break;
			    case 9:
			    	break;
				default:
					mDialogNum += calcArray[position];
					mNumEdit.setText(mDialogNum.substring(1, mDialogNum.length()));
					break;
			}
		}
	};
	
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
//	        	String[] data = view.getTag().toString().split(Const.KEY_DELIMITER);
	        	//form recordid _id
//	            AlertDialog dialog = (AlertDialog) CreateDialog(data[0],data[1]);
	            String data = view.getTag().toString();
	            String hintPiece = mDbOperator.queryRecordINhintPiece(data);
	            AlertDialog dialog = (AlertDialog) PieceDialog(data,hintPiece);
	            dialog.show();
	            if(Const.NO_DATA == hintPiece)
	            	dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setEnabled(false);
	            // piace
	            
//	            if (!TextUtils.isEmpty(number)) {
//	            }
	            
//	            PopupWindow();
//	            mMenuWindow.calcVisibility(true);
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
	            super(DtRecordInFrgmtActivity.this);
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
	           
               String data2 = mDbOperator.getDataLeafName(Const.TABLE_PupWinContent, c.getString(COLUMN_INDEX_DATA2)); //data1  4
               String data3 = mDbOperator.getDataLeafName(Const.TABLE_PupWinContent, c.getString(COLUMN_INDEX_DATA3));
               views.labelView.setText(data2+"  "+data3);
	            
               String remark = c.getString(COLUMN_INDEX_REMARK);
	            views.remark.setText(remark);
	            
	            String data1 = mDbOperator.getDataLeafName(Const.TABLE_PupWinContent,  c.getString(COLUMN_INDEX_DATA1)); //name 5
	            if (!TextUtils.isEmpty(data1)) {
	                views.line1View.setText(data1);
	                
	                int num = c.getInt(COLUMN_INDEX_NUM); //num 3
	                views.line2View.setText(Integer.toString(num));
	                
//	                if (mPiece != null && !mPiece.equals("")) {
//	                	float tmpMoney = num * Float.parseFloat(mPiece);
//	                	TOTALMONEY +=tmpMoney;
//	                }
	                
	                
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
	    
		public void PopupWindow() {
			if (mMenuWindow == null) {
				mMenuWindow = new PopupWidows(this);
				initPupWidowsAdapter();
			    mMenuWindow.setPopupWindowListener(PoPupGridViewListener,mPopClearButtonListener,
			    		mPopOkButtonListener, mPupAdapter,mChangeElementSortListener,mHotkeyListener);
			}
//			View layout = RecordListActivity.this.findViewById(R.id.recordbase);
			mMenuWindow.showAtLocation(this.findViewById(R.id.linerbase),
					Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
			mMenuWindow.update();
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
	             .setMultiChoiceItems(remarkarr, null,
	                     new DialogInterface.OnMultiChoiceClickListener() {
								public void onClick(DialogInterface dialog,int which, boolean isChecked) {
									map.put(which, isChecked);
								}
	                     })
	             .setPositiveButton("OK",
	                     new DialogInterface.OnClickListener() {
	                         public void onClick(DialogInterface dialog,int which) {
	                        	 remarkStr="";
	                        	 for(int i = 0 ;i < remarkarr.length;i++) {
	                        		 if(map.containsKey(i)) {
	                        			 if((Boolean) map.get(i)) remarkStr += remarkarr[i];
	                        		 }
	                        	 }
	                        	 mBodyLine.setRemark(remarkStr);
	                        	 Toast.makeText(mContext, remarkStr,Toast.LENGTH_LONG).show();
	                         }
	                     }).show();
	    }
	
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
		private boolean sortflag = false; 
		private OnClickListener mChangeElementSortListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (PageIndex < TypeCount)
				    startPupWinContent(PageIndex);
				sortflag = true;
			}
		};
		
private OnClickListener mHotkeyListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (PageIndex < TypeCount)
					startPartTree();
				sortflag = true;
			}
		};
		private static final int REQUEST_CODE = 1;  
	    private String strImgPath = "";//照片保存路径  
	    private File imageFile = null;//照片文件  
	    /** 定义相片的最大尺寸 **/  
	    private final int IMAGE_MAX_WIDTH = 540;  
	    private final int IMAGE_MAX_HEIGHT = 960;  
	private OnClickListener mNewRecordListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// dialog参数设置
			AlertDialog.Builder builder = new AlertDialog.Builder(
					DtRecordInFrgmtActivity.this); // 先得到构造器
			builder.setTitle("提示"); // 设置标题
			// builder.setMessage("是否确认退出?"); //设置内容
			// builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
			// 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。

			final String items[] = { getString(R.string.add_record),
					getString(R.string.share), getString(R.string.open),getString(R.string.open) };
			builder.setItems(items, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SUM_HEAD.setText("");
//					NUM_HEAD.setText("");
					// dialog.dismiss();
					String mms = null;
					switch (which) {
					case 0:
						createNewRecord();
//						SUM_HEAD.setText("");
						NUM_HEAD.setText("");
						break;
					case 1:
						mms =  mDbOperator.queryRecordINToMms(Bundle_recordid);
		            	MMSDialog(mms);
		            	Toast.makeText(mContext, getString(R.string.dialog_mms_hint), Toast.LENGTH_SHORT)
						.show();
						break;
					case 2: // OPEN
						editEnable = true;
						Bundle_Gesture = true;
						popupHandler.sendEmptyMessageDelayed(0, 100);
						break;
					case 3: // camera
//						cameraRemark();
						mms =  mDbOperator.queryRecordINToMms(Bundle_recordid);
						
						shareWeChatFriend("11",mms,1,Utils.textAsBitmap(mms, 20));
						break;
					default:
						break;
					}
				}
			});

			builder.create().show();
		}
	};
	/**
     * 分享到微信好友
     *
     * @param msgTitle
     *            (分享标题)
     * @param msgText
     *            (分享内容)
     * @param type
     *            (分享类型)
     * @param drawable
     *            (分享图片，若分享类型为AndroidShare.TEXT，则可以为null)
     */
    public void shareWeChatFriend(String msgTitle, String msgText, int type,
                                  Bitmap drawable) {

        shareMsg("com.tencent.mm", "com.tencent.mm.ui.tools.ShareImgUI", "微信",
                msgTitle, msgText, type, drawable);
    }
    /**
     * 点击分享的代码
     *
     * @param packageName
     *            (包名,跳转的应用的包名)
     * @param activityName
     *            (类名,跳转的页面名称)
     * @param appname
     *            (应用名,跳转到的应用名称)
     * @param msgTitle
     *            (标题)
     * @param msgText
     *            (内容)
     * @param type
     *            (发送类型：text or pic 微信朋友圈只支持pic)
     */
    private void shareMsg(String packageName, String activityName,
                          String appname, String msgTitle, String msgText, int type,
                          Bitmap drawable) {
        if (!packageName.isEmpty() && !isAvilible(mContext, packageName)) {// 判断APP是否存在
            Toast.makeText(mContext, "请先安装" + appname, Toast.LENGTH_SHORT)
                    .show();
            return;
        }

        Intent intent = new Intent("android.intent.action.SEND");
        if (type == 0) {
            intent.setType("text/plain");
        } else if (type == 1) {
            intent.setType("image/*");
            final Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(
            		mContext.getContentResolver(), drawable, null, null));
            intent.putExtra(Intent.EXTRA_STREAM, uri);
        }

        intent.putExtra(Intent.EXTRA_SUBJECT, msgTitle);
        intent.putExtra(Intent.EXTRA_TEXT, msgText);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (!packageName.isEmpty()) {
            intent.setComponent(new ComponentName(packageName, activityName));
            mContext.startActivity(intent);
        } else {
        	mContext.startActivity(Intent.createChooser(intent, msgTitle));
        }
    }
    /**
     * 判断相对应的APP是否存在
     *
     * @param context
     * @param packageName
     * @return
     */
    public boolean isAvilible(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();

        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        for (int i = 0; i < pinfo.size(); i++) {
            if (((PackageInfo) pinfo.get(i)).packageName
                    .equalsIgnoreCase(packageName))
                return true;
        }
        return false;
    }
	private void cameraRemark() {
		Intent getPhoto = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
//        strImgPath = Environment.getExternalStorageDirectory()  
//                .toString() + "/CSDN_PIC/";  
//        String fileName = Utils.systemDate() + ".jpg";// 照片以格式化日期方式命名  
//        File out = new File(strImgPath);  
//        if (!out.exists()) {  
//            out.mkdirs();  
//        }  
//        out = new File(strImgPath, fileName);  
//        strImgPath = strImgPath + fileName;// 该照片的绝对路径  
//        Uri uri = Uri.fromFile(out);  
//        getPhoto.putExtra(MediaStore.EXTRA_OUTPUT, uri);//根据uri保存照片  
//        getPhoto.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);//保存照片的质量  
        startActivityForResult(getPhoto, REQUEST_CODE);//启动相机拍照
	}
	 /** 保存相机的图片 **/
    private void saveCameraImage(Intent data) {
        // 检查sd card是否存在
        if (!Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            Log.i(TAG, "sd card is not avaiable/writeable right now.");
            return;
        }
        // 为图片命名啊
        String fileName = new DateFormat().format("yyyymmss",
                Calendar.getInstance(Locale.CHINA))+ ".jpg";
        Bitmap bitmap = (Bitmap) data.getExtras().get("data");// 解析返回的图片成bitmap

        strImgPath = Environment.getExternalStorageDirectory()  
                .toString() + "/CSDN_PIC/";
        File out = new File(strImgPath);  
        if (!out.exists()) {  
            out.mkdirs();  
        } 
        // 保存文件
        FileOutputStream fos = null;
        strImgPath = strImgPath + fileName;// 该照片的绝对路径
        try {// 写入SD card
            fos = new FileOutputStream(strImgPath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            try {
                fos.flush();
                fos.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            bitmap.recycle();
            bitmap = null;
            System.gc();
        }// 显示图片
//        ((ImageView) findViewById(R.id.show_image)).setImageBitmap(bmp);
    }
    private void zipImage(String savePath,Bitmap bmp) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(savePath, options);
        options.inSampleSize = computeInitialSampleSize(options, 480, 480 * 960);
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(savePath, options);
        try {
            FileOutputStream fos = new FileOutputStream(savePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        bitmap.recycle();
        bitmap = null;
        System.gc();
    }

	private int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;
		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128
				: (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
		if (upperBound < lowerBound) {
			// return the larger one when there is no overlapping zone.
			return lowerBound;
		}
		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}
	 /** 
     * 返回照片结果处理 
     */  
    @Override  
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
        super.onActivityResult(requestCode, resultCode, data);  
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
        	saveCameraImage(data);
        	
            imageFile = new File(strImgPath);  
            int scale = 0;  
                scale = getZoomScale(imageFile);//得到缩放倍数  
                Log.i(TAG, "scale = "+scale);  
                BitmapFactory.Options options = new BitmapFactory.Options();  
                options.inSampleSize = scale;  
//              photoImageView.setImageBitmap(BitmapFactory.decodeFile(strImgPath,options));//按指定options显示图片防止OOM  
            }else {  
//                Toast.makeText(MainActivity.this, R.string.failed, Toast.LENGTH_LONG).show();  
            }
        
        }  
  
    /** 
     * 图片缩放处理 
     * @param imageFile 照片文件 
     * @return 缩放的倍数 
     */  
    private int getZoomScale(File imageFile) {  
        int scale = 1;  
        BitmapFactory.Options options = new BitmapFactory.Options();  
        options.inJustDecodeBounds = true;  
        BitmapFactory.decodeFile(strImgPath, options);  
        while (options.outWidth / scale >= IMAGE_MAX_WIDTH  
                || options.outHeight / scale >= IMAGE_MAX_HEIGHT) {  
            scale *= 2;  
        }  
        return scale;  
    }  
		private void REAddRecord() {
			if (Bundle_readd) {
				mDbOperator.insertRecordToday(Bundle_recordid, Bundle_telnumber,Dialog_dateBulu);  //zhang  dan suo yin
				mDbOperator.insertRecordTodayIndex(Bundle_telnumber,Dialog_dateBulu);
				
				startQuery();
				clearPopWindow();
			}
			Bundle_Gesture = true;
			popupHandler.sendEmptyMessageDelayed(0, 100);
		}
		private void createNewRecord() {
			String recordid = null;
			String time = null;
			if (!TextUtils.isEmpty(Bundle_telnumber)) {
					time = Utils.systemFrmtTime("yyMMddHHmmss");
					recordid = Bundle_telnumber + time;
					mDbOperator.insertRecordToday(recordid, Bundle_telnumber);  //zhang  dan suo yin
					mDbOperator.insertRecordTodayIndex(Bundle_telnumber);
					Bundle_recordid = recordid;
					setActivityTitle();
					startQuery();
					clearPopWindow();
			}

			// update recorid for open case
//			mDbOperator.updateCallsRecordid(Bundle_ID,recordid);

			// for UI (is clicked)
			mDbOperator.updateCallsUsed(Bundle_ID,Const.USED_NEW_RECORD);
			
			
			Bundle_Gesture = true;
			popupHandler.sendEmptyMessageDelayed(0, 100);
		}
		private String Dialog_dateBulu = null;
		public void DateDialog(final boolean longClick) {
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int monthOfYear = calendar.get(Calendar.MONTH);
			
			int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			DatePickerDialog datePickerDialog = new DatePickerDialog(
					DtRecordInFrgmtActivity.this,
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
							String date = year +"/"+ month+"/" + day;
							Dialog_dateBulu = date;
							Toast.makeText(mContext, date, Toast.LENGTH_SHORT)
									.show();
							// startQuery();
							REAddRecord();
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
				String times = Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss");
				mBodyLine.setDate(times);// set timed
				
				mDbOperator.insertRecordin(mBodyLine);
				
				new  DBServerOperator(mContext).insertRecordin(mBodyLine); 
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
				
				NUM_HEAD.setText(" "+Integer.toString(mDbOperator.queryRecordINNum(Bundle_recordid))+"台");
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
		
		private void startPartTree() {
			Intent intent = new Intent();
			intent.setClass(this, WarHosDataTree.class);
			intent.putExtra(Const.BUND_HOTKEY, Const.KEY_VALUE_TRUE);
//			intent.putExtra(Const.BUNDLE_DRAG, true);
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

		@Override
		public void button1Click() {
			cameraRemark();
		}
}
