package com.android.gastove.settings;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.AsyncQueryHandler;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
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
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gastove.R;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.util.BodyLine;
import com.android.gastove.util.Const;
import com.android.gastove.util.GroupingListAdapter;
import com.android.gastove.util.Utils;


public class ContactsInfo extends FragmentActivity {
	
	RecentCallsAdapter mAdapter;
    private QueryHandler mQueryHandler;
    private boolean mScrollToTop;
    private static final String TAG = "RecordListActivity";
    private static final int QUERY_TOKEN = 55;
    
    /** The projection to use when querying the call log table */
    static final String[] CONTACT_INFO_PROJECTION = new String[] {
        "_id",   //0
        "name",//1
        "telphone",//2
        "used",//3
        "date"//4
    };
    
    public static final class RecentCallsListItemViews {
        TextView line1View;
        TextView line2View;
        TextView labelView;
        TextView typeView;
        TextView dateView;
        TextView rownumView;
//        TextView callView;
        View callView;
        ImageView groupIndicator;
        TextView groupSize;
    }
    
    static final int ID_COLUMN_INDEX = 0;
    static final int NAME_COLUMN_INDEX = 1;
    static final int PHONENUMBER_COLUMN_INDEX = 2;
    static final int USED_COLUMN_INDEX = 3;
    static final int DATE_COLUMN_INDEX = 4;
    
    private Context mContext;
	private DBOperator mDbOperator;
    
	private OnClickListener addContactLister = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			ContactAddDialog(false);
		}
	};
	public Boolean spellCheck(String contectsname,String telnum) {
		
		if (contectsname == null || contectsname.equals("")) return false;
		if (telnum == null || telnum.equals("")) return false;
		
		return true;
	}
	private void ContactAddDialog(Boolean Bundle) {
		final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);	
		View alertDialogView = View.inflate(this, R.layout.contacts_add, null);
		final EditText contactname = (EditText) alertDialogView.findViewById(R.id.contactname);
		final EditText contactphonenum = (EditText) alertDialogView.findViewById(R.id.contactphonenum);
		if (Bundle) contactphonenum.setText(Bundle_telnum);
		alertDialog.setTitle(getResources().getString(R.string.renameConfirmation_title));
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {

						if (spellCheck(contactname.getText().toString().trim(),contactphonenum.getText().toString().trim())) {
							String name = contactname.getText().toString().trim();
							String telnum = contactphonenum.getText().toString().trim();
							if (mDbOperator.isContactsExist(telnum)) {
								Toast.makeText(getApplicationContext(), telnum + " 已存在", Toast.LENGTH_LONG).show();
								return;
							}
		             		mDbOperator.insertContacts(name,telnum);
//		             		startQuery();
		             		Toast.makeText(getApplicationContext(), "添加成功", Toast.LENGTH_LONG).show();
						} else {
							Toast.makeText(getApplicationContext(), "No ", Toast.LENGTH_LONG).show();
						}
						
					}
				});
		alertDialog.setNegativeButton(android.R.string.cancel, null);
		AlertDialog tempDialog = alertDialog.create();
		tempDialog.setView(alertDialogView, 0, 0, 0, 0);
		
		/** 3.WT6/5/3vHm<|EL **/
		tempDialog.setOnShowListener(new OnShowListener() {
			public void onShow(DialogInterface dialog) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.showSoftInput(contactname, InputMethodManager.SHOW_IMPLICIT);
			}
		});
		tempDialog.show();
	} 
	
    private final ContentObserver mContentObserver = new ContentObserver(new Handler()) {
    	
        @Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			startQuery();
		}
    };
    
    private TextView headTV;
	@Override
	    protected void onCreate(Bundle state) {
	        super.onCreate(state);

	        setContentView(R.layout.contacts_info);
	        mContext = getApplicationContext();
	        // Typing here goes to the dialer
	        setDefaultKeyMode(DEFAULT_KEYS_DIALER);
	        mAdapter = new RecentCallsAdapter();
	        mQueryHandler = new QueryHandler(this);
	        mDbOperator = new DBOperator(mContext);
	        mListView = (ListView)findViewById(R.id.list_view);
	        mListView.setAdapter(mAdapter);
	        mListView.setOnCreateContextMenuListener(this);
	        mAddContactButton = (Button)findViewById(R.id.add_contact);	       
	        mAddContactButton.setOnClickListener(addContactLister);
//	        mListView.addFooterView(LayoutInflater.from(mContext).inflate(R.layout.contacts_info_foot_botton, null));
	        setOverflowShowingAlways();
//	        getActionBar().setDisplayShowHomeEnabled(false);
	        
	        headTV = (TextView)findViewById(R.id.headTV);
//	        headTV.setText(getString(R.string.head_contact));
	        
			Intent intent = getIntent();
			Bundle_telnum = intent.getStringExtra(Const.BUNDLE_TEL_NUMBER);
	    }
	   ArrayList<BodyLine> mBodyArrays;
	   private String Bundle_telnum = null;
		private ListView mListView;
        private Button mAddContactButton;

	    protected void onStart() {
	        mScrollToTop = true;
	        super.onStart();
	    }

	    @Override
	protected void onResume() {
		startQuery();
		super.onResume();
		int count = mDbOperator.gettableCount(Const.TABLE_Contacts);
		headTV.setText(getString(R.string.head_contact) +Const.KEY_DELIMITER_ +count);
		mAdapter.mPreDrawListener = null; // Let it restart the thread after
											// next draw

		Uri AUTHORITY_URI = Uri.parse("content://" + Const.AUTHORITY + Const.KEY_DELIMITER_
				+ Const.TABLE_Contacts);
		mContext.getContentResolver().registerContentObserver(AUTHORITY_URI,
				true, mContentObserver);

		if (Bundle_telnum != null) {
			ContactAddDialog(true);
		}
	}
		
	    @Override
	    protected void onPause() {
	        super.onPause();
	        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
	        Bundle_telnum = null;
	    }

	    @Override
	    protected void onDestroy() {
	        super.onDestroy();
	        mAdapter.changeCursor(null);
	    }
	    
	    private Uri ContactsInfoUri = null;

	private void startQuery() {
		mAdapter.setLoading(true);

		// systime
		String strtime = Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss"); 

		// Cancel any pending queries
		mQueryHandler.cancelOperation(QUERY_TOKEN);

		ContactsInfoUri = mDbOperator.getTableUri(Const.TABLE_Contacts);
			mQueryHandler.startQuery(QUERY_TOKEN, null, ContactsInfoUri,
					CONTACT_INFO_PROJECTION, "used=?", new String[] {"1"},
					"date DESC");

	}
	   
	    private static final int MENU_ITEM_INTENT = 1;
	    private static final int MENU_ITEM_DELETE_ALL = 2;
	    
	    private static final int CONTEXT_MENU_ITEM_DELETE = 1;
	    private void setOverflowShowingAlways()
		{
			try
			{
				// true if a permanent menu key is present, false otherwise.
				ViewConfiguration config = ViewConfiguration.get(this);
				Field menuKeyField = ViewConfiguration.class
						.getDeclaredField("sHasPermanentMenuKey");
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			} catch (Exception e)
			{
				e.printStackTrace();
			}
		}
	    @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
//	        menu.add(0, MENU_ITEM_INTENT, 0, R.string.contextmenu_manager)
//	                .setIcon(R.drawable.ic_menu_managert);
//	        menu.add(0, MENU_ITEM_DELETE_ALL, 0, R.string.menu_viewContact)
//                    .setIcon(R.drawable.ic_menu_contact);
//	        menu.add(0, MENU_ITEM_DELETE_ALL, 0, R.string.recentCalls_deleteAll)
//            .setIcon(android.R.drawable.ic_menu_close_clear_cancel);
	    	
			getMenuInflater().inflate(R.menu.main, menu);
	        return true;
	    }
	    @Override
		public boolean onMenuOpened(int featureId, Menu menu)
		{
			if (featureId == Window.FEATURE_ACTION_BAR && menu != null)
			{
				if (menu.getClass().getSimpleName().equals("MenuBuilder"))
				{
					try
					{
						Method m = menu.getClass().getDeclaredMethod(
								"setOptionalIconsVisible", Boolean.TYPE);
						m.setAccessible(true);
						m.invoke(menu, true);
					} catch (Exception e)
					{
					}
				}
			}
			return super.onMenuOpened(featureId, menu);
		}
	    @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	            case MENU_ITEM_INTENT: 
//	            	Intent intent = new Intent(ContactsInfo.this,PupWinMageType.class);
//	            	startActivity(intent);
	                return true;
	            case MENU_ITEM_DELETE_ALL:
//	            	Intent intent2 = new Intent(ContactsInfo.this,ContactsAddActivity.class);
//	            	startActivity(intent2);
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

	        String name = cursor.getString(1);
	        String telphone = cursor.getString(2);
//	        
	        menu.setHeaderTitle(getString(R.string.contextmenu_title));
	        menu.setHeaderIcon(R.drawable.ic_dialog_icon);
	        Intent intent = new Intent();
	        intent.putExtra(Const.BUNDLE_TEL_NUMBER, telphone);
	        intent.putExtra(Const.BUNDLE_TOADY, "ALL");
	        intent.setClassName("com.android.gastove", "com.android.gastove.face.IndexRecordFrgmtActivity");
            menu.add(0, 0, 0, R.string.menu_ContactRecordin).setIntent(intent);
//            
            menu.add(0, CONTEXT_MENU_ITEM_DELETE, 0, R.string.recentCalls_removeFromRecentList);
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
	                    long id = cursor.getLong(PHONENUMBER_COLUMN_INDEX);
	                    sb.append(id);

	                mDbOperator.upContactsUsedUN(sb.toString());
	                return true;
	            }
	        }
			return super.onContextItemSelected(item);
	    }
	    
	   /*
	    * Adapter
	    */
	   private static final class QueryHandler extends AsyncQueryHandler {
	        private final WeakReference<ContactsInfo> mActivity;

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
	            mActivity = new WeakReference<ContactsInfo>(
	                    (ContactsInfo) context);
	        }

	        @Override
	        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
	            final ContactsInfo activity = mActivity.get();
	            if (activity != null && !activity.isFinishing()) {
	                final ContactsInfo.RecentCallsAdapter callsAdapter = activity.mAdapter;
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

//	        public void onClick(View view) {
//	            String number = (String) view.getTag();
//	            if (!TextUtils.isEmpty(number)) {
//	              // create recordid
//	  	          //number + systime(Date 131109170811)
//					long times = System.currentTimeMillis();
//					Date curdate = new Date(times);
//					SimpleDateFormat format = new SimpleDateFormat("yyMMddHHmmss");
//					String recordin = number + format.format(curdate);
//	            	
//	            	Intent intent = new Intent();
//	            	intent.setClass(ContactsInfo.this, RecordListActivity.class);
////	            	intent.putExtra(Const.RECORD_ID, recordin);
//	            	startActivity(intent);
//	            }else {
//	            	
//	            }
//	        }

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
	            super(ContactsInfo.this);
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
	            View view = inflater.inflate(R.layout.contacts_info_list_item_layout, parent, false);
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
//	            views.callView = (TextView)view.findViewById(R.id.call_icon);
//	            views.callView = view.findViewById(R.id.call_icon);
//	            views.callView.setOnClickListener(this);
	            view.setTag(views);
	        }

		public void bindView(Context context, View view, Cursor c) {
			final RecentCallsListItemViews views = (RecentCallsListItemViews) view.getTag();


			// 1
			String name = c.getString(NAME_COLUMN_INDEX);// 1
			views.line1View.setText(name);
			
			String telphone = c.getString(PHONENUMBER_COLUMN_INDEX);
			if (telphone.length() == 11) {
			    String tmptel = telphone.substring(0, 3) +"****"+ telphone.substring(7);
			    views.labelView.setText(tmptel);
			} else {
				views.labelView.setText(telphone);
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
