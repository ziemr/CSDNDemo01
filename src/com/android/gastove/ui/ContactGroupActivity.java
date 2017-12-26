package com.android.gastove.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.AlertDialog;
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
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gastove.R;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.util.BodyLine;
import com.android.gastove.util.Const;
import com.android.gastove.util.GroupingListAdapter;
import com.android.gastove.util.SharedPrefsData;
import com.android.gastove.util.Utils;


public class ContactGroupActivity extends FragmentActivity {
	
	RecentCallsAdapter mAdapter;
    private QueryHandler mQueryHandler;
    private boolean mScrollToTop;
    private static final String TAG = "ContactGroup";
    private static final int QUERY_TOKEN = 55;
    
    
    public static final class IndexListItemViews {
        TextView txt_data1;
        TextView txt_data2;
        TextView txt_data3;
        TextView txt_data4;
        ImageView callView;
    }
    
    static final int ID_COLUMN_INDEX = 0;
    static final int RECORDID_COLUMN_INDEX = 1;
    static final int groupName_COLUMN_INDEX = 2;
    static final int groupPhone_COLUMN_INDEX = 3;
    static final int DATE_COLUMN_INDEX = 4;
    
    private int STATUS_NEWGROUP = 1;
    private int STATUS_JIONGROUP = 2;
    private int STATUS_VIEWGROUP = 3;
    private int STATUS_FLAG = 0;
    private Context mContext;
	private DBOperator mDbOperator;
	private static ContactGroupActivity mIndexFrgmt;
    /**
     * 僀儞僗僞儞僗庢摼
     * @return
     */
    public static ContactGroupActivity getInstance(){
        if( mIndexFrgmt == null ){
        	mIndexFrgmt = new ContactGroupActivity();
        }
        return mIndexFrgmt;
    }
    private OnItemClickListener mClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view,
				int position, long id) {
			// TODO 帺摦惗惉偝傟偨儊僜僢僪丒僗僞僽
			Cursor cursor = (Cursor) mAdapter.getItem(position);
			String groupleader = cursor.getString(groupPhone_COLUMN_INDEX);
//			Intent intent = new Intent(mContext,IndexRecordFrgmtActivity.class);
//			intent.putExtra(Const.BUNDLE_TEL_NUMBER, number);
//			intent.putExtra(Const.BUNDLE_TOADY,Bundle_Today );
//			startActivity(intent);
			
			if (STATUS_FLAG == STATUS_JIONGROUP) 
			    jonGroupDialog(groupleader);
			    
			else if(STATUS_FLAG == STATUS_VIEWGROUP) {
				String groupID = cursor.getString(RECORDID_COLUMN_INDEX);
				Intent intent = new Intent();
				intent.setClass(mContext, ContactGroupMemberActivity.class);
				intent.putExtra(Const.BUNDLE_ID, groupID);
				
				startActivity(intent);
			}
			
			
		}
    };
	
	private void jonGroupDialog(final String leaderphone) {
		String coname = mDbOperator.getContactName(Bundle_thephone);
		String leadername =mDbOperator.getContactName(leaderphone);
		
    	String dialogmessage = null; //getString(R.string.dialog_mms_backup)+ Const.KEY_NEXTLINE+ getString(R.string.dialog_mms_backup_time);
    	dialogmessage = "确定将  " + coname + "  加入分组：" +  leadername;
//    	try {
//    		dialogmessage += new SharedPrefsData(ContactGroupActivity.this).getSharedData(Const.SHAREPREFS_DATABUCKUP_TIME);
//    	}catch(Exception e){
//    		dialogmessage += "join faile";
//    	}
       
    	new AlertDialog.Builder(ContactGroupActivity.this)
        .setIcon(android.R.drawable.ic_dialog_alert)   
        .setTitle(R.string.dialog_title_warnning)   
        .setMessage(dialogmessage)
        .setPositiveButton(android.R.string.ok,new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				String groupID = mDbOperator.getContactsGroupID(leaderphone);
				mDbOperator.upContactsGroup(groupID, Bundle_thephone, false);
				Utils.showToast(mContext, "加入成功");
				STATUS_FLAG = 0;
			}
		})
        .setNegativeButton(android.R.string.cancel, null)
        .create().show();
    }
    private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
    	
        @Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			startQuery();
		}
    };
    
    private void createNewGroup() {
    	String groupID = mDbOperator.insertContactGroup(Bundle_thephone);
    	mDbOperator.upContactsGroup(groupID, Bundle_thephone,true);
    	startQuery();
    }
    private void ShowDialog(String telphone,String contactid) {
		// dialog参数设置
		AlertDialog.Builder builder = new AlertDialog.Builder(
				ContactGroupActivity.this); // 先得到构造器
		builder.setTitle("提示"); // 设置标题
		// builder.setMessage("是否确认退出?"); //设置内容
		// builder.setIcon(R.mipmap.ic_launcher);//设置图标，图片id即可
		// 设置列表显示，注意设置了列表显示就不要设置builder.setMessage()了，否则列表不起作用。
        final String itemarray[]= new String[2];
//        if (contactid != null) {
//        	itemarray[0] = "change";
//        	itemarray[1] = "test";
//        } else {
        	itemarray[0] = "新建分组";
        	itemarray[1] = "加入分组";
//        }
		
		builder.setItems(itemarray, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
//				SUM_HEAD.setText("");
//				NUM_HEAD.setText("");
				// dialog.dismiss();
				switch (which) {
				case 0:
					createNewGroup();
					STATUS_FLAG = STATUS_NEWGROUP;
					break;
				case 1:
					STATUS_FLAG = STATUS_JIONGROUP;
					break;
				case 2: // OPEN
//					editEnable = true;
					break;
				case 3: // EDIT ALL

					break;
				default:
					break;
				}
			}
		});

		builder.create().show();
    }
    
	   @Override
	public void onCreate(Bundle state) {
	        super.onCreate(state);
	        setContentView(R.layout.dt_frgmtaty_recordin_layout); 
	        mContext = getApplicationContext();
	        
			mListView = (ListView) findViewById(R.id.callslist);
			mAdapter = new RecentCallsAdapter();
			
//			mListView.setOnCreateContextMenuListener(this);
			mListView.setAdapter(mAdapter);
			mListView.setOnItemClickListener(mClickListener);
	        
	        mQueryHandler = new QueryHandler(this);
	        mDbOperator = new DBOperator(mContext);
	        
	        Intent intent = getIntent();
	        Bundle_thephone = intent.getStringExtra(Const.BUNDLE_TEL_NUMBER);
	        Bundle_contactId = intent.getStringExtra(Const.BUNDLE_ID);
	        if (Bundle_thephone != null) {
	        	ShowDialog(Bundle_thephone,Bundle_contactId);
	        }else {
	        	STATUS_FLAG = STATUS_VIEWGROUP;
	        }
	        
	    }
	   private ListView mListView;

	ArrayList<BodyLine> mBodyArrays;
	   private String Bundle_thephone = null;
	   private String Bundle_contactId = null;
	
	   @Override
	public void onStart() {
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
	        super.onPause();
	        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
	    }

	    @Override
		public void onDestroy() {
	        super.onDestroy();
	        mAdapter.changeCursor(null);
	        STATUS_FLAG  = 0;
	    }
	    
	    private Uri RecordTodayUri = null;

	private void startQuery() {
		mAdapter.setLoading(true);

		// Cancel any pending queries
		mQueryHandler.cancelOperation(QUERY_TOKEN);

		RecordTodayUri = mDbOperator.getTableUri(Const.TABLE_ContactGroup);
//		String selection = "used=? and today=?";
//		String[] selectionArgs = new String[] { Const.USED,Bundle_Today};
		String selection = null;
		String[] selectionArgs = null;
		mQueryHandler.startQuery(QUERY_TOKEN, null, RecordTodayUri,
				new String[] { "*" }, selection, selectionArgs, null);
	}
	   
	    private static final int MENU_ITEM_INTENT = 1;
	    private static final int MENU_ITEM_DELETE_ALL = 2;
	    
	    private static final int DIALOG_CONFIRM_INTENT = 1;
	    private static final int CONTEXT_MENU_ITEM_DELETE = 1;
	    
	   private static final class QueryHandler extends AsyncQueryHandler {
	        private final WeakReference<ContactGroupActivity> mActivity;

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
	            mActivity = new WeakReference<ContactGroupActivity>(
	                    (ContactGroupActivity) context);
	        }

	        @Override
	        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
	            final ContactGroupActivity activity = mActivity.get();
	            if (activity != null && !activity.isFinishing()) {
	                final ContactGroupActivity.RecentCallsAdapter callsAdapter = activity.mAdapter;
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

	        /**
	         * Reusable char array buffers.
	         */


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
	            View view = inflater.inflate(R.layout.tab_frgmt_index_list_item, parent, false);
	            findAndCacheViews(view);
	            return view;
	        }

	        @Override
	        protected void bindStandAloneView(View view, Context context, Cursor cursor) {
	            bindView(context, view, cursor);
	        }

	        private void findAndCacheViews(View view) {

	        	IndexListItemViews views = new IndexListItemViews();
	            views.txt_data1 = (TextView) view.findViewById(R.id.txt_data1);
	            views.txt_data2 = (TextView) view.findViewById(R.id.txt_data2);
	            views.txt_data3 = (TextView) view.findViewById(R.id.txt_data3);
	            views.txt_data4 = (TextView) view.findViewById(R.id.txt_data4);
	            views.callView = (ImageView)view.findViewById(R.id.img_caller_icon);
//	            views.callView.setBackgroundResource(R.drawable.ic_tab_unselected_friends_list);
	            view.setTag(views);
	        }

		public void bindView(Context context, View view, Cursor c) {
			final IndexListItemViews views = (IndexListItemViews) view.getTag();

			String phonenum = c.getString(groupPhone_COLUMN_INDEX);
			views.txt_data2.setText("****"+ phonenum.substring(phonenum.length()-3));
			String name = c.getString(groupName_COLUMN_INDEX);
			if (!TextUtils.isEmpty(phonenum)) {
				views.txt_data1.setText(name);
			} else {
				// name not insert DB
				views.txt_data1.setText(phonenum);
			}

			// Listen for the first draw
			if (mPreDrawListener == null) {
				mFirst = true;
				mPreDrawListener = this;
				view.getViewTreeObserver().addOnPreDrawListener(this);
			}
		}
	    }
}
