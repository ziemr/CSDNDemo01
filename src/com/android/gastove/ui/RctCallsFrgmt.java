package com.android.gastove.ui;

import java.lang.ref.WeakReference;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
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
import android.provider.CallLog.Calls;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.gastove.R;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.settings.ContactsInfo;
import com.android.gastove.util.Const;
import com.android.gastove.util.GroupingListAdapter;
import com.android.gastove.util.Utils;

@SuppressLint("ValidFragment")
public class RctCallsFrgmt extends Fragment 
{

//	private Fragment mRctCallsFrgmt;
	private android.support.v4.app.FragmentManager FM;
    
	RecentCallsAdapter mAdapter;
    private QueryHandler mQueryHandler;
    private boolean mScrollToTop;
    private static final String TAG = "RecordListActivity";
    private static final int QUERY_TOKEN = 55;
    
    private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
    	
        @Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			startQuery();
		}
    };
    
    private DBOperator mDbOperator;
    private Context mContext;
    private static RctCallsFrgmt mCallsFrgmt;
    
	public RctCallsFrgmt()
	{
//		this.tabType = tabType;
	}

    public static RctCallsFrgmt getInstance(){
        if( mCallsFrgmt == null ){
        	mCallsFrgmt = new RctCallsFrgmt();
        }
        return mCallsFrgmt;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		// 设置Fragment不重建
        setRetainInstance(true);
		mContext = getActivity();
		FM = getFragmentManager();
		
//		Bundle bundle = getArguments();
//		deptId = bundle.getLong("id");
//		deptName = bundle.getString("name");
		mAdapter = new RecentCallsAdapter(mContext);
		mQueryHandler = new QueryHandler(this);
		mDbOperator = new DBOperator(mContext);
//		想让Fragment中的onCreateOptionsMenu生效必须先调用setHasOptionsMenu方法
		setHasOptionsMenu(true);
//        getListView().setOnCreateContextMenuListener(this);
//        setListAdapter(mAdapter);
//        getListView().setOnTouchListener(mListTouchListener);
	}
	
	private ListView mListView;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
	{
		
		View mView =inflater.inflate(R.layout.tab_frgmt_rctcalls , container, false); 
//		View view = inflater.inflate(R.layout.tab_frgmt_rctcalls, null);
		mListView = (ListView) mView.findViewById(R.id.callslist);
		mListView.setOnCreateContextMenuListener(this);
		mListView.setAdapter(mAdapter);
//		TextView tip = (TextView) view.findViewById(R.id.id_tip);
//		tip.setText(TabAdapter.TITLES[tabType]);
//		mListView.setOnCreateContextMenuListener(this);
		return mView;
		 //test
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		 //test
//        mDbOperator.insertCalls("18953186556");
//        mDbOperator.insertCalls("18953186557");
//		mDbOperator.DelCallsEntherDay();
		
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
        mScrollToTop = true;
		super.onStart();
	}


	@Override
	public void onResume() {
		startQuery();
        super.onResume();
        mAdapter.mPreDrawListener = null; // Let it restart the thread after next draw
        
        Uri AUTHORITY_URI = Uri.parse("content://" + Const.AUTHORITY +"/" + Const.TABLE_RecordIN);
        mContext.getContentResolver().registerContentObserver(AUTHORITY_URI, true, mContentObserver);
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mContext.getContentResolver().unregisterContentObserver(mContentObserver);
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		mAdapter.changeCursor(null);
	}

	private Uri RecentIncomingUri = null;

	   private void startQuery() {
	        mAdapter.setLoading(true);

	        // Cancel any pending queries
	        mQueryHandler.cancelOperation(QUERY_TOKEN);
	        RecentIncomingUri = mDbOperator.getTableUri(Const.TABLE_Calls);
	        mQueryHandler.startQuery(QUERY_TOKEN, null, RecentIncomingUri,
	        		RECENT_IN_PROJECTION, null, null, "date DESC");
	        
	        /*
	        //incoming
	        String selection = Calls.TYPE  + " IN (" + Calls.INCOMING_TYPE +"," +Calls.MISSED_TYPE + ")";
	        // Cancel any pending queries
	        mQueryHandler.cancelOperation(QUERY_TOKEN);
	        mQueryHandler.startQuery(QUERY_TOKEN, null, Calls.CONTENT_URI,
	                CALL_LOG_PROJECTION, selection, null, Calls.DEFAULT_SORT_ORDER);
	        */
	    }
	
	
	   private static final class QueryHandler extends AsyncQueryHandler {
	        private final WeakReference<RctCallsFrgmt> mCurrentFrgmt;

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

//	        HomePageActivity fragment = null;
//	        List<Fragment> list = MainActivity.this.getSupportFragmentManager().getFragments();
//	        for(Fragment fment : list){
//	        if(fment instance of HomePageActivity){
//	        fragment = (HomePageActivity) fment;
	        
	        public QueryHandler(Fragment context) {
	            super(context.getActivity().getContentResolver());
	            Log.d("onQueryComplete framt", "QueryHandler context"+String.valueOf(context.hashCode()));
	            mCurrentFrgmt = new WeakReference<RctCallsFrgmt>(
	                    (RctCallsFrgmt) context);
	        }

	        @Override
	        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
//	        	rowNo = cursor.getCount();
	            final RctCallsFrgmt frgmt = mCurrentFrgmt.get();
	            Log.d("onQueryComplete framt", "onQueryComplete get frmt" + String.valueOf(frgmt.hashCode()));
	            if (frgmt != null && !frgmt.isDetached()) {
	                final RctCallsFrgmt.RecentCallsAdapter callsAdapter = frgmt.mAdapter;
//	                int count = cursor.getCount();
//	                Log.d("onQueryComplete framt", "onQueryComplete count" + String.valueOf(count));
//	                cursor.moveToFirst();
//	                int num = cursor.getInt(0);
	           //     callsAdapter.setLoading(false);
	                callsAdapter.changeCursor(cursor);
 	                if (frgmt.mScrollToTop) {
	                	frgmt.mScrollToTop = false;
	                }
	            } else {
	                cursor.close();
	                }
	        }
	    }	
	
	
	
	


@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		// TODO Auto-generated method stub
        menu.add(0, MENU_ITEM_DELETE_ALL, 0, R.string.recentCalls_deleteAll)
        .setIcon(R.drawable.ic_menu_delete_all);
//        menu.findItem(id).setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS)
        super.onCreateOptionsMenu(menu, inflater);
//return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
        switch (item.getItemId()) {
        case MENU_ITEM_DELETE_ALL:
        	CreateDialog(MENU_ITEM_DELETE_ALL).show();
            return true;
    }
    return super.onOptionsItemSelected(item);
	}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfoIn) {
		AdapterView.AdapterContextMenuInfo menuInfo;
        try {
             menuInfo = (AdapterView.AdapterContextMenuInfo) menuInfoIn;
        } catch (ClassCastException e) {
            Log.e(TAG, "bad menuInfoIn", e);
            return;
        }
        Cursor cursor = (Cursor) mAdapter.getItem(menuInfo.position);
        String phonenum = cursor.getString(PHONENUMBER_COLUMN_INDEX);
        
        String name = mDbOperator.getContactName(phonenum);
        menu.setHeaderTitle(name);
        //menu one
        Intent intent = new Intent();
	    intent.putExtra(Const.BUNDLE_TEL_NUMBER, phonenum);
	    intent.putExtra(Const.BUNDLE_TOADY, "ALL");
	    intent.setClassName("com.android.gastove", "com.android.gastove.ui.IndexRecordFrgmtActivity");
        menu.add(0, MENU_ITEM_DELETE_ALL, 0, R.string.menu_ContactRecordin).setIntent(intent);
        //menu two

		String _id = cursor.getString(ID_COLUMN_INDEX);
		String recordid = null;
		String time = null;
		if (!TextUtils.isEmpty(phonenum)) {
//			String oldRecordid = mDbOperator.getCallsRecordid(_id);
			
//			if (Const.NO_DATA.equals(oldRecordid)) {
				time = Utils.systemFrmtTime("yyMMddHHmmss");
				recordid = phonenum + time;

//				mDbOperator.insertRecordToday(recordid, phonenum);  //zhang  dan suo yin

				// for reopen
//				mDbOperator.updateCallsRecordid(_id, recordid);
				// for UI (is clicked)
//				mDbOperator.updateCallsUsed(_id,Const.USED);
//				mDbOperator.insertRecordTodayIndex(phonenum);
//			} else {
//				recordid = oldRecordid;
//			}

					Intent intentR = new Intent();
					intentR.setClass(mContext, DtRecordInFrgmtActivity.class);
					intentR.putExtra(Const.BUNDLE_RECORD_ID, recordid);
					intentR.putExtra(Const.BUNDLE_ID,_id );
					intentR.putExtra(Const.BUNDLE_TEL_NUMBER, phonenum);
					intentR.putExtra(Const.BUNDLE_READD_RECORD, true);
//					startActivity(intent);
			        menu.add(0, MENU_ITEM_ADD_CONTECT, 0, R.string.readd_record).setIntent(intentR);
		}
        
        boolean exist = mDbOperator.isContactsExist(phonenum);
        //menu three
        if (!exist) {
         	menu.setHeaderTitle(phonenum);
           	Intent intentContact = new Intent(mContext,ContactsInfo.class);
           	intentContact.putExtra(Const.BUNDLE_TEL_NUMBER, phonenum);
           	menu.add(0, MENU_ITEM_ADD_CONTECT, 0, R.string.save_contact).setIntent(intentContact);
           } 
        String contactID = null;
        //menu four
        if (exist && Const.NO_DATA.equals(contactID = mDbOperator.getContactGROUPID(phonenum))) {
        	Intent intentR = new Intent();
			intentR.setClass(mContext, ContactGroupActivity.class);
//			intentR.putExtra(Const.BUNDLE_RECORD_ID, recordid);
			intentR.putExtra(Const.BUNDLE_ID,contactID );
			intentR.putExtra(Const.BUNDLE_TEL_NUMBER, phonenum);
//			intentR.putExtra(Const.BUNDLE_READD_RECORD, true);
//			startActivity(intent);
	        menu.add(0, MENU_ITEM_ADD_CONTECTGROUP, 0, R.string.menu_contactgroup).setIntent(intentR);
        }
        super.onCreateContextMenu(menu, v, menuInfo);
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {
		 switch (item.getItemId()) {
         case 0: {
             // Convert the menu info to the proper type
             AdapterView.AdapterContextMenuInfo menuInfo;
             try {
                  menuInfo = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
             } catch (ClassCastException e) {
                 Log.e(TAG, "bad menuInfoIn", e);
                 return false;
             }

//             Cursor cursor = (Cursor)mAdapter.getItem(menuInfo.position);
//             StringBuilder sb = new StringBuilder();
//             String date = cursor.getString(DATE_COLUMN_INDEX);
//                 sb.append(id);

//             mDbOperator.deleteRecord();
             return true;
    }}
		return super.onContextItemSelected(item);
	}


	private static final int MENU_ITEM_DELETE_ALL = 1;
//	private static final int MENU_ITEM_SEARCH = 1;
	private static final int MENU_ITEM_ADD_CONTECT = 2;
	private static final int MENU_ITEM_ADD_CONTECTGROUP = 3;
    protected Dialog CreateDialog(int id) {
        switch (id) {
            case MENU_ITEM_DELETE_ALL:
                return new AlertDialog.Builder(mContext)
                    .setTitle(R.string.clearCallLogConfirmation_title)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setMessage(R.string.clearCallLogConfirmation)
                    .setNegativeButton(android.R.string.cancel, null)
                    .setPositiveButton(android.R.string.ok, new OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        	mDbOperator.clearCallsAll();
//                            startQuery();
                        }
                    })
                    .setCancelable(false)
                    .create();
        }
        return null;
    }
//private static int rowNo = -1;
	   /*
	    * Adapter
	    */
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
		String[] data = view.getTag().toString().split(Const.KEY_DELIMITER);
		String number = data[0];
		String _id = data[1];
//		String USED = data[2];
		
		String recordid = null;
//		String name = null;
		String time = null;
		if (!TextUtils.isEmpty(number)) {
//			name = mDbOperator.getContactName(number);

			String oldRecordid = mDbOperator.getCallsRecordid(_id);
			
			if (Const.NO_DATA.equals(oldRecordid)) {
				time = Utils.systemFrmtTime("yyMMddHHmmss");
				recordid = number + time;

				mDbOperator.insertRecordToday(recordid, number);  //zhang  dan suo yin

				// for reopen
				mDbOperator.updateCallsRecordid(_id, recordid);
				// for UI (is clicked)
				mDbOperator.updateCallsUsed(_id,Const.USED);
				mDbOperator.insertRecordTodayIndex(number);
			} else {
				    recordid = oldRecordid;
				
			}
			
//			FragmentTransaction ft = FM.beginTransaction();//注意。一个transaction 只能commit一次，所以不要定义成全局变量
//			long id = adapter.getDepartments().get(postion).getId();
//			String name = adapter.getDepartments().get(postion).getName();
//			DeptDocFragment df = new DeptDocFragment();
//			Bundle bundle = new Bundle();
//			bundle.putString(Const.BUNDLE_RECORD_ID, recordid);
//			bundle.putString(Const.BUNDLE_NAME, name);
//			bundle.putString(Const.BUNDLE_TEL_NUMBER, number);
//			bundle.putBoolean(Const.BUNDLE_Gesture, true);
//			df.setArguments(bundle);
//			ft.replace(R.id.id_pager, df);
//			ft.addToBackStack(null);
//			ft.commit();
			
//				if (USED.equals(Const.USED_NEW_RECORD)) {
//					SimpleDateFormat format = new SimpleDateFormat(
//							"yyyy/MM/dd HH:mm:ss");
//					time = format.format(curdate);
//					Intent intent = new Intent();
//					intent.setClass(mContext, IndexRecordFrgmtActivity.class);
//					intent.putExtra(Const.BUNDLE_TEL_NUMBER, number);
//					intent.putExtra(Const.BUNDLE_TOADY,time.substring(0,10));
////					intent.putExtra("Callfrom","Callfrom" );
//					startActivity(intent);
//					
//				} else {

					Intent intent = new Intent();
					intent.setClass(mContext, DtRecordInFrgmtActivity.class);
					intent.putExtra(Const.BUNDLE_RECORD_ID, recordid);
					intent.putExtra(Const.BUNDLE_ID,_id );
					intent.putExtra(Const.BUNDLE_TEL_NUMBER, number);
					// add item use gesture(pupwindow)
					intent.putExtra(Const.BUNDLE_Gesture, true);
					startActivity(intent);
//				}
		}

		// RecordTodayIndex
		// if (!mDbOperator.isRecordIndexExist(number))
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
//                  startRequestProcessing();
                  break;
          }
      }
  };

  public RecentCallsAdapter(Context mContext) {
      super(mContext);
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
      View view = inflater.inflate(R.layout.tab_frgmt_rctcalls_list_item, parent, false);
      findAndCacheViews(view);
      return view;
  }

  @Override
  protected void bindStandAloneView(View view, Context context, Cursor cursor) {
      bindView(context, view, cursor);
  }

  private void findAndCacheViews(View view) {

      // Get the views to bind to
	  RctCallsListItemViews views = new RctCallsListItemViews();
      views.txt_data1 = (TextView) view.findViewById(R.id.txt_data1);
      views.txt_data2 = (TextView) view.findViewById(R.id.txt_data2);
      views.txt_data3 = (TextView) view.findViewById(R.id.txt_data3);
      views.txt_data5 = (TextView) view.findViewById(R.id.txt_data5);
      views.callView = (ImageView)view.findViewById(R.id.call_icon);
      views.callView.setOnClickListener(this);
      view.setTag(views);
  }

		public void bindView(Context context, View view, Cursor c) {
			Log.d("onQueryComplete framt", "bindView");
			final RctCallsListItemViews views = (RctCallsListItemViews) view
					.getTag();

			// 0
			int rownumber = c.getInt(ID_COLUMN_INDEX); // rownum 0
			// views.rownumView.setText(Integer.toString(rownumber));
			// views.rownumView.setText(Integer.toString(rowNo--));

			// 1
			String phonenum = c.getString(PHONENUMBER_COLUMN_INDEX);// 1
			// Store away the number so we can call it directly if you click on
			// the call icon
			views.callView.setTag(phonenum + Const.KEY_DELIMITER + rownumber);

			if (phonenum.length() >= 7) {
				String tmptel ="****"
						+ phonenum.substring(phonenum.length()-3);

				views.txt_data3.setText(tmptel);
			} else {
				views.txt_data3.setText(phonenum);
			}
			String name = mDbOperator.getContactName(phonenum);

			if (!TextUtils.isEmpty(name)) {
				views.txt_data1.setText(name);
//				views.txt_data3.setText(phonenum);
			} else {
				// name not insert DB
				views.txt_data1.setText(phonenum);
			}
			String Tiems = c.getString(Tiems_COLUMN_INDEX);
			if (Tiems.equals("1")) {
				views.txt_data2.setText("");
			} else {
				// name not insert DB
				views.txt_data2.setText("(" + Tiems + ")");
			}

			String date = c.getString(DATE_COLUMN_INDEX); // date
			String Today = c.getString(Today_COLUMN_INDEX);
			if (date != null) {
				String strtime = Utils.systemFrmtTime("yyyy/MM/dd"); 
				// yyyy/MM/dd HH:mm:ss

				if (Today.equals(strtime))
					Today = date.substring(11);

				else {
					String times[] = Today.split(Const.KEY_DELIMITER_);
					int CallDay = Integer.parseInt(times[2]);
					int NowDay = Integer.valueOf(strtime.substring(strtime.length() - 2));
					String CallMonth = times[1];
					int ShowDay = NowDay - CallDay;
					if (ShowDay == 1) {
						Today = getString(R.string.ui_rct_yestoday);
					} else if (ShowDay == 2) {
						Today = getString(R.string.ui_rct_oldyestoday);
					} else {
						switch (Integer.parseInt(CallMonth)) {
						case 1:
						case 3:
						case 5:
						case 7:
						case 8:
						case 10:
						case 12:
							if (ShowDay == -30)
								Today = getString(R.string.ui_rct_yestoday);
							if (ShowDay == -29)
								Today = getString(R.string.ui_rct_oldyestoday);
							break;
						case 2:
							break;
						case 4:
						case 6:
						case 9:
						case 11:
							if (ShowDay == -29)
								Today = getString(R.string.ui_rct_yestoday);
							if (ShowDay == -28)
								Today = getString(R.string.ui_rct_oldyestoday);
							break;
						default:
							break;
						}
					}
				}
				views.txt_data5.setText(Today); // yyyy/MM/dd HH:mm:ss

			}
			int used = c.getInt(Used_COLUMN_INDEX);
			if (used == Integer.parseInt(Const.USED)) {
				// once use
				views.callView.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_menu_recordin));
				
			} else if (used == Integer.parseInt(Const.USED_NEW_RECORD)){
				// mulity use
				views.callView.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_menu_recordin_multiy));
			} else {
				// no use
				views.callView.setImageDrawable(getResources().getDrawable(
						R.drawable.ic_menu_recordin_nouse));
			}

			// Listen for the first draw
			if (mPreDrawListener == null) {
				mFirst = true;
				mPreDrawListener = this;
				view.getViewTreeObserver().addOnPreDrawListener(this);
			}
		}

}
/** The projection to use when querying the call log table */

public static final class RecentCallsListItemViews {
    TextView line1View;
    TextView line2View;
    TextView labelView;
    TextView typeView;
    TextView dateView;
    TextView rownumView;
    ImageView callView;
    ImageView groupIndicator;
    TextView groupSize;
}
public static final class RctCallsListItemViews {
    TextView txt_data1;
    TextView txt_data2;
    TextView txt_data3;
    TextView txt_data5;
    ImageView callView;
}

static final String[] RECENT_IN_PROJECTION = new String[] {
    "_id",//0
    "number",//1
    "date",//2
    "used",//3
    "data1",//4
    "data2",//5
    "name"//6
};

/** The projection to use when querying the call log table */
static final String[] CALL_LOG_PROJECTION = new String[] {
        Calls._ID,
        Calls.NUMBER,
        Calls.DATE,
        Calls.DURATION,
        Calls.TYPE,
        Calls.CACHED_NAME,
        Calls.CACHED_NUMBER_TYPE,
        Calls.CACHED_NUMBER_LABEL
};

static final int ID_COLUMN_INDEX = 0;
static final int PHONENUMBER_COLUMN_INDEX = 1;
static final int Used_COLUMN_INDEX = 3;
static final int Tiems_COLUMN_INDEX = 6;
static final int DATE_COLUMN_INDEX = 2;
static final int CALL_TYPE_COLUMN_INDEX = 4;
static final int Today_COLUMN_INDEX = 5;


}
