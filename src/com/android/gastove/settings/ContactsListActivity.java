package com.android.gastove.settings;


import java.lang.ref.WeakReference;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.AsyncQueryHandler;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.CharArrayBuffer;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Contacts.ContactMethods;
import android.provider.Contacts.People;
import android.provider.Contacts.Phones;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Nickname;
import android.provider.ContactsContract.CommonDataKinds.Organization;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredPostal;
import android.provider.ContactsContract.Contacts;
import android.provider.ContactsContract.Contacts.AggregationSuggestions;
import android.provider.ContactsContract.Data;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.android.gastove.R;
import com.android.gastove.contacts.ContactInfoData;
import com.android.gastove.contacts.ContactListItemView;
import com.android.gastove.contacts.ContactsPreferences;
import com.android.gastove.contacts.ContactsSectionIndexer;
import com.android.gastove.contacts.MsgRunnable;
import com.android.gastove.contacts.PinnedHeaderListView;
import com.android.gastove.contacts.SearchEditText;
import com.android.gastove.contacts.TextHighlightingAnimation;
import com.android.gastove.contacts.TextHighlightingAnimation.TextWithHighlighting;
import com.android.gastove.util.Const;
import com.android.gastove.util.Utils;

public class ContactsListActivity extends ListActivity {
    private TextHighlightingAnimation mHighlightingAnimation;
    private static final int TEXT_HIGHLIGHTING_ANIMATION_DURATION = 350;
    private ContactItemListAdapter mAdapter;
    private int mPinnedHeaderBackgroundColor;
    private SearchEditText mSearchEditText;
    int mMode = MODE_DEFAULT;
    private boolean mSearchMode;
    private boolean mDisplayOnlyPhones;
    /** Run a search query in a PICK_PHONE mode */
    private static final int QUERY_TOKEN = 42;
    
    private String getTextFilter() {
        if (mSearchEditText != null) {
            return mSearchEditText.getText().toString();
        }
        return null;
    }
    private void hideSoftKeyboard() {
        // Hide soft keyboard, if visible
        InputMethodManager inputMethodManager = (InputMethodManager)
                getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.hideSoftInputFromWindow(mList.getWindowToken(), 0);
    }
    
    public boolean onKeyPreIme(int keyCode, KeyEvent event) {
        if (mSearchMode && keyCode == KeyEvent.KEYCODE_BACK && TextUtils.isEmpty(getTextFilter())) {
            hideSoftKeyboard();
            onBackPressed();
            return true;
        }
        return false;
    }
    
    private static final int MENU_ITEM_MANAGER = 2;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(0, MENU_ITEM_DELETE_ALL, 0, R.string.recentCalls_deleteAll)
//                .setIcon(android.R.drawable.ic_menu_close_clear_cancel);
//        menu.add(0, MENU_ITEM_MANAGER, 0, R.string.recentCalls_menu_sendcontact)
//        .setIcon(R.drawable.ic_menu_sendcon);
        return true;
    }
    private ProgressDialog mProgressDialog;
    private Handler handler = new Handler(){
    	  
        @Override  
        public void handleMessage(Message msg) {  
            //关闭ProgressDialog  
        	mProgressDialog.dismiss();  
        }}; 
        
//     
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case MENU_ITEM_MANAGER : {
//            	
//            	Toast.makeText(getApplicationContext(), "发送联系人", Toast.LENGTH_LONG).show();
//            	mArrayList = mAdapter.getContactInfo();
//                
//            	mProgressDialog = ProgressDialog.show(ContactsListActivity.this, "Loading...", "Please wait...", true, false);
//                new Thread(new MsgRunnable(mArrayList,mProgressDialog,context)).start();
//            	return true;
//            }
//        }
//        return super.onOptionsItemSelected(item);
//    }
    private String queryPhoneNumbers(long contactId) {
        Uri baseUri = ContentUris.withAppendedId(Contacts.CONTENT_URI, contactId);
        Uri dataUri = Uri.withAppendedPath(baseUri, Contacts.Data.CONTENT_DIRECTORY);

        Cursor c = getContentResolver().query(dataUri,
                new String[] {Phone._ID, Phone.NUMBER, Phone.IS_SUPER_PRIMARY},
                Data.MIMETYPE + "=?", new String[] {Phone.CONTENT_ITEM_TYPE}, null);
        if (c != null) {
            if (c.moveToFirst()) {
                return c.getString(1);
            }
            c.close();
        }
        return null;
    }
    private void setEmptyText() {
//        if (mMode == MODE_JOIN_CONTACT || mSearchMode) {
//            return;
//        }

        TextView empty = (TextView) findViewById(R.id.emptyText);
        if (mDisplayOnlyPhones) {
            empty.setText(getText(R.string.noContactsWithPhoneNumbers));
        }
    }
    private Uri getContactFilterUri(String filter) {
        Uri baseUri;
        if (!TextUtils.isEmpty(filter)) {
            baseUri = Uri.withAppendedPath(Contacts.CONTENT_FILTER_URI, Uri.encode(filter));
        } else {
            baseUri = Contacts.CONTENT_URI;
        }

        if (mAdapter.getDisplaySectionHeadersEnabled()) {
            return buildSectionIndexerUri(baseUri);
        } else {
            return baseUri;
        }
    }
    private QueryHandler mQueryHandler;
    private long mQueryAggregateId;
    static final String[] CONTACTS_SUMMARY_PROJECTION = new String[] {
        Contacts._ID,                       // 0
        "display_name"//Contacts.DISPLAY_NAME_PRIMARY,      // 1
//        "display_name_alt",//Contacts.DISPLAY_NAME_ALTERNATIVE,  // 2
//        "sort_key",//Contacts.SORT_KEY_PRIMARY,          // 3
//        Contacts.STARRED,                   // 4
//        Contacts.TIMES_CONTACTED,           // 5
//        Contacts.CONTACT_PRESENCE,          // 6
//        Contacts.PHOTO_ID,                  // 7
//        Contacts.LOOKUP_KEY,                // 8
//        "phonetic_name",//Contacts.PHONETIC_NAME,             // 9
//        Contacts.HAS_PHONE_NUMBER,          // 10
    };
    private int mSortOrder;
    private String getSortOrder(String[] projectionType) {
        if (mSortOrder == 1) {// ContactsContract.Preferences.SORT_ORDER_PRIMARY) {
            return "sort_key";// Contacts.SORT_KEY_PRIMARY;
        } else {
            return "sort_key_alt";//Contacts.SORT_KEY_ALTERNATIVE;
        }
    }
    
    private Cursor getShowAllContactsLabelCursor(String[] projection) {
        MatrixCursor matrixCursor = new MatrixCursor(projection);
        Object[] row = new Object[projection.length];
        // The only columns we care about is the id
//        row[SUMMARY_ID_COLUMN_INDEX] = JOIN_MODE_SHOW_ALL_CONTACTS_ID;
        row[0] = -2;
        matrixCursor.addRow(row);
        return matrixCursor;
    }
//    private static Cursor mCursor = null;
    private static class QueryHandler extends AsyncQueryHandler {
        protected final WeakReference<ContactsListActivity> mActivity;
        protected boolean mLoadingJoinSuggestions = false;

        public QueryHandler(Context context) {
            super(context.getContentResolver());
            mActivity = new WeakReference<ContactsListActivity>((ContactsListActivity) context);
        }

        public void setLoadingJoinSuggestions(boolean flag) {
            mLoadingJoinSuggestions = flag;
        }

        @Override
        protected void onQueryComplete(int token, Object cookie, Cursor cursor) {
            final ContactsListActivity activity = mActivity.get();
            if (activity != null && !activity.isFinishing()) {

                // Whenever we get a suggestions cursor, we need to immediately kick off
                // another query for the complete list of contacts
                if (cursor != null && mLoadingJoinSuggestions) {
                    mLoadingJoinSuggestions = false;
                    if (cursor.getCount() > 0) {
                        activity.mAdapter.setSuggestionsCursor(cursor);
                    } else {
                        cursor.close();
                        activity.mAdapter.setSuggestionsCursor(null);
                    }

                    if (activity.mAdapter.mSuggestionsCursorCount == 0 ) {
                           // || !activity.mJoinModeShowAllContacts) {
                        startQuery(QUERY_TOKEN, null, 
                        		        activity.getContactFilterUri(activity.getTextFilter()),
                        		        CONTACTS_SUMMARY_PROJECTION,
                                Contacts._ID + " != " + activity.mQueryAggregateId
                                        + " AND " + "in_visible_group =1", //CLAUSE_ONLY_VISIBLE,
                                        null,
                                activity.getSortOrder(CONTACTS_SUMMARY_PROJECTION));
                        return;
                    }

                    cursor = activity.getShowAllContactsLabelCursor(CONTACTS_SUMMARY_PROJECTION);
                }

                activity.mAdapter.changeCursor(cursor);
//                mCursor = cursor;
                // Now that the cursor is populated again, it's possible to restore the list state
//                if (activity.mListState != null) {
//                    activity.mList.onRestoreInstanceState(activity.mListState);
//                    activity.mListState = null;
//                }
            } else {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }
    private static final Uri CONTACTS_CONTENT_URI_WITH_LETTER_COUNTS =
            buildSectionIndexerUri(Contacts.CONTENT_URI);
    private static Uri buildSectionIndexerUri(Uri uri) {
        return uri.buildUpon()
                .appendQueryParameter("address_book_index_extras",//ContactCounts.ADDRESS_BOOK_INDEX_EXTRAS,
                		"true").build();
    }
    private int mDisplayOrder;
    private ContactsPreferences mContactsPrefs;
    private boolean mHighlightWhenScrolling;
    static final String[] CONTACTS_SUMMARY_FILTER_PROJECTION =new String[] {
        Contacts._ID,                       // 0
        "display_name",//Contacts.DISPLAY_NAME_PRIMARY,      // 1
        "display_name_alt",//Contacts.DISPLAY_NAME_ALTERNATIVE,  // 2
        "sort_key",//Contacts.SORT_KEY_PRIMARY,          // 3
        Contacts.STARRED,                   // 4
        Contacts.TIMES_CONTACTED,           // 5
        Contacts.CONTACT_PRESENCE,          // 6
        Contacts.PHOTO_ID,                  // 7
        Contacts.LOOKUP_KEY,                // 8
        "phonetic_name",//Contacts.PHONETIC_NAME,             // 9
        Contacts.HAS_PHONE_NUMBER,          // 10
        "snippet_mimetype",//SearchSnippetColumns.SNIPPET_MIMETYPE, // 11
        "snippet_data1",//SearchSnippetColumns.SNIPPET_DATA1,     // 12
        "snippet_data4",//SearchSnippetColumns.SNIPPET_DATA4,     // 13
    };
    
    
    void startQuery() {
        // Set the proper empty string
        setEmptyText();

        mAdapter.setLoading(true);

        // Cancel any pending queries
        mQueryHandler.cancelOperation(QUERY_TOKEN);
        mQueryHandler.setLoadingJoinSuggestions(false);

        mSortOrder = mContactsPrefs.getSortOrder();
        mDisplayOrder = mContactsPrefs.getDisplayOrder();

        // When sort order and display order contradict each other, we want to
        // highlight the part of the name used for sorting.
        mHighlightWhenScrolling = false;
        if (mSortOrder == 1 &&//ContactsContract.Preferences.SORT_ORDER_PRIMARY &&
                mDisplayOrder == 2 ){//ContactsContract.Preferences.DISPLAY_ORDER_ALTERNATIVE) {
            mHighlightWhenScrolling = true;
        } else if (mSortOrder == 2 && // ContactsContract.Preferences.SORT_ORDER_ALTERNATIVE &&
                mDisplayOrder == 1 ) { //ContactsContract.Preferences.DISPLAY_ORDER_PRIMARY) {
            mHighlightWhenScrolling = true;
        }
        String[] projection = getProjectionForQuery();
        if (mSearchMode && TextUtils.isEmpty(getTextFilter())) {
            mAdapter.changeCursor(new MatrixCursor(projection));
            return;
        }

        String callingPackage = getCallingPackage();
        
        Uri uri = CONTACTS_CONTENT_URI_WITH_LETTER_COUNTS;//getUriToQuery();
        
        if (!TextUtils.isEmpty(callingPackage)) {
            uri = uri.buildUpon()
                    .appendQueryParameter("requesting_package",//ContactsContract.REQUESTING_PACKAGE_PARAM_KEY,
                            callingPackage)
                    .build();
        }

        // Kick off the new query
        switch (mMode) {
            case MODE_DEFAULT:
            	default :
                mQueryHandler.startQuery(QUERY_TOKEN, null, Contacts.CONTENT_URI, projection, null,
                        null, getSortOrder(projection));
                break;
        }//getContactSelection()
    }
    
    String[] getProjectionForQuery() {
        switch(mMode) {
            case MODE_JOIN_CONTACT:
            case MODE_STREQUENT:
            case MODE_FREQUENT:
            case MODE_STARRED:
            case MODE_DEFAULT:
            case MODE_CUSTOM:
            case MODE_INSERT_OR_EDIT_CONTACT:
            case MODE_GROUP:
            case MODE_PICK_CONTACT:
            case MODE_PICK_OR_CREATE_CONTACT: {
                return mSearchMode
                        ? CONTACTS_SUMMARY_FILTER_PROJECTION
                        : CONTACTS_SUMMARY_PROJECTION;
            }
            case MODE_QUERY:
            case MODE_QUERY_PICK:
            case MODE_QUERY_PICK_TO_EDIT: {
                return CONTACTS_SUMMARY_FILTER_PROJECTION;
            }
        }

        // Default to normal aggregate projection
        return CONTACTS_SUMMARY_PROJECTION;
    }
    /**
     * Return the selection arguments for a default query based on the
     * {@link #mDisplayOnlyPhones} flag.
     */
    private String getContactSelection() {
        if (mDisplayOnlyPhones) {
            return CLAUSE_ONLY_VISIBLE + " AND " + CLAUSE_ONLY_PHONES;
        } else {
            return CLAUSE_ONLY_VISIBLE;
        }
    }
    
    private static final String CLAUSE_ONLY_VISIBLE = Contacts.IN_VISIBLE_GROUP + "=1";
    private static final String CLAUSE_ONLY_PHONES = Contacts.HAS_PHONE_NUMBER + "=1";
    
    Cursor doFilter(String filter) {
        String[] projection = getProjectionForQuery();
        if (mSearchMode && TextUtils.isEmpty(getTextFilter())) {
            return new MatrixCursor(projection);
        }

        final ContentResolver resolver = getContentResolver();
        switch (mMode) {
            case MODE_DEFAULT:
            case MODE_CUSTOM:
            case MODE_PICK_CONTACT:
            case MODE_PICK_OR_CREATE_CONTACT:
            case MODE_INSERT_OR_EDIT_CONTACT: {
                return resolver.query(getContactFilterUri(filter), projection,
                        getContactSelection(), null, getSortOrder(projection));
            }

            case MODE_STARRED: {
                return resolver.query(getContactFilterUri(filter), projection,
                        Contacts.STARRED + "=1", null,
                        getSortOrder(projection));
            }

            case MODE_FREQUENT: {
                return resolver.query(getContactFilterUri(filter), projection,
                        Contacts.TIMES_CONTACTED + " > 0", null,
                        Contacts.TIMES_CONTACTED + " DESC, "
                        + getSortOrder(projection));
            }

            case MODE_STREQUENT: {
                Uri uri;
                if (!TextUtils.isEmpty(filter)) {
                    uri = Uri.withAppendedPath(Contacts.CONTENT_STREQUENT_FILTER_URI,
                            Uri.encode(filter));
                } else {
                    uri = Contacts.CONTENT_STREQUENT_URI;
                }
                return resolver.query(uri, projection, null, null, null);
            }

            case MODE_PICK_PHONE: {
                Uri uri = getUriToQuery();
                if (!TextUtils.isEmpty(filter)) {
                    uri = Uri.withAppendedPath(Phone.CONTENT_FILTER_URI, Uri.encode(filter));
                }
                return resolver.query(uri, projection, CLAUSE_ONLY_VISIBLE, null,
                        getSortOrder(projection));
            }

            case MODE_LEGACY_PICK_PHONE: {
                //TODO: Support filtering here (bug 2092503)
                break;
            }

            case MODE_JOIN_CONTACT: {

                // We are on a background thread. Run queries one after the other synchronously
                Cursor cursor = resolver.query(getJoinSuggestionsUri(filter), projection, null,
                        null, null);
                mAdapter.setSuggestionsCursor(cursor);
                mJoinModeShowAllContacts = false;
                return resolver.query(getContactFilterUri(filter), projection,
                        Contacts._ID + " != " + mQueryAggregateId + " AND " + CLAUSE_ONLY_VISIBLE,
                        null, getSortOrder(projection));
            }
        }
        throw new UnsupportedOperationException("filtering not allowed in mode " + mMode);
    }
    
    private Uri getJoinSuggestionsUri(String filter) {
        Builder builder = Contacts.CONTENT_URI.buildUpon();
        builder.appendEncodedPath(String.valueOf(mQueryAggregateId));
        builder.appendEncodedPath(AggregationSuggestions.CONTENT_DIRECTORY);
        if (!TextUtils.isEmpty(filter)) {
            builder.appendEncodedPath(Uri.encode(filter));
        }
        builder.appendQueryParameter("limit", String.valueOf(4));
        return builder.build();
    }
    
    private Uri getUriToQuery() {
        switch(mMode) {

            case MODE_JOIN_CONTACT:
                return getJoinSuggestionsUri(null);
            case MODE_FREQUENT:
            case MODE_STARRED:
                return Contacts.CONTENT_URI;

            case MODE_DEFAULT:
            case MODE_CUSTOM:
            case MODE_INSERT_OR_EDIT_CONTACT:
            case MODE_PICK_CONTACT:
            case MODE_PICK_OR_CREATE_CONTACT:{
                return CONTACTS_CONTENT_URI_WITH_LETTER_COUNTS;
            }
            case MODE_STREQUENT: {
                return Contacts.CONTENT_STREQUENT_URI;
            }
            case MODE_LEGACY_PICK_PERSON:
            case MODE_LEGACY_PICK_OR_CREATE_PERSON: {
                return People.CONTENT_URI;
            }
            case MODE_PICK_PHONE: {
                return buildSectionIndexerUri(Phone.CONTENT_URI);
            }
            case MODE_LEGACY_PICK_PHONE: {
                return Phones.CONTENT_URI;
            }
            case MODE_PICK_POSTAL: {
                return buildSectionIndexerUri(StructuredPostal.CONTENT_URI);
            }
            case MODE_LEGACY_PICK_POSTAL: {
                return ContactMethods.CONTENT_URI;
            }
            case MODE_QUERY:
            case MODE_QUERY_PICK:
            case MODE_QUERY_PICK_TO_EDIT: {
                return getContactFilterUri(mInitialFilter);
            }
            case MODE_QUERY_PICK_PHONE: {
                return Uri.withAppendedPath(Phone.CONTENT_FILTER_URI,
                        Uri.encode(mInitialFilter));
            }
            default: {
                throw new IllegalStateException("Can't generate URI: Unsupported Mode.");
            }
        }
    }
    
    private String mInitialFilter;
    
    final static class ContactListItemCache {
        public CharArrayBuffer nameBuffer = new CharArrayBuffer(128);
        public CharArrayBuffer dataBuffer = new CharArrayBuffer(128);
        public CharArrayBuffer highlightedTextBuffer = new CharArrayBuffer(128);
        public TextWithHighlighting textWithHighlighting;
        public CharArrayBuffer phoneticNameBuffer = new CharArrayBuffer(128);
    }
    
    private ArrayList<ContactInfoData> mArrayList = new ArrayList<ContactInfoData>();
    
    private final class ContactItemListAdapter extends CursorAdapter implements
			SectionIndexer, OnScrollListener,PinnedHeaderListView.PinnedHeaderAdapter {
    	
    	
		private SectionIndexer mIndexer;
		private boolean mLoading = true;
		private CharSequence mUnknownNameText;
		private boolean mDisplayPhotos = false;
		private boolean mDisplayCallButton = false;
		private boolean mDisplayAdditionalData = true;
		private int mFrequentSeparatorPos = ListView.INVALID_POSITION;
		private boolean mDisplaySectionHeaders = true;
		private Cursor mSuggestionsCursor;
		private int mSuggestionsCursorCount;
        private Cursor mCursor;
        private Context mContext;
        
        
		public ContactItemListAdapter(Context context) {
			super(context, null, false);
            mContext = context;
			mUnknownNameText = context.getText(android.R.string.unknownName);
//			switch (mMode) {
//			case MODE_LEGACY_PICK_POSTAL:
//			case MODE_PICK_POSTAL:
//			case MODE_LEGACY_PICK_PHONE:
//			case MODE_PICK_PHONE:
//			case MODE_STREQUENT:
//			case MODE_FREQUENT:
//				mDisplaySectionHeaders = false;
//				break;
//			}
//
			if (mSearchMode) {
				mDisplaySectionHeaders = false;
			}

			// Do not display the second line of text if in a specific SEARCH
			// query mode, usually for
			// matching a specific E-mail or phone number. Any contact details
			// shown would be identical, and columns might not even be present
			// in the returned cursor.
//			if (mMode != MODE_QUERY_PICK_PHONE && mQueryMode != QUERY_MODE_NONE) {
				mDisplayAdditionalData = false;
//			}

//			if ((mMode & MODE_MASK_NO_DATA) == MODE_MASK_NO_DATA) {
				mDisplayAdditionalData = false;
//			}

//			if ((mMode & MODE_MASK_SHOW_CALL_BUTTON) == MODE_MASK_SHOW_CALL_BUTTON) {
				mDisplayCallButton = true;
//			}

//			if ((mMode & MODE_MASK_SHOW_PHOTOS) == MODE_MASK_SHOW_PHOTOS) {
				mDisplayPhotos = true;
//			}
		}

		public boolean getDisplaySectionHeadersEnabled() {
			return mDisplaySectionHeaders;
		}

		public void setSuggestionsCursor(Cursor cursor) {
			if (mSuggestionsCursor != null) {
				mSuggestionsCursor.close();
			}
			mSuggestionsCursor = cursor;
			mSuggestionsCursorCount = cursor == null ? 0 : cursor.getCount();
		}

		/**
		 * Callback on the UI thread when the content observer on the backing
		 * cursor fires. Instead of calling requery we need to do an async query
		 * so that the requery doesn't block the UI thread for a long time.
		 */
		@Override
		protected void onContentChanged() {
			CharSequence constraint = getTextFilter();
			if (!TextUtils.isEmpty(constraint)) {
				// Reset the filter state then start an async filter operation
				Filter filter = getFilter();
				filter.filter(constraint);
			} else {
				// Start an async query
				startQuery();
			}
		}

		public void setLoading(boolean loading) {
			mLoading = loading;
		}
		
	    private int mProviderStatus = 0;
		@Override
		public boolean isEmpty() {
			if (mProviderStatus != 0 ) { //ProviderStatus.STATUS_NORMAL) {
				return true;
			}

			if (mSearchMode) {
				return TextUtils.isEmpty(getTextFilter());
			} else if ((mMode & MODE_MASK_CREATE_NEW) == MODE_MASK_CREATE_NEW) {
				// This mode mask adds a header and we always want it to show
				// up, even
				// if the list is empty, so always claim the list is not empty.
				return false;
			} else {
				if (mCursor == null || mLoading) {
					// We don't want the empty state to show when loading.
					return false;
				} else {
					return super.isEmpty();
				}
			}
		}

		@Override
		public int getItemViewType(int position) {
			if (position == 0
					&& (mShowNumberOfContacts || (mMode & MODE_MASK_CREATE_NEW) != 0)) {
				return IGNORE_ITEM_VIEW_TYPE;
			}

			if (isShowAllContactsItemPosition(position)) {
				return IGNORE_ITEM_VIEW_TYPE;
			}

			if (isSearchAllContactsItemPosition(position)) {
				return IGNORE_ITEM_VIEW_TYPE;
			}

			if (getSeparatorId(position) != 0) {
				// We don't want the separator view to be recycled.
				return IGNORE_ITEM_VIEW_TYPE;
			}

			return super.getItemViewType(position);
		}

		public ArrayList<ContactInfoData> getContactInfo() {
			 ArrayList<ContactInfoData> tmpArrayList = new ArrayList<ContactInfoData>();
			 int count  = mCursor.getCount();
	        	for ( int i = 0; i< count ; i++) {
	        		mCursor.moveToPosition(i);

	        		String telphone = queryPhoneNumbers(mCursor.getLong(0));
	    			ContactInfoData data = new ContactInfoData();
	    			data.setData(0, mCursor.getString(1));
	    			data.setData(1, telphone);
	        		Log.d("qqqqqqqqqqq", "msg"+ i+"     " + mCursor.getString(1));
	        		Log.d("qqqqqqqqqqq", "msg"+ i+"     " + telphone);
	        		tmpArrayList.add(data);
	        	}
	        	return tmpArrayList;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
//			if (!mDataValid) {
//				throw new IllegalStateException(
//						"this should only be called when the cursor is valid");
//			}

			// handle the total contacts item
			if (position == 0 && mShowNumberOfContacts) {
				return getTotalContactCountView(parent);
			}

//			if (position == 0 && (mMode & MODE_MASK_CREATE_NEW) != 0) {
//				// Add the header for creating a new contact
//				return getLayoutInflater().inflate(R.layout.create_new_contact,
//						parent, false);
//			}

			if (isShowAllContactsItemPosition(position)) {
				return getLayoutInflater().inflate(
						R.layout.contacts_list_show_all_item, parent, false);
			}

			if (isSearchAllContactsItemPosition(position)) {
				return getLayoutInflater().inflate(
						R.layout.contacts_list_search_all_item, parent, false);
			}

			// Handle the separator specially
			int separatorId = getSeparatorId(position);
			if (separatorId != 0) {
				TextView view = (TextView) getLayoutInflater().inflate(
						R.layout.list_separator, parent, false);
				view.setText(separatorId);
				return view;
			}

			boolean showingSuggestion;
			Cursor cursor;
			if (mSuggestionsCursorCount != 0
					&& position < mSuggestionsCursorCount + 2) {
				showingSuggestion = true;
				cursor = mSuggestionsCursor;
			} else {
				showingSuggestion = false;
				cursor = mCursor;
			}
			
			int realPosition = getRealPosition(position);
			Log.d("11111111",""+ realPosition);
			if (!cursor.moveToPosition(realPosition)) {
				throw new IllegalStateException(
						"couldn't move cursor to position " + position);
			}

			boolean newView;
			View v;
			if (convertView == null || convertView.getTag() == null) {
				newView = true;
				v = newView(mContext, cursor, parent);
			} else {
				newView = false;
				v = convertView;
			}
			bindView(v, mContext, cursor);
			bindSectionHeader(v, realPosition, mDisplaySectionHeaders
					&& !showingSuggestion);
			return v;
		}

		private View getTotalContactCountView(ViewGroup parent) {
			final LayoutInflater inflater = getLayoutInflater();
			View view = inflater
					.inflate(R.layout.total_contacts, parent, false);

			TextView totalContacts = (TextView) view
					.findViewById(R.id.totalContactsText);

			String text;
			int count = getRealCount();

			if (mSearchMode && !TextUtils.isEmpty(getTextFilter())) {
				text = getQuantityText(count,
						R.string.listFoundAllContactsZero,
						R.plurals.searchFoundContacts);
			} else {
				if (mDisplayOnlyPhones) {
					text = getQuantityText(count,
							R.string.listTotalPhoneContactsZero,
							R.plurals.listTotalPhoneContacts);
				} else {
					text = getQuantityText(count,
							R.string.listTotalAllContactsZero,
							R.plurals.listTotalAllContacts);
				}
			}
			totalContacts.setText(text);
			return view;
		}

		private boolean isShowAllContactsItemPosition(int position) {
			return mMode == MODE_JOIN_CONTACT && mJoinModeShowAllContacts
					&& mSuggestionsCursorCount != 0
					&& position == mSuggestionsCursorCount + 2;
		}

		private boolean isSearchAllContactsItemPosition(int position) {
			return mSearchMode && position == getCount() - 1;
		}

		private int getSeparatorId(int position) {
			int separatorId = 0;
//			if (position == mFrequentSeparatorPos) {
//				separatorId = R.string.favoritesFrquentSeparator;
//			}
			if (mSuggestionsCursorCount != 0) {
				if (position == 0) {
					separatorId = R.string.separatorJoinAggregateSuggestions;
				} else if (position == mSuggestionsCursorCount + 1) {
					separatorId = R.string.separatorJoinAggregateAll;
				}
			}
			return separatorId;
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			final ContactListItemView view = new ContactListItemView(context,
					null);
//			view.setOnCallButtonClickListener(ContactsListActivity.this);
			view.setTag(new ContactListItemCache());
			return view;
		}
		

		@Override
		public void bindView(View itemView, Context context, Cursor cursor) {
			final ContactListItemView view = (ContactListItemView) itemView;
			final ContactListItemCache cache = (ContactListItemCache) view
					.getTag();

			int typeColumnIndex;
			int dataColumnIndex;
			int labelColumnIndex;
			int defaultType;
			int nameColumnIndex;
			int phoneticNameColumnIndex;
			boolean displayAdditionalData = mDisplayAdditionalData;
			boolean highlightingEnabled = false;
			switch (mMode) {
//			case MODE_PICK_PHONE:
//			case MODE_LEGACY_PICK_PHONE:
			case MODE_QUERY_PICK_PHONE: {
				nameColumnIndex = PHONE_DISPLAY_NAME_COLUMN_INDEX;
				phoneticNameColumnIndex = -1;
				dataColumnIndex = PHONE_NUMBER_COLUMN_INDEX;
				typeColumnIndex = PHONE_TYPE_COLUMN_INDEX;
				labelColumnIndex = PHONE_LABEL_COLUMN_INDEX;
				defaultType = Phone.TYPE_HOME;
				break;
			}
//			case MODE_PICK_POSTAL:
			case MODE_LEGACY_PICK_POSTAL: {
				nameColumnIndex = POSTAL_DISPLAY_NAME_COLUMN_INDEX;
				phoneticNameColumnIndex = -1;
				dataColumnIndex = POSTAL_ADDRESS_COLUMN_INDEX;
				typeColumnIndex = POSTAL_TYPE_COLUMN_INDEX;
				labelColumnIndex = POSTAL_LABEL_COLUMN_INDEX;
				defaultType = StructuredPostal.TYPE_HOME;
				break;
			}
			default: {
				nameColumnIndex = getSummaryDisplayNameColumnIndex();
				if (mMode == MODE_LEGACY_PICK_PERSON
						|| mMode == MODE_LEGACY_PICK_OR_CREATE_PERSON) {
					phoneticNameColumnIndex = -1;
				} else {
					phoneticNameColumnIndex = SUMMARY_PHONETIC_NAME_COLUMN_INDEX;
				}
				dataColumnIndex = -1;
				typeColumnIndex = -1;
				labelColumnIndex = -1;
				defaultType = Phone.TYPE_HOME;
				displayAdditionalData = false;
				highlightingEnabled = mHighlightWhenScrolling
						&& mMode != MODE_STREQUENT;
			}
			}

			
			// Set the name
			cursor.copyStringToBuffer(nameColumnIndex, cache.nameBuffer);
			//get telphone
//			String telphone = queryPhoneNumbers(cursor.getLong(0));
//			ContactInfoData data = new ContactInfoData();
//			data.setData(0, cursor.getString(1));
//			data.setData(1, telphone);
//			mArrayList.add(data);
			
			TextView nameView = view.getNameTextView();
			int size = cache.nameBuffer.sizeCopied;
			if (size != 0) {
				if (highlightingEnabled) {
					if (cache.textWithHighlighting == null) {
						cache.textWithHighlighting = mHighlightingAnimation
								.createTextWithHighlighting();
					}
					buildDisplayNameWithHighlighting(nameView, cursor,
							cache.nameBuffer, cache.highlightedTextBuffer,
							cache.textWithHighlighting);
				} else {
					nameView.setText(cache.nameBuffer.data, 0, size);
				}
			} else {
				nameView.setText(mUnknownNameText);
			}

			boolean hasPhone = cursor.getColumnCount() >= SUMMARY_HAS_PHONE_COLUMN_INDEX
					&& cursor.getInt(SUMMARY_HAS_PHONE_COLUMN_INDEX) != 0;

			// Make the call button visible if requested.
			if (mDisplayCallButton && hasPhone) {
				int pos = cursor.getPosition();
				view.showCallButton(android.R.id.button1, pos);
			} else {
				view.hideCallButton();
			}

			// Set the photo, if requested
			if (mDisplayPhotos) {
				boolean useQuickContact = (mMode & MODE_MASK_DISABLE_QUIKCCONTACT) == 0;

				long photoId = 0;
				if (!cursor.isNull(SUMMARY_PHOTO_ID_COLUMN_INDEX)) {
					photoId = cursor.getLong(SUMMARY_PHOTO_ID_COLUMN_INDEX);
				}

				ImageView viewToUse;
//				if (useQuickContact) {
//					// Build soft lookup reference
//					final long contactId = cursor
//							.getLong(SUMMARY_ID_COLUMN_INDEX);
//					final String lookupKey = cursor
//							.getString(SUMMARY_LOOKUP_KEY_COLUMN_INDEX);
//					QuickContactBadge quickContact = view.getQuickContact();
//					quickContact.assignContactUri(Contacts.getLookupUri(
//							contactId, lookupKey));
////					quickContact.setSelectedContactsAppTabIndex(StickyTabs
////							.getTab(getIntent()));
//					viewToUse = quickContact;
//				} else {
					viewToUse = view.getPhotoView();
//				}

				final int position = cursor.getPosition();
//				mPhotoLoader.loadPhoto(viewToUse, photoId);
			}

//			if ((mMode & MODE_MASK_NO_PRESENCE) == 0) {
//				// Set the proper icon (star or presence or nothing)
//				int serverStatus;
//				if (!cursor.isNull(SUMMARY_PRESENCE_STATUS_COLUMN_INDEX)) {
//					serverStatus = cursor
//							.getInt(SUMMARY_PRESENCE_STATUS_COLUMN_INDEX);
//					Drawable icon = ContactPresenceIconUtil.getPresenceIcon(
//							mContext, serverStatus);
//					if (icon != null) {
//						view.setPresence(icon);
//					} else {
//						view.setPresence(null);
//					}
//				} else {
//					view.setPresence(null);
//				}
//			} else {
				view.setPresence(null);
//			}

			if (mShowSearchSnippets) {
				boolean showSnippet = false;
				String snippetMimeType = cursor
						.getString(SUMMARY_SNIPPET_MIMETYPE_COLUMN_INDEX);
				if (Email.CONTENT_ITEM_TYPE.equals(snippetMimeType)) {
					String email = cursor
							.getString(SUMMARY_SNIPPET_DATA1_COLUMN_INDEX);
					if (!TextUtils.isEmpty(email)) {
						view.setSnippet(email);
						showSnippet = true;
					}
				} else if (Organization.CONTENT_ITEM_TYPE
						.equals(snippetMimeType)) {
					String company = cursor
							.getString(SUMMARY_SNIPPET_DATA1_COLUMN_INDEX);
					String title = cursor
							.getString(SUMMARY_SNIPPET_DATA4_COLUMN_INDEX);
					if (!TextUtils.isEmpty(company)) {
						if (!TextUtils.isEmpty(title)) {
							view.setSnippet(company + " / " + title);
						} else {
							view.setSnippet(company);
						}
						showSnippet = true;
					} else if (!TextUtils.isEmpty(title)) {
						view.setSnippet(title);
						showSnippet = true;
					}
				} else if (Nickname.CONTENT_ITEM_TYPE.equals(snippetMimeType)) {
					String nickname = cursor
							.getString(SUMMARY_SNIPPET_DATA1_COLUMN_INDEX);
					if (!TextUtils.isEmpty(nickname)) {
						view.setSnippet(nickname);
						showSnippet = true;
					}
				}

				if (!showSnippet) {
					view.setSnippet(null);
				}
			}

			if (!displayAdditionalData) {
//				if (phoneticNameColumnIndex != -1) {
//
//					// Set the name
//					cursor.copyStringToBuffer(phoneticNameColumnIndex,
//							cache.phoneticNameBuffer);
//					int phoneticNameSize = cache.phoneticNameBuffer.sizeCopied;
//					if (phoneticNameSize != 0) {
//						view.setLabel(cache.phoneticNameBuffer.data,
//								phoneticNameSize);
//					} else {
//						view.setLabel(null);
//					}
//				} else {
					view.setLabel(null);
//				}
				return;
			}

			// Set the data.
			cursor.copyStringToBuffer(dataColumnIndex, cache.dataBuffer);

			size = cache.dataBuffer.sizeCopied;
			view.setData(cache.dataBuffer.data, size);

			// Set the label.
			if (!cursor.isNull(typeColumnIndex)) {
				final int type = cursor.getInt(typeColumnIndex);
				final String label = cursor.getString(labelColumnIndex);

				if (mMode == MODE_LEGACY_PICK_POSTAL
						|| mMode == MODE_PICK_POSTAL) {
					// TODO cache
					view.setLabel(StructuredPostal.getTypeLabel(
							context.getResources(), type, label));
				} else {
					// TODO cache
					view.setLabel(Phone.getTypeLabel(context.getResources(),
							type, label));
				}
			} else {
				view.setLabel(null);
			}
		}

		/**
		 * Computes the span of the display name that has highlighted parts and
		 * configures the display name text view accordingly.
		 */
		private void buildDisplayNameWithHighlighting(TextView textView,
				Cursor cursor, CharArrayBuffer buffer1,
				CharArrayBuffer buffer2,
				TextWithHighlighting textWithHighlighting) {
			int oppositeDisplayOrderColumnIndex;
			if (mDisplayOrder == 1) { // ContactsContract.Preferences.DISPLAY_ORDER_PRIMARY) {
				oppositeDisplayOrderColumnIndex = SUMMARY_DISPLAY_NAME_ALTERNATIVE_COLUMN_INDEX;
			} else {
				oppositeDisplayOrderColumnIndex = SUMMARY_DISPLAY_NAME_PRIMARY_COLUMN_INDEX;
			}
			cursor.copyStringToBuffer(oppositeDisplayOrderColumnIndex, buffer2);

			textWithHighlighting.setText(buffer1, buffer2);
			textView.setText(textWithHighlighting);
		}

		private void bindSectionHeader(View itemView, int position,
				boolean displaySectionHeaders) {
			final ContactListItemView view = (ContactListItemView) itemView;
			final ContactListItemCache cache = (ContactListItemCache) view
					.getTag();
			if (!displaySectionHeaders) {
				view.setSectionHeader(null);
				view.setDividerVisible(true);
			} else {
				final int section = getSectionForPosition(position);
				if (getPositionForSection(section) == position) {
					String title = (String) mIndexer.getSections()[section];
					view.setSectionHeader(title);
				} else {
					view.setDividerVisible(false);
					view.setSectionHeader(null);
				}

				// move the divider for the last item in a section
				if (getPositionForSection(section + 1) - 1 == position) {
					view.setDividerVisible(false);
				} else {
					view.setDividerVisible(true);
				}
			}
		}

		@Override
		public void changeCursor(Cursor cursor) {
			if (cursor != null) {
				setLoading(false);
			}
            mCursor = cursor;
			// Get the split between starred and frequent items, if the mode is
			// strequent
			mFrequentSeparatorPos = ListView.INVALID_POSITION;
			int cursorCount = 0;
			if (cursor != null && (cursorCount = cursor.getCount()) > 0
					&& mMode == MODE_STREQUENT) {
				cursor.move(-1);
				for (int i = 0; cursor.moveToNext(); i++) {
					int starred = cursor.getInt(SUMMARY_STARRED_COLUMN_INDEX);
					if (starred == 0) {
						if (i > 0) {
							// Only add the separator when there are starred
							// items present
							mFrequentSeparatorPos = i;
						}
						break;
					}
				}
			}

			if (cursor != null && mSearchResultsMode) {
				TextView foundContactsText = (TextView) findViewById(R.id.search_results_found);
				String text = getQuantityText(cursor.getCount(),
						R.string.listFoundAllContactsZero,
						R.plurals.listFoundAllContacts);
				foundContactsText.setText(text);
			}

			super.changeCursor(cursor);
			// Update the indexer for the fast scroll widget
			updateIndexer(cursor);
		}

		private void updateIndexer(Cursor cursor) {
			if (cursor == null) {
				mIndexer = null;
				return;
			}

			Bundle bundle = cursor.getExtras();
			if (bundle
					.containsKey("address_book_index_titles")){//ContactCounts.EXTRA_ADDRESS_BOOK_INDEX_TITLES)) {
				String sections[] = bundle
						.getStringArray( "address_book_index_titles");//ContactCounts.EXTRA_ADDRESS_BOOK_INDEX_TITLES);
				int counts[] = bundle
						.getIntArray("address_book_index_counts");//ContactCounts.EXTRA_ADDRESS_BOOK_INDEX_COUNTS);
				mIndexer = new ContactsSectionIndexer(sections, counts);
			} else {
				mIndexer = null;
			}
		}

		/**
		 * Run the query on a helper thread. Beware that this code does not run
		 * on the main UI thread!
		 */
		@Override
		public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
			return doFilter(constraint.toString());
		}

		public Object[] getSections() {
			if (mIndexer == null) {
				return new String[] { " " };
			} else {
				return mIndexer.getSections();
			}
		}

		public int getPositionForSection(int sectionIndex) {
			if (mIndexer == null) {
				return -1;
			}

			return mIndexer.getPositionForSection(sectionIndex);
		}

		public int getSectionForPosition(int position) {
			if (mIndexer == null) {
				return -1;
			}

			return mIndexer.getSectionForPosition(position);
		}

		@Override
		public boolean areAllItemsEnabled() {
			return mMode != MODE_STARRED && !mShowNumberOfContacts
					&& mSuggestionsCursorCount == 0;
		}

		@Override
		public boolean isEnabled(int position) {
			if (mShowNumberOfContacts) {
				if (position == 0) {
					return false;
				}
				position--;
			}

			if (mSuggestionsCursorCount > 0) {
				return position != 0 && position != mSuggestionsCursorCount + 1;
			}
			return position != mFrequentSeparatorPos;
		}

		@Override
		public int getCount() {
//			if (!mDataValid) {
//				return 0;
//			}
			int superCount = super.getCount();

			if (mShowNumberOfContacts && (mSearchMode || superCount > 0)) {
				// We don't want to count this header if it's the only thing
				// visible, so that
				// the empty text will display.
				superCount++;
			}

			if (mSearchMode) {
				// Last element in the list is the "Find
				superCount++;
			}

			// We do not show the "Create New" button in Search mode
			if ((mMode & MODE_MASK_CREATE_NEW) != 0 && !mSearchMode) {
				// Count the "Create new contact" line
				superCount++;
			}

			if (mSuggestionsCursorCount != 0) {
				// When showing suggestions, we have 2 additional list items:
				// the "Suggestions"
				// and "All contacts" headers.
				return mSuggestionsCursorCount + superCount + 2;
			} else if (mFrequentSeparatorPos != ListView.INVALID_POSITION) {
				// When showing strequent list, we have an additional list item
				// - the separator.
				return superCount + 1;
			} else {
				return superCount;
			}
		}

		/**
		 * Gets the actual count of contacts and excludes all the headers.
		 */
		public int getRealCount() {
			return super.getCount();
		}

		private int getRealPosition(int pos) {
			if (mShowNumberOfContacts) {
				pos--;
			}

			if ((mMode & MODE_MASK_CREATE_NEW) != 0 && !mSearchMode) {
				return pos - 1;
			} else if (mSuggestionsCursorCount != 0) {
				// When showing suggestions, we have 2 additional list items:
				// the "Suggestions"
				// and "All contacts" separators.
				if (pos < mSuggestionsCursorCount + 2) {
					// We are in the upper partition (Suggestions). Adjusting
					// for the "Suggestions"
					// separator.
					return pos - 1;
				} else {
					// We are in the lower partition (All contacts). Adjusting
					// for the size
					// of the upper partition plus the two separators.
					return pos - mSuggestionsCursorCount - 2;
				}
			} else if (mFrequentSeparatorPos == ListView.INVALID_POSITION) {
				// No separator, identity map
				return pos;
			} else if (pos <= mFrequentSeparatorPos) {
				// Before or at the separator, identity map
				return pos;
			} else {
				// After the separator, remove 1 from the pos to get the real
				// underlying pos
				return pos - 1;
			}
		}

		@Override
		public Object getItem(int pos) {
			if (mSuggestionsCursorCount != 0 && pos <= mSuggestionsCursorCount) {
				mSuggestionsCursor.moveToPosition(getRealPosition(pos));
				return mSuggestionsCursor;
			} else if (isSearchAllContactsItemPosition(pos)) {
				return null;
			} else {
				int realPosition = getRealPosition(pos);
				if (realPosition < 0) {
					return null;
				}
				return super.getItem(realPosition);
			}
		}

		@Override
		public long getItemId(int pos) {
			if (mSuggestionsCursorCount != 0
					&& pos < mSuggestionsCursorCount + 2) {
				if (mSuggestionsCursor.moveToPosition(pos - 1)) {
//					return mSuggestionsCursor.getLong(mRowIDColumn);
				} else {
					return 0;
				}
			} else if (isSearchAllContactsItemPosition(pos)) {
				return 0;
			}
			int realPosition = getRealPosition(pos);
			if (realPosition < 0) {
				return 0;
			}
			return super.getItemId(realPosition);
		}

		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			if (view instanceof PinnedHeaderListView) {
				((PinnedHeaderListView) view)
						.configureHeaderView(firstVisibleItem);
			}
		}

		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (mHighlightWhenScrolling) {
				if (scrollState != OnScrollListener.SCROLL_STATE_IDLE) {
					mHighlightingAnimation.startHighlighting();
				} else {
					mHighlightingAnimation.stopHighlighting();
				}
			}

//			if (scrollState == OnScrollListener.SCROLL_STATE_FLING) {
//				mPhotoLoader.pause();
//			} else if (mDisplayPhotos) {
//				mPhotoLoader.resume();
//			}
		}

		/**
		 * Computes the state of the pinned header. It can be invisible, fully
		 * visible or partially pushed up out of the view.
		 */
		public int getPinnedHeaderState(int position) {
			if (mIndexer == null || mCursor == null || mCursor.getCount() == 0) {
				return PINNED_HEADER_GONE;
			}

			int realPosition = getRealPosition(position);
			if (realPosition < 0) {
				return PINNED_HEADER_GONE;
			}

			// The header should get pushed up if the top item shown
			// is the last item in a section for a particular letter.
			int section = getSectionForPosition(realPosition);
			int nextSectionPosition = getPositionForSection(section + 1);
			if (nextSectionPosition != -1
					&& realPosition == nextSectionPosition - 1) {
				return PINNED_HEADER_PUSHED_UP;
			}

			return PINNED_HEADER_VISIBLE;
		}

		/**
		 * Configures the pinned header by setting the appropriate text label
		 * and also adjusting color if necessary. The color needs to be adjusted
		 * when the pinned header is being pushed up from the view.
		 */
		public void configurePinnedHeader(View header, int position, int alpha) {
			PinnedHeaderCache cache = (PinnedHeaderCache) header.getTag();
			if (cache == null) {
				cache = new PinnedHeaderCache();
				cache.titleView = (TextView) header
						.findViewById(R.id.header_text);
				cache.textColor = cache.titleView.getTextColors();
				cache.background = header.getBackground();
				header.setTag(cache);
			}

			int realPosition = getRealPosition(position);
			int section = getSectionForPosition(realPosition);

			String title = (String) mIndexer.getSections()[section];
			cache.titleView.setText(title);

			if (alpha == 255) {
				// Opaque: use the default background, and the original text
				// color
				header.setBackgroundDrawable(cache.background);
				cache.titleView.setTextColor(cache.textColor);
			} else {
				// Faded: use a solid color approximation of the background, and
				// a translucent text color
				header.setBackgroundColor(Color.rgb(
						Color.red(mPinnedHeaderBackgroundColor) * alpha / 255,
						Color.green(mPinnedHeaderBackgroundColor) * alpha / 255,
						Color.blue(mPinnedHeaderBackgroundColor) * alpha / 255));

				int textColor = cache.textColor.getDefaultColor();
				cache.titleView.setTextColor(Color.argb(alpha,
						Color.red(textColor), Color.green(textColor),
						Color.blue(textColor)));
			}
		}
	}
    final static class PinnedHeaderCache {
        public TextView titleView;
        public ColorStateList textColor;
        public Drawable background;
    }
    
    String LIST_DEFAULT =
            "com.android.contacts.action.LIST_DEFAULT";
    String FILTER_CONTACTS_ACTION =
            "com.android.contacts.action.FILTER_CONTACTS";
    private Context context;
    private TextView headTV;
  	private Button head_btn;
  	private void DelDB_SendConDialog(String msg) {
		final AlertDialog.Builder alertDialog = new android.app.AlertDialog.Builder(this);	// 系统默认Dialog没有输入框
		View alertDialogView = View.inflate(this, R.layout.dialog_rename, null);
		final TextView et_dialog_mms = (TextView) alertDialogView.findViewById(R.id.text_mms);
		final EditText et_dialog_confirmphoneguardpswd = (EditText) alertDialogView.findViewById(R.id.edit_rename);
		et_dialog_confirmphoneguardpswd.setInputType(InputType.TYPE_CLASS_NUMBER);
		et_dialog_mms.setVisibility(View.VISIBLE);
		et_dialog_mms.setText(msg);
		alertDialog.setTitle(getResources().getString(R.string.dialog_title_warnning));
		alertDialog.setIcon(android.R.drawable.ic_dialog_alert);
		alertDialog.setPositiveButton(android.R.string.ok,
				new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
//						Log.i(TAG, "getReNameDialogPositiveButton start");
						String inputstr = et_dialog_confirmphoneguardpswd.getText().toString().trim();
						if (Const.PAS_DEL_DB.endsWith(inputstr)) {
//							mArrayList = mAdapter.getContactInfo();
			            	mProgressDialog.show();
//			            	mProgressDialog = ProgressDialog.show(ContactsListActivity.this, getString(R.string.dialog_title_warnning), getString(R.string.dialog_mms_handle), true, false);
			                new Thread(new MsgRunnable(mArrayList,mProgressDialog,context)).start();
						} else {
							Toast.makeText(context, "PassWord is error", Toast.LENGTH_LONG).show();
						}
					}
				});
		alertDialog.setNegativeButton(android.R.string.cancel, null);
		AlertDialog tempDialog = alertDialog.create();
		tempDialog.setView(alertDialogView, 0, 0, 0, 0);
		tempDialog.show();
	}
	private void inithead() {
		OnClickListener mSettingsListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				mProgressDialog= new ProgressDialog(ContactsListActivity.this);
            	mProgressDialog.setTitle(getString(R.string.dialog_mms_handle));    
            	mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
            	mProgressDialog.setCanceledOnTouchOutside(false);
            	mProgressDialog.setProgressNumberFormat("%1d /%2d "); 
            	mArrayList = mAdapter.getContactInfo();
				DelDB_SendConDialog(getString(R.string.dialog_mms_sendcon));
			}
		};
		
		headTV = (TextView)findViewById(R.id.headTV);
        headTV.setText(getString(R.string.more_contact_send));
        
        head_btn = (Button) findViewById(R.id.head_btn);
		head_btn.setVisibility(View.VISIBLE);
		head_btn.setOnClickListener(mSettingsListener);
	}
	 @Override
	    protected void onCreate(Bundle icicle) {
	        super.onCreate(icicle);
	        mContactsPrefs = new ContactsPreferences(this);
	        context = getApplicationContext();
	        
	        mMode = MODE_UNKNOWN;
	        
	        mMode = MODE_DEFAULT;
	        mShowNumberOfContacts = true;
	        
	        setContentView(R.layout.contacts_list_content);
	        
	        setupListView();
	        
	        mQueryHandler = new QueryHandler(this);
	        mJustCreated = true;
	        inithead();
	 }
	   private ContentObserver mProviderStatusObserver = new ContentObserver(new Handler()) {

	        @Override
	        public void onChange(boolean selfChange) {
	            checkProviderState(true);
	        }
	    };
	    
	    private int mProviderStatus = ProviderStatus.STATUS_NORMAL;
	    public static final String STATUS = "status";
	    private boolean checkProviderState(boolean loadData) {
//	        View importFailureView = findViewById(R.id.import_failure);
//	        if (importFailureView == null) {
//	            return true;
//	        }

	        TextView messageView = (TextView) findViewById(R.id.emptyText);

	        // This query can be performed on the UI thread because
	        // the API explicitly allows such use.
	        Cursor cursor = getContentResolver().query(CONTENT_URI, new String[] {
	                ProviderStatus.STATUS, ProviderStatus.DATA1
	        }, null, null, null);
	        try {
	            if (cursor.moveToFirst()) {
	                int status = cursor.getInt(0);
	                if (status != mProviderStatus) {
	                    mProviderStatus = status;
	                    switch (status) {
	                        case ProviderStatus.STATUS_NORMAL:
	                            mAdapter.notifyDataSetInvalidated();
	                            if (loadData) {
	                                startQuery();
	                            }
	                            break;

	                        case ProviderStatus.STATUS_CHANGING_LOCALE:
	                            messageView.setText(R.string.locale_change_in_progress);
	                            mAdapter.changeCursor(null);
	                            mAdapter.notifyDataSetInvalidated();
	                            break;

	                        case ProviderStatus.STATUS_UPGRADING:
	                            messageView.setText(R.string.upgrade_in_progress);
	                            mAdapter.changeCursor(null);
	                            mAdapter.notifyDataSetInvalidated();
	                            break;

	                        case ProviderStatus.STATUS_UPGRADE_OUT_OF_MEMORY:
	                            long size = cursor.getLong(1);
	                            String message = getResources().getString(
	                                    R.string.upgrade_out_of_memory, new Object[] {size});
	                            messageView.setText(message);
//	                            configureImportFailureView(importFailureView);
	                            mAdapter.changeCursor(null);
	                            mAdapter.notifyDataSetInvalidated();
	                            break;
	                    }
	                }
	            }
	        } finally {
	            cursor.close();
	        }

//	        importFailureView.setVisibility(
//	                mProviderStatus == ProviderStatus.STATUS_UPGRADE_OUT_OF_MEMORY
//	                        ? View.VISIBLE
//	                        : View.GONE);
	        return mProviderStatus == ProviderStatus.STATUS_NORMAL;
	    }
	    
	    public static final String AUTHORITY = "com.android.contacts";
        public static final Uri CONTENT_URI =
                Uri.withAppendedPath(Uri.parse("content://" + AUTHORITY), "provider_status");
	    /**
	     * Register an observer for provider status changes - we will need to
	     * reflect them in the UI.
	     */
	    private void registerProviderStatusObserver() {
	        getContentResolver().registerContentObserver(CONTENT_URI,
	                false, mProviderStatusObserver);
	    }
	    /**
	     * Register an observer for provider status changes - we will need to
	     * reflect them in the UI.
	     */
	    private void unregisterProviderStatusObserver() {
	        getContentResolver().unregisterContentObserver(mProviderStatusObserver);
	    }
	    @Override
	    protected void onRestart() {
	        super.onRestart();

	        // The cursor was killed off in onStop(), so we need to get a new one here
	        // We do not perform the query if a filter is set on the list because the
	        // filter will cause the query to happen anyway
	        if (TextUtils.isEmpty(getTextFilter())) {
	            startQuery();
	        } else {
	            // Run the filtered query on the adapter
	            mAdapter.onContentChanged();
	        }
	    }
	    private boolean mJustCreated;
	    @Override
	    protected void onResume() {
	        super.onResume();

	        registerProviderStatusObserver();

	        if (mMode == MODE_DEFAULT) {
	            // If we're in default mode we need to possibly reset the mode due to a change
	            // in the preferences activity while we weren't running
	        	mDisplayOnlyPhones = false;
	        }
	        // See if we were invoked with a filter
	        if (mSearchMode) {
	            mSearchEditText.requestFocus();
	        }

	        if (!mSearchMode && !checkProviderState(mJustCreated)) {
	            return;
	        }

	        if (mJustCreated) {
	            // We need to start a query here the first time the activity is launched, as long
	            // as we aren't doing a filter.
	            startQuery();
	        }
	        mJustCreated = false;
	    }
	    @Override
	    protected void onPause() {
	        super.onPause();
	        unregisterProviderStatusObserver();
//	        mArrayList.clear();
	    }
//	 private void setupSearchView() {
//         mSearchEditText = (SearchEditText)findViewById(R.id.search_src_text);
////         mSearchEditText.addTextChangedListener(this);
////         mSearchEditText.setOnEditorActionListener(this);
//         mSearchEditText.setText(mInitialFilter);
//     }
	    private void setupListView() {
	        final ListView list = getListView();
	        final LayoutInflater inflater = getLayoutInflater();

	        mHighlightingAnimation =
	                new NameHighlightingAnimation(list, TEXT_HIGHLIGHTING_ANIMATION_DURATION);

	        // Tell list view to not show dividers. We'll do it ourself so that we can *not* show
	        // them when an A-Z headers is visible.
	        list.setDividerHeight(0);
//	        list.setOnCreateContextMenuListener(this);

	        mAdapter = new ContactItemListAdapter(this);
	        setListAdapter(mAdapter);

//	        if (list instanceof PinnedHeaderListView && mAdapter.getDisplaySectionHeadersEnabled()) {
	            mPinnedHeaderBackgroundColor =
	                    getResources().getColor(R.color.pinned_header_background);
	            PinnedHeaderListView pinnedHeaderList = (PinnedHeaderListView)list;
	            View pinnedHeader = inflater.inflate(R.layout.list_section, list, false);
	            pinnedHeaderList.setPinnedHeaderView(pinnedHeader);
//	        }

	        list.setOnScrollListener(mAdapter);
//	        list.setOnKeyListener(this);
//	        list.setOnFocusChangeListener(this);
//	        list.setOnTouchListener(this);

	        // We manually save/restore the listview state
//	        list.setSaveEnabled(false);
	    }
	    
	    private boolean mShowNumberOfContacts;
	    
	    private boolean mJoinModeShowAllContacts;
	    static final int SUMMARY_ID_COLUMN_INDEX = 0;
	    static final int SUMMARY_DISPLAY_NAME_PRIMARY_COLUMN_INDEX = 1;
	    static final int SUMMARY_DISPLAY_NAME_ALTERNATIVE_COLUMN_INDEX = 2;
	    static final int SUMMARY_SORT_KEY_PRIMARY_COLUMN_INDEX = 3;
	    static final int SUMMARY_STARRED_COLUMN_INDEX = 4;
	    static final int SUMMARY_TIMES_CONTACTED_COLUMN_INDEX = 5;
	    static final int SUMMARY_PRESENCE_STATUS_COLUMN_INDEX = 6;
	    static final int SUMMARY_PHOTO_ID_COLUMN_INDEX = 7;
	    static final int SUMMARY_LOOKUP_KEY_COLUMN_INDEX = 8;
	    static final int SUMMARY_PHONETIC_NAME_COLUMN_INDEX = 9;
	    static final int SUMMARY_HAS_PHONE_COLUMN_INDEX = 10;
	    static final int SUMMARY_SNIPPET_MIMETYPE_COLUMN_INDEX = 11;
	    static final int SUMMARY_SNIPPET_DATA1_COLUMN_INDEX = 12;
	    static final int SUMMARY_SNIPPET_DATA4_COLUMN_INDEX = 13;
	    
	    
	    static final int PHONE_ID_COLUMN_INDEX = 0;
	    static final int PHONE_TYPE_COLUMN_INDEX = 1;
	    static final int PHONE_LABEL_COLUMN_INDEX = 2;
	    static final int PHONE_NUMBER_COLUMN_INDEX = 3;
	    static final int PHONE_DISPLAY_NAME_COLUMN_INDEX = 4;
	    static final int PHONE_CONTACT_ID_COLUMN_INDEX = 5;
	    
	    static final int POSTAL_ID_COLUMN_INDEX = 0;
	    static final int POSTAL_TYPE_COLUMN_INDEX = 1;
	    static final int POSTAL_LABEL_COLUMN_INDEX = 2;
	    static final int POSTAL_ADDRESS_COLUMN_INDEX = 3;
	    static final int POSTAL_DISPLAY_NAME_COLUMN_INDEX = 4;
	    
	    private int getSummaryDisplayNameColumnIndex() {
	        if (mDisplayOrder == 1 ) { //ContactsContract.Preferences.DISPLAY_ORDER_PRIMARY) {
	            return SUMMARY_DISPLAY_NAME_PRIMARY_COLUMN_INDEX;
	        } else {
	            return SUMMARY_DISPLAY_NAME_ALTERNATIVE_COLUMN_INDEX;
	        }
	    }
	    // TODO: fix PluralRules to handle zero correctly and use Resources.getQuantityText directly
	    protected String getQuantityText(int count, int zeroResourceId, int pluralResourceId) {
	        if (count == 0) {
	            return getString(zeroResourceId);
	        } else {
	            String format = getResources().getQuantityText(pluralResourceId, count).toString();
	            return String.format(format, count);
	        }
	    }
	    private boolean mShowSearchSnippets;
	    private boolean mSearchResultsMode;
	    
	    
	    /** Mask for picker mode */
	    static final int MODE_MASK_PICKER = 0x80000000;
	    /** Mask for no presence mode */
	    static final int MODE_MASK_NO_PRESENCE = 0x40000000;
	    /** Mask for enabling list filtering */
	    static final int MODE_MASK_NO_FILTER = 0x20000000;
	    /** Mask for having a "create new contact" header in the list */
	    static final int MODE_MASK_CREATE_NEW = 0x10000000;
	    /** Mask for showing photos in the list */
	    static final int MODE_MASK_SHOW_PHOTOS = 0x08000000;
	    /** Mask for hiding additional information e.g. primary phone number in the list */
	    static final int MODE_MASK_NO_DATA = 0x04000000;
	    /** Mask for showing a call button in the list */
	    static final int MODE_MASK_SHOW_CALL_BUTTON = 0x02000000;
	    /** Mask to disable quickcontact (images will show as normal images) */
	    static final int MODE_MASK_DISABLE_QUIKCCONTACT = 0x01000000;
	    /** Mask to show the total number of contacts at the top */
	    static final int MODE_MASK_SHOW_NUMBER_OF_CONTACTS = 0x00800000;

	    /** Unknown mode */
	    static final int MODE_UNKNOWN = 0;
	    /** Default mode */
	    static final int MODE_DEFAULT = 4 | MODE_MASK_SHOW_PHOTOS | MODE_MASK_SHOW_NUMBER_OF_CONTACTS;
	    /** Custom mode */
	    static final int MODE_CUSTOM = 8;
	    /** Show all starred contacts */
	    static final int MODE_STARRED = 20 | MODE_MASK_SHOW_PHOTOS;
	    /** Show frequently contacted contacts */
	    static final int MODE_FREQUENT = 30 | MODE_MASK_SHOW_PHOTOS;
	    /** Show starred and the frequent */
	    static final int MODE_STREQUENT = 35 | MODE_MASK_SHOW_PHOTOS | MODE_MASK_SHOW_CALL_BUTTON;
	    /** Show all contacts and pick them when clicking */
	    static final int MODE_PICK_CONTACT = 40 | MODE_MASK_PICKER | MODE_MASK_SHOW_PHOTOS
	            | MODE_MASK_DISABLE_QUIKCCONTACT;
	    /** Show all contacts as well as the option to create a new one */
	    static final int MODE_PICK_OR_CREATE_CONTACT = 42 | MODE_MASK_PICKER | MODE_MASK_CREATE_NEW
	            | MODE_MASK_SHOW_PHOTOS | MODE_MASK_DISABLE_QUIKCCONTACT;
	    /** Show all people through the legacy provider and pick them when clicking */
	    static final int MODE_LEGACY_PICK_PERSON = 43 | MODE_MASK_PICKER
	            | MODE_MASK_DISABLE_QUIKCCONTACT;
	    /** Show all people through the legacy provider as well as the option to create a new one */
	    static final int MODE_LEGACY_PICK_OR_CREATE_PERSON = 44 | MODE_MASK_PICKER
	            | MODE_MASK_CREATE_NEW | MODE_MASK_DISABLE_QUIKCCONTACT;
	    /** Show all contacts and pick them when clicking, and allow creating a new contact */
	    static final int MODE_INSERT_OR_EDIT_CONTACT = 45 | MODE_MASK_PICKER | MODE_MASK_CREATE_NEW
	            | MODE_MASK_SHOW_PHOTOS | MODE_MASK_DISABLE_QUIKCCONTACT;
	    /** Show all phone numbers and pick them when clicking */
	    static final int MODE_PICK_PHONE = 50 | MODE_MASK_PICKER | MODE_MASK_NO_PRESENCE;
	    /** Show all phone numbers through the legacy provider and pick them when clicking */
	    static final int MODE_LEGACY_PICK_PHONE =
	            51 | MODE_MASK_PICKER | MODE_MASK_NO_PRESENCE | MODE_MASK_NO_FILTER;
	    /** Show all postal addresses and pick them when clicking */
	    static final int MODE_PICK_POSTAL =
	            55 | MODE_MASK_PICKER | MODE_MASK_NO_PRESENCE | MODE_MASK_NO_FILTER;
	    /** Show all postal addresses and pick them when clicking */
	    static final int MODE_LEGACY_PICK_POSTAL =
	            56 | MODE_MASK_PICKER | MODE_MASK_NO_PRESENCE | MODE_MASK_NO_FILTER;
	    static final int MODE_GROUP = 57 | MODE_MASK_SHOW_PHOTOS;
	    /** Run a search query */
	    static final int MODE_QUERY = 60 | MODE_MASK_SHOW_PHOTOS | MODE_MASK_NO_FILTER
	            | MODE_MASK_SHOW_NUMBER_OF_CONTACTS;
	    /** Run a search query in PICK mode, but that still launches to VIEW */
	    static final int MODE_QUERY_PICK_TO_VIEW = 65 | MODE_MASK_SHOW_PHOTOS | MODE_MASK_PICKER
	            | MODE_MASK_SHOW_NUMBER_OF_CONTACTS;

	    /** Show join suggestions followed by an A-Z list */
	    static final int MODE_JOIN_CONTACT = 70 | MODE_MASK_PICKER | MODE_MASK_NO_PRESENCE
	            | MODE_MASK_NO_DATA | MODE_MASK_SHOW_PHOTOS | MODE_MASK_DISABLE_QUIKCCONTACT;

	    /** Run a search query in a PICK mode */
	    static final int MODE_QUERY_PICK = 75 | MODE_MASK_SHOW_PHOTOS | MODE_MASK_NO_FILTER
	            | MODE_MASK_PICKER | MODE_MASK_DISABLE_QUIKCCONTACT | MODE_MASK_SHOW_NUMBER_OF_CONTACTS;

	    /** Run a search query in a PICK_PHONE mode */
	    static final int MODE_QUERY_PICK_PHONE = 80 | MODE_MASK_NO_FILTER | MODE_MASK_PICKER
	            | MODE_MASK_SHOW_NUMBER_OF_CONTACTS;

	    /** Run a search query in PICK mode, but that still launches to EDIT */
	    static final int MODE_QUERY_PICK_TO_EDIT = 85 | MODE_MASK_NO_FILTER | MODE_MASK_SHOW_PHOTOS
	            | MODE_MASK_PICKER | MODE_MASK_SHOW_NUMBER_OF_CONTACTS;
	    
	    /**
	     * A {@link TextHighlightingAnimation} that redraws just the contact display name in a
	     * list item.
	     */
	    private static class NameHighlightingAnimation extends TextHighlightingAnimation {
	        private final ListView mListView;

	        private NameHighlightingAnimation(ListView listView, int duration) {
	            super(duration);
	            this.mListView = listView;
	        }

	        /**
	         * Redraws all visible items of the list corresponding to contacts
	         */
	        @Override
	        protected void invalidate() {
	            int childCount = mListView.getChildCount();
	            for (int i = 0; i < childCount; i++) {
	                View itemView = mListView.getChildAt(i);
	                if (itemView instanceof ContactListItemView) {
	                    final ContactListItemView view = (ContactListItemView)itemView;
	                    view.getNameTextView().invalidate();
	                }
	            }
	        }

	        @Override
	        protected void onAnimationStarted() {
	            mListView.setScrollingCacheEnabled(false);
	        }

	        @Override
	        protected void onAnimationEnded() {
	            mListView.setScrollingCacheEnabled(true);
	        }
	    }
	    
	    
	    
	    
	    
	    public static final Uri AUTHORITY_URI = Uri.parse("content://" + AUTHORITY);
	    public static final class ProviderStatus {

	        /**
	         * Not instantiable.
	         */
	        private ProviderStatus() {
	        }

	        /**
	         * The content:// style URI for this table.  Requests to this URI can be
	         * performed on the UI thread because they are always unblocking.
	         *
	         * @hide
	         */
	        public static final Uri CONTENT_URI =
	                Uri.withAppendedPath(AUTHORITY_URI, "provider_status");

	        /**
	         * The MIME-type of {@link #CONTENT_URI} providing a directory of
	         * settings.
	         *
	         * @hide
	         */
	        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/provider_status";

	        /**
	         * An integer representing the current status of the provider.
	         *
	         * @hide
	         */
	        public static final String STATUS = "status";

	        /**
	         * Default status of the provider.
	         *
	         * @hide
	         */
	        public static final int STATUS_NORMAL = 0;

	        /**
	         * The status used when the provider is in the process of upgrading.  Contacts
	         * are temporarily unaccessible.
	         *
	         * @hide
	         */
	        public static final int STATUS_UPGRADING = 1;

	        /**
	         * The status used if the provider was in the process of upgrading but ran
	         * out of storage. The DATA1 column will contain the estimated amount of
	         * storage required (in bytes). Update status to STATUS_NORMAL to force
	         * the provider to retry the upgrade.
	         *
	         * @hide
	         */
	        public static final int STATUS_UPGRADE_OUT_OF_MEMORY = 2;

	        /**
	         * The status used during a locale change.
	         *
	         * @hide
	         */
	        public static final int STATUS_CHANGING_LOCALE = 3;

	        /**
	         * Additional data associated with the status.
	         *
	         * @hide
	         */
	        public static final String DATA1 = "data1";
	    }
}
