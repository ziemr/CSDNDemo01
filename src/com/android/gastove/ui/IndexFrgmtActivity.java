package com.android.gastove.ui;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.content.AsyncQueryHandler;
import android.content.Context;
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
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.gastove.R;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.util.BodyLine;
import com.android.gastove.util.Const;
import com.android.gastove.util.GroupingListAdapter;
import com.android.gastove.util.Utils;


public class IndexFrgmtActivity extends FragmentActivity {
	
	RecentCallsAdapter mAdapter;
    private QueryHandler mQueryHandler;
    private boolean mScrollToTop;
    private static final String TAG = "IndexFrgmtActivity";
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
    static final int PHONENUMBER_COLUMN_INDEX = 1;
    static final int NAME_COLUMN_INDEX = 3;
    static final int DATE_COLUMN_INDEX = 4;
    
    private Context mContext;
	private DBOperator mDbOperator;
	private static IndexFrgmtActivity mIndexFrgmt;
    /**
     * インスタンス取得
     * @return
     */
    public static IndexFrgmtActivity getInstance(){
        if( mIndexFrgmt == null ){
        	mIndexFrgmt = new IndexFrgmtActivity();
        }
        return mIndexFrgmt;
    }
    private OnItemClickListener mClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> arg0, View view,
				int position, long id) {
			// TODO 自動生成されたメソッド・スタブ
			Cursor cursor = (Cursor) mAdapter.getItem(position);
			String number = cursor.getString(1);
			Intent intent = new Intent(mContext,IndexRecordFrgmtActivity.class);
			intent.putExtra(Const.BUNDLE_TEL_NUMBER, number);
			intent.putExtra(Const.BUNDLE_TOADY,Bundle_Today );
			startActivity(intent);
		}
    };
	
    private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
    	
        @Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			startQuery();
		}
    };
    
    
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
			mListView.setOnCreateContextMenuListener(this);
			
	        mQueryHandler = new QueryHandler(this);
	        mDbOperator = new DBOperator(mContext);
	        
	        Intent intent = getIntent();
	        Bundle_Today = intent.getStringExtra(Const.BUNDLE_TOADY);
	        
	        if (Bundle_Today == null) {
	     // systime
			String strtime = Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss"); 
			Bundle_Today = strtime.subSequence(0, 10).toString();
	        }
	        
//	        setTitle("--" + Bundle_Today + "--");
	        
	    	
	        //-------------------------------------------------------
	    }
	   private ListView mListView;

	ArrayList<BodyLine> mBodyArrays;
	   private String Bundle_Today = null;
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
		             return true;
		    }}
				return super.onContextItemSelected(item);
			}
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
	    }
	    
	    private Uri RecordTodayUri = null;

	private void startQuery() {
		mAdapter.setLoading(true);

		// Cancel any pending queries
		mQueryHandler.cancelOperation(QUERY_TOKEN);

		RecordTodayUri = mDbOperator.getTableUri(Const.TABLE_RecordTodayIndex);
		String selection = "used=? and today=?";
		String[] selectionArgs = new String[] { Const.USED,Bundle_Today};
		mQueryHandler.startQuery(QUERY_TOKEN, null, RecordTodayUri,
				new String[] { "*" }, selection, selectionArgs, null);
	}
	   
	    private static final int MENU_ITEM_INTENT = 1;
	    private static final int MENU_ITEM_DELETE_ALL = 2;
	    
	    private static final int DIALOG_CONFIRM_INTENT = 1;
	    private static final int CONTEXT_MENU_ITEM_DELETE = 1;
	    
	   private static final class QueryHandler extends AsyncQueryHandler {
	        private final WeakReference<IndexFrgmtActivity> mActivity;

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
	            mActivity = new WeakReference<IndexFrgmtActivity>(
	                    (IndexFrgmtActivity) context);
	        }

	        @Override
	        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
	            final IndexFrgmtActivity activity = mActivity.get();
	            if (activity != null && !activity.isFinishing()) {
	                final IndexFrgmtActivity.RecentCallsAdapter callsAdapter = activity.mAdapter;
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

			String phonenum = c.getString(PHONENUMBER_COLUMN_INDEX);
			views.txt_data2.setText("****"+ phonenum.substring(phonenum.length()-3));
			String name = mDbOperator.getContactName(phonenum);
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
