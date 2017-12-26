package com.android.gastove.settings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnShowListener;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gastove.R;
import com.android.gastove.provider.DBOperator;
import com.android.gastove.settings.sortlist.CharacterParser;
import com.android.gastove.settings.sortlist.SideBar;
import com.android.gastove.settings.sortlist.SideBar.OnTouchingLetterChangedListener;
import com.android.gastove.settings.sortlist.SortModel;
import com.android.gastove.ui.DtRecordInFrgmtActivity;
import com.android.gastove.util.Const;
import com.android.gastove.util.Utils;


/**
 * 
 */
public class ContactsInfoSort extends FragmentActivity {
	
	private View mBaseView;
	private ListView sortListView;
	private SideBar sideBar;
	private TextView dialog;
	private SortAdapter adapter;
	private ClearEditText mClearEditText;
	private Map<String, String> callRecords;

	private CharacterParser characterParser;
	private List<SortModel> SourceDateList;

	private PinyinComparator pinyinComparator;
	private DBOperator mDbOperator;
	private ImageView toaddContact;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.contacts_main);
//		mContext = getApplicationContext();
		mDbOperator = new DBOperator(this);
		ContactsInfoUri = mDbOperator.getTableUri(Const.TABLE_Contacts);
		initView();
		initData();
		sortListView.setOnCreateContextMenuListener(this);
	}

	private void initView() {
		sideBar = (SideBar) this.findViewById(R.id.sidrbar);
		dialog = (TextView) this.findViewById(R.id.dialog);

		sortListView = (ListView) this.findViewById(R.id.sortlist);

		toaddContact = (ImageView) this.findViewById(R.id.setting_to_contact);
		toaddContact.setOnClickListener(addContactLister);
	}

	private void initData() {
		characterParser = CharacterParser.getInstance();

		pinyinComparator = new PinyinComparator();

		sideBar.setTextView(dialog);

		sideBar.setOnTouchingLetterChangedListener(new OnTouchingLetterChangedListener() {

			@SuppressLint("NewApi")
			@Override
			public void onTouchingLetterChanged(String s) {
				int position = adapter.getPositionForSection(s.charAt(0));
				if (position != -1) {
					sortListView.setSelection(position);
				}
			}
		});

		sortListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Toast.makeText(getApplication(),
				// ((SortModel)adapter.getItem(position)).getName(),
				// Toast.LENGTH_SHORT).show();
				String number = callRecords.get(((SortModel) adapter
						.getItem(position)).getName());
				Toast.makeText(ContactsInfoSort.this, number, 0).show();
			}
		});

		new ConstactAsyncTask().execute(0);

	}
	public  Map<String,String> getAllCallRecords(Context context) {
		Map<String,String> temp = new HashMap<String, String>();
		try {

			Cursor c = context.getContentResolver().query(ContactsInfoUri,
					null, null, null, "name" + " COLLATE LOCALIZED ASC");
			if (c.moveToFirst()) {
				do {
					String contactId = c.getString(c
							.getColumnIndex(ContactsContract.Contacts._ID));
					String name = c.getString(c.getColumnIndex("name"));
					String number = c.getString(c.getColumnIndex("telphone"));
					temp.put(name, number);
				} while (c.moveToNext());
			}
			c.close();

		} catch (Exception e) {
		}
		return temp;
	}
	private Uri ContactsInfoUri = null;
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
    @Override
    protected void onPause() {
        super.onPause();
//        mContext.getContentResolver().unregisterContentObserver(mContentObserver);
    }
    
    @Override
    public void onCreateContextMenu(ContextMenu menu, View view, ContextMenuInfo menuInfoIn) {
        AdapterView.AdapterContextMenuInfo menuInfo;
        try {
             menuInfo = (AdapterView.AdapterContextMenuInfo) menuInfoIn;
        } catch (ClassCastException e) {
//            Log.e(TAG, "bad menuInfoIn", e);
            return;
        }
        String telphone = callRecords.get(((SortModel) adapter
				.getItem(menuInfo.position)).getName());
//        Cursor cursor = (Cursor) mAdapter.getItem(menuInfo.position);

//        String name = cursor.getString(1);
//        String telphone = cursor.getString(2);
//        
        menu.setHeaderTitle(getString(R.string.contextmenu_title));
        menu.setHeaderIcon(R.drawable.ic_dialog_icon);
        Intent intent = new Intent();
        intent.putExtra(Const.BUNDLE_TEL_NUMBER, telphone);
        intent.putExtra(Const.BUNDLE_TOADY, "ALL");
        intent.setClassName("com.android.gastove", "com.android.gastove.ui.IndexRecordFrgmtActivity");
        menu.add(0, 0, 0, R.string.menu_ContactRecordin).setIntent(intent);
//        
//        menu.add(0, CONTEXT_MENU_ITEM_DELETE, 0, R.string.recentCalls_removeFromRecentList);
        
		String recordid = null;
		String time = null;
		if (!TextUtils.isEmpty(telphone)) {
			time = Utils.systemFrmtTime("yyMMddHHmmss");
			recordid = telphone + time;
			Intent intentR = new Intent();
			intentR.setClassName("com.android.gastove",
					"com.android.gastove.ui.DtRecordInFrgmtActivity");
			intentR.putExtra(Const.BUNDLE_RECORD_ID, recordid);
			intentR.putExtra(Const.BUNDLE_TEL_NUMBER, telphone);
			intentR.putExtra(Const.BUNDLE_Gesture, true);
			mDbOperator.insertRecordToday(recordid, telphone);  //zhang  dan suo yin
			mDbOperator.insertRecordTodayIndex(telphone);
			menu.add(0, 0, 0, R.string.add_record).setIntent(intentR);
		}
    }
    
	private OnClickListener addContactLister = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(ContactsInfoSort.this, ContactsInfo.class);
			startActivity(intent);
		}
	};
	public Boolean spellCheck(String contectsname,String telnum) {
		
		if (contectsname == null || contectsname.equals("")) return false;
		if (telnum == null || telnum.equals("")) return false;
		
		return true;
	}
    
	private class ConstactAsyncTask extends AsyncTask<Integer, Integer, Integer> {

		@Override
		protected Integer doInBackground(Integer... arg0) {
			int result = -1;
			callRecords = getAllCallRecords(ContactsInfoSort.this);
			result = 1;
			return result;
		}
		@Override
		protected void onPostExecute(Integer result) {
			super.onPostExecute(result);
			if (result == 1) { //bug -- keyset
				List<String> constact = new ArrayList<String>();
				for (Iterator<String> keys = callRecords.keySet().iterator(); keys
						.hasNext();) {
					String key = keys.next();
					constact.add(key);
				}
				String[] names = new String[] {};
				names = constact.toArray(names);
				SourceDateList = filledData(names);

				// 根据a-z进行排序源数�?
				Collections.sort(SourceDateList, pinyinComparator);
				adapter = new SortAdapter(ContactsInfoSort.this, SourceDateList);
				sortListView.setAdapter(adapter);

				mClearEditText = (ClearEditText) ContactsInfoSort.this
						.findViewById(R.id.filter_edit);
				mClearEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
					
					@Override
					public void onFocusChange(View arg0, boolean arg1) {
						mClearEditText.setGravity(Gravity.LEFT|Gravity.CENTER_VERTICAL);
						
					}
				});
				// 根据输入框输入�?�的改变来过滤搜�?
				mClearEditText.addTextChangedListener(new TextWatcher() {

					@Override
					public void onTextChanged(CharSequence s, int start,
							int before, int count) {
						// 当输入框里面的�?�为空，更新为原来的列表，否则为过滤数据列表
						filterData(s.toString());
					}

					@Override
					public void beforeTextChanged(CharSequence s, int start,
							int count, int after) {

					}

					@Override
					public void afterTextChanged(Editable s) {
					}
				});
			}
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

	}

	/**
	 * 为ListView填充数据
	 * 
	 * @param date
	 * @return
	 */
	private List<SortModel> filledData(String[] date) {
		List<SortModel> mSortList = new ArrayList<SortModel>();

		for (int i = 0; i < date.length; i++) {
			SortModel sortModel = new SortModel();
			sortModel.setName(date[i]);
			// 汉字转换成拼�?
			String pinyin = characterParser.getSelling(date[i]);
			String sortString = pinyin.substring(0, 1).toUpperCase();

			// 正则表达式，判断首字母是否是英文字母
			if (sortString.matches("[A-Z]")) {
				sortModel.setSortLetters(sortString.toUpperCase());
			} else {
				sortModel.setSortLetters("#");
			}

			mSortList.add(sortModel);
		}
		return mSortList;

	}

	/**
	 * 根据输入框中的�?�来过滤数据并更新ListView
	 * 
	 * @param filterStr
	 */
	private void filterData(String filterStr) {
		List<SortModel> filterDateList = new ArrayList<SortModel>();

		if (TextUtils.isEmpty(filterStr)) {
			filterDateList = SourceDateList;
		} else {
			filterDateList.clear();
			for (SortModel sortModel : SourceDateList) {
				String name = sortModel.getName();
				if (name.indexOf(filterStr.toString()) != -1
						|| characterParser.getSelling(name).startsWith(
								filterStr.toString())) {
					filterDateList.add(sortModel);
				}
			}
		}

		// 根据a-z进行排序
		Collections.sort(filterDateList, pinyinComparator);
		adapter.updateListView(filterDateList);
	}

}
