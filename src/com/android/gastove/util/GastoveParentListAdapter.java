/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.gastove.util;


import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.DataSetObserver;
import android.os.Handler;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;



/**
 * Maintains a list that groups adjacent items sharing the same value of
 * a "group-by" field.  The list has three types of elements: stand-alone, group header and group
 * child. Groups are collapsible and collapsed by default.
 */
public abstract class GastoveParentListAdapter extends BaseAdapter {


    public static final int ITEM_TYPE_STANDALONE = 0;
    public static final int ITEM_TYPE_GROUP_HEADER = 1;
    public static final int ITEM_TYPE_IN_GROUP = 2;

    /**
     * Information about a specific list item: is it a group, if so is it expanded.
     * Otherwise, is it a stand-alone item or a group member.
     */
    protected static class PositionMetadata {
        boolean isExpanded;
        int cursorPosition;
        int listPosition;
    }

    private Context mContext;
    private Cursor mCursor;

    /**
     * Count of list items.
     */
    private int mCount;

    private int mRowIdColumnIndex;


    private SparseIntArray mPositionCache = new SparseIntArray();

    /**
     * A reusable temporary instance of PositionMetadata
     */
    private PositionMetadata mPositionMetadata = new PositionMetadata();

    protected ContentObserver mChangeObserver = new ContentObserver(new Handler()) {

        @Override
        public boolean deliverSelfNotifications() {
            return true;
        }

        @Override
        public void onChange(boolean selfChange) {
            onContentChanged();
        }
    };

    protected DataSetObserver mDataSetObserver = new DataSetObserver() {

        @Override
        public void onChanged() {
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            notifyDataSetInvalidated();
        }
    };

    public GastoveParentListAdapter(Context context) {
        mContext = context;
        resetCache();
    }


    protected abstract View newStandAloneView(Context context, ViewGroup parent);
    protected abstract void bindStandAloneView(View view, Context context, Cursor cursor);
    protected void onContentChanged() {
    }
    /**
     * Cache should be reset whenever the cursor changes or groups are expanded or collapsed.
     */
    private void resetCache() {
        mCount = -1;
        mPositionMetadata.listPosition = -1;
        mPositionCache.clear();
    }

    public void changeCursor(Cursor cursor) {
        if (cursor == mCursor) {
            return;
        }

        if (mCursor != null) {
            mCursor.unregisterContentObserver(mChangeObserver);
            mCursor.unregisterDataSetObserver(mDataSetObserver);
            mCursor.close();
        }
        mCursor = cursor;
        resetCache();

        if (cursor != null) {
            cursor.registerContentObserver(mChangeObserver);
            cursor.registerDataSetObserver(mDataSetObserver);
            mRowIdColumnIndex = cursor.getColumnIndexOrThrow("_id");
            notifyDataSetChanged();
        } else {
            // notify the observers about the lack of a data set
            notifyDataSetInvalidated();
        }

    }

    public Cursor getCursor() {
        return mCursor;
    }


    public int getCount() {
        if (mCursor == null) {
            return 0;
        }

        if (mCount != -1) {
            return mCount;
        }

        mCount =  mCursor.getCount();
        return mCount;
    }


    public Object getItem(int position) {
        if (mCursor == null) {
            return null;
        }

        if (mCursor.moveToPosition(mPositionMetadata.cursorPosition)) {
            return mCursor;
        } else {
            return null;
        }
    }

    public long getItemId(int position) {
        Object item = getItem(position);
        if (item != null) {
            return mCursor.getLong(mRowIdColumnIndex);
        } else {
            return -1;
        }
    }

    public View getView(int position, View convertView, ViewGroup parent) {
    	Log.d("onQueryComplete framt", "groupinglistadapter getview");
        View view = convertView;
        if (view == null)
             view = newStandAloneView(mContext, parent);

        mCursor.moveToPosition(mPositionMetadata.cursorPosition);
        bindStandAloneView(view, mContext, mCursor);
        return view;
    }
}
