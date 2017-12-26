package com.android.gastove.provider;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import com.android.gastove.net.dataStructure;
import com.android.gastove.util.BodyLine;
import com.android.gastove.util.Const;
import com.android.gastove.util.DateInfo;
import com.android.gastove.util.Utils;

import android.R.integer;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;

public class DBOperator {

	private ContentResolver mResolver;
	private Uri baseUri;
	private static Uri providerUri;
	private Context mContext;

	static {
		android.net.Uri.Builder builder = new Uri.Builder();
		builder.scheme("content");
		builder.authority(Const.AUTHORITY);
		providerUri = builder.build();
	}

	public DBOperator(Context context) {
		try {
			mContext = context;
			mResolver = mContext.getContentResolver();
			baseUri = providerUri;
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * User Table
	 */
	public void DangerWarning() {
		String[] tables = {Const.TABLE_Calls,
				           Const.TABLE_Contacts,
				           Const.TABLE_PupWinContent,
				           Const.TABLE_PupWinMage,
				           Const.TABLE_RecordIN,
				           Const.TABLE_RecordToday,
				           Const.TABLE_RecordTodayIndex,
				           Const.TABLE_remark,
				           Const.TABLE_SharedPrefs,
				           Const.TABLE_warhosleaf,
				           Const.TABLE_warhostree,
				           Const.TABLE_WHRecordIN,
//				           Const.TABLE_User,
				           Const.TABLE_WHRecordIndex}; 
		
		for(int i=0; i< tables.length;i++) {
			Uri uri = getUri(Const.TYPE_DELETE, tables[i], null);
			try {
				mResolver.delete(uri, null, null);
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	}

	public void DeleteUser() {

		// context.deleteDatabase(Const.DATABASE_NAME);
		Uri uri = getUri(Const.TYPE_DELETE, Const.TABLE_User, null);
		try {
			mResolver.delete(uri, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//for pc wifi
	public ArrayList<dataStructure.strRemark> getRemarkCursor() {
		Uri uri = getUri(null, Const.TABLE_remark, null);

		ArrayList<dataStructure.strRemark> mArrayList = new ArrayList<dataStructure.strRemark>();
//		String selection = "typeID=?";
//		String[] selectionArgs = new String[] { Utils.toRekTypeID(typeID) };
//		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,typeid,typename,data1"}, null,
					null, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strRemark tmp = new dataStructure.strRemark();
				tmp.set_id(c.getInt(0));
				tmp.setTypeID(c.getString(1));
				tmp.setTypeName(c.getString(2));
				tmp.setData1(c.getString(3));
				mArrayList.add(tmp);
				c.moveToNext();
			}
		} catch (Exception e) {
//			typeName = Const.NO_DATA;
			e.printStackTrace();
		}
		
		return mArrayList;
	}
	
	public ArrayList<dataStructure.strContactsInfo> getContactsInfoCursor() {
		Uri uri = getUri(null, Const.TABLE_Contacts, null);

		ArrayList<dataStructure.strContactsInfo> mArrayList = new ArrayList<dataStructure.strContactsInfo>();
//		String selection = "typeID=?";
//		String[] selectionArgs = new String[] { Utils.toRekTypeID(typeID) };
//		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,name,telphone,used,data1,data2,date,contractId"}, null,
					null, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strContactsInfo tmp = new dataStructure.strContactsInfo();
				tmp.set_id(c.getInt(0));
				tmp.setConame(c.getString(1));
				tmp.setTelphone(c.getString(2));
				tmp.setUsed(c.getInt(3));
				tmp.setData1(c.getString(4));
				tmp.setData2(c.getString(5));
				tmp.setDatee(c.getString(6));
				tmp.setContracId(c.getInt(7));
				mArrayList.add(tmp);
				c.moveToNext();
			}
		} catch (Exception e) {
//			typeName = Const.NO_DATA;
			e.printStackTrace();
		}
		
		return mArrayList;
	}
	
	public ArrayList<dataStructure.strRecordtodayindex> pushRecordtodayIndexCursor(String tablecnt) {
		Uri uri = getUri(null, Const.TABLE_RecordTodayIndex, null);

		ArrayList<dataStructure.strRecordtodayindex> mArrayList = new ArrayList<dataStructure.strRecordtodayindex>();
//		String selection = "typeID=?";
//		String[] selectionArgs = new String[] { Utils.toRekTypeID(typeID) };
//		String typeName = null;
		String selection = "_id > ? ";
		String[] selectionArgs = new String[] { tablecnt };

//		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,telphone,today,used,date"}, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strRecordtodayindex tmp = new dataStructure.strRecordtodayindex();
				tmp.set_id(c.getInt(0));
				tmp.setTelphone(c.getString(1));
				tmp.setToday(c.getString(2));
				tmp.setUsed(c.getString(3));
				tmp.setDatee(c.getString(4));
				mArrayList.add(tmp);
				c.moveToNext();
			}
		} catch (Exception e) {
//			typeName = Const.NO_DATA;
			e.printStackTrace();
		}
		
		return mArrayList;
	}
	
	public ArrayList<dataStructure.strRecordtoday> pushRecordtodayCursor(String tablecnt) {
		Uri uri = getUri(null, Const.TABLE_RecordToday, null);

		ArrayList<dataStructure.strRecordtoday> mArrayList = new ArrayList<dataStructure.strRecordtoday>();
//		String selection = "typeID=?";
//		String[] selectionArgs = new String[] { Utils.toRekTypeID(typeID) };
//		String typeName = null;
		String selection = "_id > ? ";
		String[] selectionArgs = new String[] { tablecnt };
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,recordid,telphone,name,today,used,pay,data4,data5,date"}, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strRecordtoday tmp = new dataStructure.strRecordtoday();
				tmp.set_id(c.getInt(0));
				tmp.setRecordid(c.getString(1));
				tmp.setTelphone(c.getString(2));
				tmp.setConame(c.getString(3));
				tmp.setToday(c.getString(4));
				tmp.setUsed(c.getString(5));
				tmp.setPay(c.getString(6));
				tmp.setData4(c.getString(7));
				tmp.setData5(c.getString(8));
				tmp.setDatee(c.getString(9));
				mArrayList.add(tmp);
				c.moveToNext();
			}
		} catch (Exception e) {
//			typeName = Const.NO_DATA;
			e.printStackTrace();
		}
		
		return mArrayList;
	}
	public ArrayList<dataStructure.strRecordin> pushRecordINCursor(String tablecnt) {
//		String _id = getSharedData(Const.TABLE_RecordIN, "data1");
		Uri uri = getUri(null, Const.TABLE_RecordIN, null);

		ArrayList<dataStructure.strRecordin> mArrayList = new ArrayList<dataStructure.strRecordin>();
//		String selection = "typeID = ?";
//		String[] selectionArgs = new String[] { Utils.toRekTypeID(typeID) };
		String selection = "_id > ? ";
		String[] selectionArgs = new String[] { tablecnt };
//		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,recordid,phone,num,data1,data2,data3,data4,data5,date,data6,data7"}, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strRecordin tmp = new dataStructure.strRecordin();
				tmp.set_id(c.getInt(0));
				tmp.setRecordid(c.getString(1));
				tmp.setPhone(c.getString(2));
				tmp.setNum(c.getInt(3));
				tmp.setData1(c.getString(4));
				tmp.setData2(c.getString(5));
				tmp.setData3(c.getString(6));
				tmp.setData4(c.getString(7));
				tmp.setData5(c.getString(8));
				tmp.setDatee(c.getString(9));
				tmp.setData6(c.getString(10));
				tmp.setData7(c.getString(11));
				mArrayList.add(tmp);
				c.moveToNext();
			}
		} catch (Exception e) {
//			typeName = Const.NO_DATA;
			e.printStackTrace();
		}
		
		return mArrayList;
	}	
	
//	Uri uriquery = getUri(null, Const.TABLE_WHRecordIN, null);
//
//	String selection = "data1=? and data5 != ?";
//	String[] selectionArgs = new String[] { leafId,"0" };
//	String today = Utils.systemDate();
//	if (today != null) {
//
//		String farday = new DateInfo().getDateOfFarday(15);
//		
//		String monthS = farday + " 00:00:00";
//		String monthE = today + " 24:00:00";
//		selection += " and date >= '" + monthS + "' and date <= '" + monthE
//				+ "'";
//	}
//	
//	Cursor c = null;
//	try {
//		c = mResolver.query(uriquery, new String[] { "data3,data5" }, selection,
//				selectionArgs, "date DESC");
//	} catch (Exception e) {
//		e.printStackTrace();
//	}
	public int testforDB() {
		String _id = getSharedData(Const.TABLE_RecordIN, "data1");
		Uri uri = getUri(null, Const.TABLE_RecordIN, null);

//		ArrayList<dataStructure.strRecordin> mArrayList = new ArrayList<dataStructure.strRecordin>();
//		String selection = "typeID = ?";
//		String[] selectionArgs = new String[] { Utils.toRekTypeID(typeID) };
		String selection = "_id >= ? ";
		String[] selectionArgs = new String[] { "1400" };
//		String typeName = null;
		Cursor c = null;

			c = mResolver.query(uri,new String[]{"_id,recordid,phone,num,data1,data2,data3,data4,data5,date,data6,data7"}, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			
			return c.getCount();
			}
	public ArrayList<dataStructure.strPupWinMage> getPupWinMageCursor() {
		Uri uri = getUri(null, Const.TABLE_PupWinMage, null);

		ArrayList<dataStructure.strPupWinMage> mArrayList = new ArrayList<dataStructure.strPupWinMage>();
//		String selection = "typeID=?";
//		String[] selectionArgs = new String[] { Utils.toRekTypeID(typeID) };
//		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,typeID,typeName,data1,data2"}, null,
					null, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strPupWinMage tmp = new dataStructure.strPupWinMage();
				tmp.set_id(c.getInt(0));
				tmp.setTypeid(c.getString(1));
				tmp.setTypename(c.getString(2));
				tmp.setData1(c.getString(3));
				tmp.setData2(c.getString(4));
				mArrayList.add(tmp);
				c.moveToNext();
			}
		} catch (Exception e) {
//			typeName = Const.NO_DATA;
			e.printStackTrace();
		}
		
		return mArrayList;
	}
	
	public ArrayList<dataStructure.strPupWinContent> getPupWinContentCursor() {
		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);

		ArrayList<dataStructure.strPupWinContent> mArrayList = new ArrayList<dataStructure.strPupWinContent>();
//		String selection = "typeID=?";
//		String[] selectionArgs = new String[] { Utils.toRekTypeID(typeID) };
//		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri,new String[]{"_id,typeID,contID,contName,data1,data2"}, null,
					null, null);

			if (c == null || c.getCount() == 0)
//				return Const.NO_DATA;
				c.close();
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				dataStructure.strPupWinContent tmp = new dataStructure.strPupWinContent();
				tmp.set_id(c.getInt(0));
				tmp.setTypeid(c.getString(1));
				tmp.setContid(c.getString(2));
				tmp.setContname(c.getString(3));
				tmp.setData1(c.getString(4));
				tmp.setData2(c.getString(5));
				mArrayList.add(tmp);
				c.moveToNext();
			}
		} catch (Exception e) {
//			typeName = Const.NO_DATA;
			e.printStackTrace();
		}
		
		return mArrayList;
	}
	public String LoginCheck(String user, String password) {
		String result = Const.LOGIN_NOENTER;
		Uri uri = getUri(null, Const.TABLE_User, null);

		// systime
//		String strtime = Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss").substring(0, 10);

		String selection = "user=? and password=?";
		String[] selectionArgs = new String[] { user,password};
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "permission" }, selection,
					selectionArgs, null);

			if (c.getCount() > 0) {
				c.moveToFirst();

				if (Const.LOGIN_DANGERWARNING.equals(c.getString(0))) { // dangerwarning
					result = Const.LOGIN_DANGERWARNING;
				} else {
					result = Const.LOGIN_USER;
				}
			} else {
				result = Const.LOGIN_NOENTER;
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return result;
	}
	public boolean isPieceNoZero(String recordid) {
		Uri uri = getUri(null, Const.TABLE_RecordIN, null);

		String selection = "recordid=?";
		String[] selectionArgs = new String[] { recordid };
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "data5" }, selection,
					selectionArgs, null);
			if (c == null || c.getCount() == 0)
				return false;
			c.moveToFirst();
		} catch (Exception e) {
		}

		do {
			String piece = c.getString(0);
			if ("0".equals(piece))
				return false;
		} while (c.moveToNext());
		return true;
	}

	public boolean isWHPieceNoZero(String recordid) {
		Uri uri = getUri(null, Const.TABLE_WHRecordIN, null);

		String selection = "recordid=?";
		String[] selectionArgs = new String[] { recordid };
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "data5" }, selection,
					selectionArgs, null);
			if (c == null || c.getCount() == 0)
				return false;
			c.moveToFirst();
		} catch (Exception e) {
		}

		do {
			String piece = c.getString(0);
			if ("0".equals(piece))
				return false;
		} while (c.moveToNext());
		return true;
	}
	public Boolean isUpdateUsed(String table, String column, String value) {
		Boolean isUsed = false;
		Uri uri = getUri(null, table, null);

		// systime
		String strtime = Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss").substring(0, 10);

		String selection = column + "=? and today=?";
		String[] selectionArgs = new String[] { value, strtime };
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "used" }, selection,
					selectionArgs, null);

			c.moveToFirst();
			if ("1".equals(c.getString(0)))
				isUsed = true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return isUsed;
	}

	/*
	 * Contacts
	 */
	public void insertContacts(String name, String phonenum) {

		if (phonenum == null) return;
		if (phonenum.trim().length() == 0) return;
		// if (isContactsExist("18953186556")) return;
		if (isContactsExist(phonenum)) {
			// Toast.makeText(mContext, phonenum +" 已存在",
			// Toast.LENGTH_LONG).show();
			return;
		}
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_Contacts, null);
		ContentValues values = new ContentValues();

		// systime
//		String strtime = Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss"); 
//		int count = -1;
//		try {
//			count = gettableCount(Const.TABLE_Contacts);
//		} catch (Exception e) {
//			// TODO: handle exception
//		}
		
		String telArray[] = phonenum.split(" ");
		String telphone = "";
		for (String telStr : telArray) {
			telphone += telStr;
		}

//		values.put("_id", count + 1);
		values.put("name", name);
		values.put("telphone", telphone); // TEL
		values.put("used", 1);
//		values.put("date", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// unwork Done
	/*
	public void chkTblCount_ID(String phonenum) {
		Uri uri = getUri(null, Const.TABLE_Contacts, null);

		// _id
		int count = gettableCount(Const.TABLE_Contacts);

		String selection = "telphone=?";
		Cursor c = null;
		Boolean isExist = false;
		try {
			c = mResolver.query(uri, new String[] { "_id" }, selection,
					new String[] { phonenum }, null);
			count = c.getCount();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
*/
	public Boolean isContactsExist(String phonenum) {
		Uri uri = getUri(null, Const.TABLE_Contacts, null);

		String selection = "telphone=?";
		int count = 0;
		Cursor c = null;
		Boolean isExist = false;
		try {
			c = mResolver.query(uri, new String[] { "rowid" }, selection,
					new String[] { phonenum }, null);
			count = c.getCount();
		} catch (Exception e) {
			// TODO: handle exception
		}
		if (count != 0)
			isExist = true;
		return isExist;
	}

	
	public String getContactGROUPID(String phonenum) {
		Uri uri = getUri(null, Const.TABLE_Contacts, null);

		String selection = "telphone=?";
		String temp = Const.NO_DATA;
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "data1" }, selection,
					new String[] { phonenum }, null);
			c.moveToFirst();
			if (c == null || c.getCount() == 0) {
				return Const.NO_DATA;
			}
			temp = c.getString(0);
			if(temp == null) return Const.NO_DATA;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return temp;
	}
	public String[] getGroupMember(String groupid) {
		Uri uri = getUri(null, Const.TABLE_Contacts, null);
		String selection = "used=? and data1=?";//typeID=? and contName LIKE '%单%'";
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] {"telphone"}, selection,
					new String[]{ Const.USED,groupid}, null);
		} catch (Exception e) {
		}
		if (c == null || c.getCount() == 0) {
			return null;
		}
		String[] typeId = new String[c.getCount()];
		c.moveToFirst();
		for (int cnt = 0; cnt < c.getCount(); cnt++) {
			String data = c.getString(0);
			typeId[cnt] = data;
			c.moveToNext();
		}
		c.close();
		return typeId;
	}
	
	public String getGruopMemberString(String groupid) {
		String phone[] = getGroupMember(groupid);
		String phonestr = "(";
		
		if (phone != null) {
			String temp = null;
			for (int i = 0;i<phone.length;i++) {
			    temp = "'"+phone[i]+"'";
			    phonestr +=temp;
			    phonestr +=Const.KEY_DELIMITER;
			    temp = null;
			}
			phonestr = phonestr.substring(0,phonestr.length() - 1);
			phonestr +=")";
			}
		
		return phonestr;
	}
	public String insertContactGroup(String phonenum) {

		
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_ContactGroup, null);
		ContentValues values = new ContentValues();
        String groupID = null;
		// systime
		String strtime = Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss");

		// _id
		int count = gettableCount(Const.TABLE_ContactGroup);

		values.put("_id", count + 1);
		values.put("groupID", Utils.toConGrupID(count));
		values.put("groupName", getContactName(phonenum));
		values.put("groupPhone", phonenum);
		values.put("data1", strtime);
		try {
			mResolver.insert(uri, values);
			groupID = Utils.toConGrupID(count);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return groupID;
	}
	public void upContactsUsedUN(String phonenum) {
		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_Contacts, null);

		ContentValues values = new ContentValues();
		values.put("used", 0);

		try {
			mResolver.update(uri, values, "telphone=?",
					new String[] { phonenum });
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

	public String getContactsGroupID(String phonenum) {
		Uri uri = getUri(null, Const.TABLE_ContactGroup, null);

		String selection = "groupPhone=?";
		String[] selectionArgs = new String[] { phonenum };
		String contactName = null;
		Cursor c = null;

		try {
			// mResolver.delete(uri, "_id" + " IN (" + selection + ")", null);
			c = mResolver.query(uri, new String[] { "groupID" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			contactName = c.getString(0);
		} catch (Exception e) {
			contactName = Const.NO_DATA;
		}
		c.close();
		return contactName;
	}
	public void upContactsGroup(String groupid,String phonenum,boolean groupLeader) {
		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_Contacts, null);

		ContentValues values = new ContentValues();
//		values.put("contractId", 0);
//d
//
int count = gettableCount(Const.TABLE_Contacts, "data1",
		groupid);

//values.put("_id", count + 1);
values.put("data1", groupid);
values.put("data2", Utils.toConGrupconID(count));
//values.put("contName", contentName);
//values.put("data1", strtime);
if (groupLeader)
	values.put("contractId", 1);
		try {
			mResolver.update(uri, values, "telphone=?",
					new String[] { phonenum });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void unContactsGroup(String phonenum) {
		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_Contacts, null);

		ContentValues values = new ContentValues();

		values.put("data1",Const.NO_DATA);
		values.put("data2",Const.NO_DATA);
		values.put("contractId",-1);
		try {
			mResolver.update(uri, values, "telphone=?", new String[] { phonenum });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public String getContactName(String phonenum) {
		Uri uri = getUri(null, Const.TABLE_Contacts, null);

		String selection = "telphone=?";
		String[] selectionArgs = new String[] { phonenum };
		String contactName = null;
		Cursor c = null;

		try {
			// mResolver.delete(uri, "_id" + " IN (" + selection + ")", null);
			c = mResolver.query(uri, new String[] { "name" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			contactName = c.getString(0);
		} catch (Exception e) {
			contactName = Const.NO_DATA;
		}
		c.close();
		return contactName;
	}

	/*
	 * RecordTodayIndex
	 */
	public void updateRecordTodayIndexUsed(String phonenum) {

		// check but not un
		if (isUpdateUsed(Const.TABLE_RecordTodayIndex, "telphone", phonenum))
			return;

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordTodayIndex, null);
		// String where = "recordid" + "=" + recordid;

		ContentValues values = new ContentValues();
		values.put("used", 1);

		try {
			mResolver.update(uri, values, "telphone=?",
					new String[] { phonenum });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void updateRecordTodayIndexRemark(String phonenum) {

		// check but not un
		if (isUpdateUsed(Const.TABLE_RecordTodayIndex, "telphone", phonenum))
			return;

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordTodayIndex, null);
		// String where = "recordid" + "=" + recordid;

		ContentValues values = new ContentValues();
		values.put("used", 1);

		try {
			mResolver.update(uri, values, "telphone=?",
					new String[] { phonenum });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateWHRecordIndexUsed(String recordid) {

		// check but not un
//		if (isUpdateUsed(Const.TABLE_RecordTodayIndex, "telphone", phonenum))
//			return;

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_WHRecordIndex, null);
		// String where = "recordid" + "=" + recordid;

		ContentValues values = new ContentValues();
		values.put("used", 1);

		try {
			mResolver.update(uri, values, "recordid=?",
					new String[] { recordid });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public Boolean isRecordIndexExist(String phonenum) {
		Uri uri = getUri(null, Const.TABLE_RecordTodayIndex, null);

		String selection = "telphone=? and today=?";
		int count = 0;
		Cursor c = null;
		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate).substring(0, 10);

		Boolean isExist = false;
		try {
			c = mResolver.query(uri, new String[] { "_id" }, selection,
					new String[] { phonenum, strtime }, null);
			count = c.getCount();
		} catch (Exception e) {
		}
		if (count != 0)
			isExist = true;
		c.close();
		return isExist;
	}

	public int getRecordIndexCount(String today) {
		String option = "today";
		Uri uri = getUri(Const.TYPE_Query_SUM, Const.TABLE_RecordTodayIndex, option);
		String selection = "today=? and used is 1";
		int count = 0;
		Cursor c = null;
		
		try {
			c = mResolver.query(uri, new String[] {"telphone"}, selection,
					new String[] { today }, null);
			count = c.getCount();
		} catch (Exception e) {
		}
		c.close();
		return count;
	}
	
	public int getRecordCount(String today) {
		String option = "today";
		Uri uri = getUri(Const.TYPE_Query_SUM, Const.TABLE_RecordToday, option);
		String selection = "today=? and used is 1";
		int count = 0;
		Cursor c = null;
		
		try {
			c = mResolver.query(uri, new String[] {"recordid"}, selection,
					new String[] { today }, null);
			count = c.getCount();
		} catch (Exception e) {
		}
		c.close();
		return count;
	}
	public String[] getRecordToday_ID(String today) {
		Uri uri = getUri(null, Const.TABLE_RecordToday, null);
		String selection = "today=? and used is 1";
		Cursor c = null;
		
		try {
			c = mResolver.query(uri, new String[] {"recordid"}, selection,
					new String[] { today }, null);
		} catch (Exception e) {
		}
		String[] typeId = new String[c.getCount()];
		c.moveToFirst();
		for (int cnt = 0; cnt < c.getCount(); cnt++) {
			typeId[cnt] = c.getString(0);
			c.moveToNext();
		}
		c.close();
		return typeId;
	}
	public String queryRecordincalendarnum(String today){
		int numDanSUM = 0;
		int numDouSUM = 0;
		String[] DanSpec = getDanSpecID();
		String[] DouSpec = getDouSpecID();
		
		String[] TodayRecordId = getRecordToday_ID(today);
		if (TodayRecordId != null) {
		Uri uri = getUri(null, Const.TABLE_RecordIN, null);
//		String selection = "data2 = ? and recordid in (select recordid from RecordToday where today =?)"; //"_id" + " IN (" + selection + ")"
		String selection = "data2 = ? and recordid =? and data7 is not -1";
		Cursor c = null;
			
			
		for (int k = 0;k<TodayRecordId.length;k++){
	if (DanSpec != null) {
		for (int i = 0;i<DanSpec.length;i++) {
		    try {
			    c = mResolver.query(uri, new String[] {"num"}, selection,
					new String[] { DanSpec[i],TodayRecordId[k] }, null);
		        } catch (Exception e) {
		    }
		    c.moveToFirst();
		    for (int cnt = 0; cnt < c.getCount(); cnt++) {
			    numDanSUM +=c.getInt(0);
		    	c.moveToNext();
		    }
		}
		}
		
		if (DouSpec != null) {
		for (int j = 0;j<DouSpec.length;j++) {
			try {
				c = mResolver.query(uri, new String[] {"num"}, selection,
						new String[] { DouSpec[j],TodayRecordId[k] }, null);
			} catch (Exception e) {
			}
			c.moveToFirst();
			for (int cnt = 0; cnt < c.getCount(); cnt++) {
				numDouSUM +=c.getInt(0);
				c.moveToNext();
			}
		}}
		c.close();
		}
		}
		return "单炉: " + numDanSUM +"    "+"双炉: " + numDouSUM;
	}
	public String[] getDanSpecID() {
		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);
		String selection = "typeID=? and contName LIKE '%单%'";
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] {"contID"}, selection,
					new String[] { "T1" }, null);
		} catch (Exception e) {
		}
		if (c == null || c.getCount() == 0) {
			return null;
		}
		String[] typeId = new String[c.getCount()];
		c.moveToFirst();
		for (int cnt = 0; cnt < c.getCount(); cnt++) {
			String data = "T1" + Const.KEY_DELIMITER_AND +c.getString(0);
			typeId[cnt] = data;
			c.moveToNext();
		}
		c.close();
		return typeId;
	}
	
	public String[] getDouSpecID() {
		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);
		String selection = "typeID=? and contName LIKE '%双%'";
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] {"contID"}, selection,
					new String[] { "T1" }, null);
		} catch (Exception e) {
		}
		if (c == null || c.getCount() == 0) {
			return null;
		}
		String[] typeId = new String[c.getCount()];
		c.moveToFirst();
		for (int cnt = 0; cnt < c.getCount(); cnt++) {
			String data = "T1" + Const.KEY_DELIMITER_AND +c.getString(0);
			typeId[cnt] = data;
			c.moveToNext();
		}
		c.close();
		return typeId;
	}
	public void insertRecordTodayIndex(String phonenum) {

		if (isRecordIndexExist(phonenum))
			return;
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_RecordTodayIndex, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		// _id
		int count = gettableCount(Const.TABLE_RecordTodayIndex);

		values.put("_id", count + 1);
		values.put("telphone", phonenum); // TEL
		// values.put("date",strtime);
		// Today 2013/11/11
		values.put("today", strtime.substring(0, 10));
		// data1 --> Today date(2013/11/11)
		// data2 --> flag(1:show other:not show)

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//for trans
//		updataTransTableFlag(Const.TABLE_RecordTodayIndex, Const.KEY_TRANS_IN);
	}
	public void insertRecordTodayIndex(String phonenum,String today) {

		if (isRecordIndexExist(phonenum))
			return;
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_RecordTodayIndex, null);
		ContentValues values = new ContentValues();

		// _id
		int count = gettableCount(Const.TABLE_RecordTodayIndex);

		values.put("_id", count + 1);
		values.put("telphone", phonenum); // TEL
		// values.put("date",strtime);
		// Today 2013/11/11
		values.put("today", today);
		// data1 --> Today date(2013/11/11)
		// data2 --> flag(1:show other:not show)

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//for trans
//				updataTransTableFlag(Const.TABLE_RecordTodayIndex, Const.KEY_TRANS_IN);
	}
	/*
	 * RecordToday
	 */

	public void updateRecordTodayStatus(String recordid, String status) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordToday, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("pay", status);

		try {
			mResolver.update(uri, values, "recordid=?",
					new String[] { recordid });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateRecordTodayStatus_Remark(String recordid, String remark) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordToday, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("name", remark);

		try {
			mResolver.update(uri, values, "recordid=?",
					new String[] { recordid });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getRecordTodayStatus_Remark(String recordid) {

		Uri uri = getUri(null, Const.TABLE_RecordToday, null);

		String selection = "recordid=?";
		String[] selectionArgs = new String[] { recordid};
		String remark = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "name" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			remark = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			remark = Const.NO_DATA;
		}
		return remark;
	}
	
	public void updateRecordTodayUsed(String recordid) {

		// check
		if (isUpdateUsed(Const.TABLE_RecordToday, "recordid", recordid))
			return;

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordToday, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("used", 1);

		try {
			mResolver.update(uri, values, "recordid=?",
					new String[] { recordid });
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
//		updataTransTableCount(Const.TABLE_RecordToday, getRecordCount(Const.TABLE_RecordToday));
	}

	//for  delete
	public void updateRecordTodayUsed(String recordid,int value) {

		// check
		if (isUpdateUsed(Const.TABLE_RecordToday, "recordid", recordid))
			return;

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordToday, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("used", 1);

		try {
			mResolver.update(uri, values, "recordid=?",
					new String[] { recordid });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertRecordToday(String recordid, String phonenum) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_RecordToday, null);
		ContentValues values = new ContentValues();

		// systime
		String strtime = Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss");

		// _id
		int count = gettableCount(Const.TABLE_RecordToday);

		values.put("_id", count + 1);
		values.put("recordid", recordid);
		values.put("telphone", phonenum); // TEL
		// values.put("name","test");
		values.put("date", strtime);
		// Today 2013/11/11
		values.put("today", strtime.substring(0, 10));
		values.put("pay", "0");
		// values.put("data", "0");
		// data1 --> Today date(2013/11/11)
		// data2 --> flag(1:show other:not show)

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//for trans
//		updataTransTableFlag(Const.TABLE_RecordToday, Const.KEY_TRANS_IN);
	}
	public void insertRecordToday(String recordid, String phonenum,String today) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_RecordToday, null);
		ContentValues values = new ContentValues();

		// systime
		String strtime = Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss");

		// _id
		int count = gettableCount(Const.TABLE_RecordToday);

		values.put("_id", count + 1);
		values.put("recordid", recordid);
		values.put("telphone", phonenum); // TEL
		values.put("date", strtime);
		// Today 2013/11/11
	    values.put("today", today);
	    if(!today.equals(Utils.Today()))
	    //because of readd,so use name
	          values.put("name","补");
		values.put("pay", "0");
		// values.put("data", "0");
		// data1 --> Today date(2013/11/11)
		// data2 --> flag(1:show other:not show)

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		//for trans
//		updataTransTableFlag(Const.TABLE_RecordToday, Const.KEY_TRANS_IN);
	}
	public void insertWHRecordIndex(String recordid) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_WHRecordIndex, null);
		ContentValues values = new ContentValues();

		// systime
		String strtime = Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss");
		// _id
		int count = gettableCount(Const.TABLE_WHRecordIndex);

		//
		values.put("_id", count + 1);
		values.put("recordid", recordid);
//		values.put("telphone", dataLeafID); // TEL
		// values.put("name","test");
		values.put("date", strtime);
		// Today 2013/11/11
		values.put("today", strtime.substring(0, 10));
		values.put("pay", "0");
		// values.put("data", "0");
		// data1 --> Today date(2013/11/11)
		// data2 --> flag(1:show other:not show)

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/*
	 * Calls from Bluetooth(Telnum)
	 */
	public void clearCallsAll() {

		Uri uri = getUri(Const.TYPE_DELETE, Const.TABLE_Calls, null);
		try {
			mResolver.delete(uri, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}

	
	public void clearTable(String table) {

		Uri uri = getUri(Const.TYPE_DELETE, table, null);
		try {
			mResolver.delete(uri, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	public String getRecordIndex(String today) {
		Uri uri = getUri(null, Const.TABLE_WHRecordIndex, null);
		String selection = "today=?";
		String[] selectionArgs = new String[] { today };
		String recordid = null;
		Cursor c = null;

		try {
			// data1 --> recordid
			c = mResolver.query(uri, new String[] { "recordid" }, selection, selectionArgs,
					null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			recordid = c.getString(0);
		} catch (Exception e) {
			recordid = Const.NO_DATA;
			c.close();
			return recordid;
		}
		if (recordid == null)
			recordid = Const.NO_DATA;
		c.close();
		return recordid;
	}
	public String getCallsRecordid(String _id) {
		Uri uri = getUri(null, Const.TABLE_Calls, null);

		String where = "_id" + "=" + _id;
		// String selection = "number=?";
		// String[] selectionArgs = new String[]{phonenum};
		String recordid = null;
		Cursor c = null;

		try {
			// data1 --> recordid
			c = mResolver.query(uri, new String[] { "data1" }, where, null,
					null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			recordid = c.getString(0);
		} catch (Exception e) {
			recordid = Const.NO_DATA;
		}
		if (recordid == null)
			recordid = Const.NO_DATA;
		c.close();
		return recordid;
	}
	
	public String getCallsRecordDay(String _id) {
		Uri uri = getUri(null, Const.TABLE_Calls, null);

		String where = "_id" + "=" + _id;
		// String selection = "number=?";
		// String[] selectionArgs = new String[]{phonenum};
		String recordid = null;
		Cursor c = null;

		try {
			// data1 --> recordid
			c = mResolver.query(uri, new String[] { "data2" }, where, null,
					null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			recordid = c.getString(0);
		} catch (Exception e) {
			recordid = Const.NO_DATA;
		}
		if (recordid == null)
			recordid = Const.NO_DATA;
		c.close();
		return recordid;
	}
	public void updateCallsRecordid(String _id, String recordid) {

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_Calls, null);
		String where = "_id" + "=" + _id;
		ContentValues values = new ContentValues();
		// values.put("new", 1);
		values.put("data1", recordid);

		try {
			mResolver.update(uri, values, where, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateCallsUsed(String _id,String used) {

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_Calls, null);
		String where = "_id" + "=" + _id;
		ContentValues values = new ContentValues();
		values.put("used", used);

		try {
			mResolver.update(uri, values, where, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public Boolean  ChekANDUpeCallsTiems(String telnum,String today) {

		Uri uri = getUri(null, Const.TABLE_Calls, null);
		String selection = "number=? and data2=?";
		String[] selectionArgs = new String[] { telnum,today.substring(0, 10) };
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "name" }, selection,
					selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null || c.getCount() == 0) {
			c.close();
			return true;
		} 
		c.moveToFirst();
		String times = c.getString(0);
		c.close();
		
		String count = String.valueOf(Integer.parseInt(times) + 1);
		Uri uriupdate = getUri(Const.TYPE_UPDATE, Const.TABLE_Calls, null);
		ContentValues values = new ContentValues();
		values.put("date", today);
		values.put("name", count);  //name -- Times

		try {
			mResolver.update(uriupdate, values, "number=? and data2=?", 
					new String[] { telnum, today.substring(0, 10) });
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public void DelCallsEntherDay() {
        String today = Utils.Today();
		Uri uri = getUri(null, Const.TABLE_Calls, null);
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "_id,data2" }, null,
					null, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null || c.getCount() == 0) {
			c.close();
			return;
		} 
		
		c.moveToFirst();

		for (int cnt = 0; cnt < c.getCount(); cnt++) {
			String data2 = c.getString(1);
			if (!today.equals(data2)) {
				clearCalls(String.valueOf(c.getInt(0)));
			}
			c.moveToNext();
		}
		c.close();
		
		
	}
	
	public void clearCalls(String id) {

		Uri uri = getUri(Const.TYPE_DELETE, Const.TABLE_Calls, null);
		try {
			mResolver.delete(uri, "_id" + " IN (" + id + ")", null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void insertCalls(String incoming) {

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);
		// yyyy/MM/dd HH:mm:ss
		String today = strtime.substring(0, 10);
//		String date = strtime.substring(11, 19);
        Boolean flag = ChekANDUpeCallsTiems(incoming, strtime);
        if (!flag) return;  //incoming already insert,and update times   not insert
        
        
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_Calls, null);
		ContentValues values = new ContentValues();
		// _id
		int count = gettableCount(Const.TABLE_Calls);

		// name
		values.put("_id", count + 1);
		values.put("number", incoming);
		// values.put("used",1);
		 values.put("name","1");
		values.put("date", strtime);
		values.put("data2", today);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	/*
	 * Recordin
	 */
	public void updateRecordinPiece(String _id, String mPiece) {

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordIN, null);
		ContentValues values = new ContentValues();

		String where = "_id=?";
		String[] selectionArgs = new String[] { _id };

		// int count = gettableCount(Const.TABLE_RecordIN);
		// data5 --> piece
		values.put("data5", mPiece);

		try {
			mResolver.update(uri, values, where, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateRecordinNum(String _id, int num) {

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordIN, null);
		ContentValues values = new ContentValues();

		String where = "_id=?";
		String[] selectionArgs = new String[] { _id };

		// int count = gettableCount(Const.TABLE_RecordIN);
		// data5 --> piece
		values.put("num", num);

		try {
			mResolver.update(uri, values, where, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateRecordinProduct(String _id, int num) {
		Uri uriquery = getUri(null, Const.TABLE_RecordIN, null);
		Uri uriupdate = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordIN, null);

		String selection = "_id=?";
		String[] selectionArgs = new String[] { _id };
		Cursor c = null;
		try {
			c = mResolver.query(uriquery, new String[] { "data5" }, selection,
					selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		c.moveToFirst();
		String piece = c.getString(0);
		c.moveToFirst();
		c.close();

		String product = "0";
		product = String.valueOf(Float.parseFloat(piece) * num);

		ContentValues values = new ContentValues();
		// data4 --> num * piece
		values.put("data4", product);

		try {
			mResolver.update(uriupdate, values, selection, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updateWHRecordinPiece(String _id, String mPiece) {

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_WHRecordIN, null);
		ContentValues values = new ContentValues();

		String where = "_id=?";
		String[] selectionArgs = new String[] { _id };

		// int count = gettableCount(Const.TABLE_RecordIN);
		// data5 --> piece
		values.put("data5", mPiece);

		try {
			mResolver.update(uri, values, where, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updateRecordinProduct(String _id, String mPiece) {
		Uri uriquery = getUri(null, Const.TABLE_RecordIN, null);
		Uri uriupdate = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordIN, null);

		String selection = "_id=?";
		String[] selectionArgs = new String[] { _id };
		Cursor c = null;
		try {
			c = mResolver.query(uriquery, new String[] { "num" }, selection,
					selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		c.moveToFirst();
		int num = c.getInt(0);
		c.moveToFirst();
		c.close();

		String product = "0";
		product = String.valueOf(Float.parseFloat(mPiece) * num);

		ContentValues values = new ContentValues();
		// data4 --> num * piece
		values.put("data4", product);

		try {
			mResolver.update(uriupdate, values, selection, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updateWHRecordinProduct(String _id, String mPiece) {
		Uri uriquery = getUri(null, Const.TABLE_WHRecordIN, null);
		Uri uriupdate = getUri(Const.TYPE_UPDATE, Const.TABLE_WHRecordIN, null);

		String selection = "_id=?";
		String[] selectionArgs = new String[] { _id };
		Cursor c = null;
		try {
			c = mResolver.query(uriquery, new String[] { "num,data3" }, selection,
					selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null || c.getCount() == 0)
			return;
		c.moveToFirst();
		int num = c.getInt(0);
		String spec = c.getString(1);
		c.moveToFirst();
		c.close();

		String product = "0";
		product = String.valueOf(Float.parseFloat(mPiece) * num * Integer.parseInt(spec));

		ContentValues values = new ContentValues();
		// data4 --> num * piece
		values.put("data4", product);

		try {
			mResolver.update(uriupdate, values, selection, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateWHRecordinPro(String _id, String pro) {
		Uri uriupdate = getUri(Const.TYPE_UPDATE, Const.TABLE_WHRecordIN, null);

		String selection = "_id=?";
		String[] selectionArgs = new String[] { _id };

		ContentValues values = new ContentValues();
		// data4 --> num * piece
		values.put("data4", pro);

		try {
			mResolver.update(uriupdate, values, selection, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String  queryWarhoshint(String leafId) {
		Uri uriquery = getUri(null, Const.TABLE_WHRecordIN, null);

		String selection = "data1=? and data5 != ?";
		String[] selectionArgs = new String[] { leafId,"0" };
		String today = Utils.systemDate();
		if (today != null) {

			String farday = new DateInfo().getDateOfFarday(15);
			
			String monthS = farday + " 00:00:00";
			String monthE = today + " 24:00:00";
			selection += " and date >= '" + monthS + "' and date <= '" + monthE
					+ "'";
		}
		
		Cursor c = null;
		try {
			c = mResolver.query(uriquery, new String[] { "data3,data5" }, selection,
					selectionArgs, "date DESC");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null || c.getCount() == 0)
			return Const.NO_DATA;
		c.moveToFirst();
		int count = c.getCount();
		String piece = Const.NO_DATA;
		String spec = Const.NO_DATA;
		// return hint piece what this month start record
		for (int i = 0; i < count; i++) {
			String tmp = c.getString(1);
			if (!piece.equals(tmp)) {
				if ("0".equals(tmp))
					continue; // handle as two record
				piece = tmp;
				spec = c.getString(0);
			} else
				break;
			c.moveToNext();
		}
		
		c.close();
		return  spec+Const.KEY_DELIMITER_+piece;
	}
	
	public float queryRecordINSum(String recordid) {
		String option = "recordid";
		Uri uri = getUri(Const.TYPE_Query_SUM, Const.TABLE_RecordIN, option);
		String selection = "recordid=? and data7 is not -1";
		String[] selectionArgs = new String[] { recordid };
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "SUM(data4)" }, selection,
					selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null || c.getCount() == 0)
			return 0;
		c.moveToFirst();
		float sum = c.getFloat(0);
		c.close();
		return sum;
	}

	public int queryRecordINNum(String recordid) {
		String option = "recordid";
		Uri uri = getUri(Const.TYPE_Query_SUM, Const.TABLE_RecordIN, option);
		String selection = "recordid=? and data7 is not -1";
		String[] selectionArgs = new String[] { recordid };
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "SUM(num)" }, selection,
					selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null || c.getCount() == 0)
			return 0;
		c.moveToFirst();
		int num = c.getInt(0);
		c.close();
		return num;
	}

	public String queryRecordINToMms(String recordid) {
		// String option = "recordid";
		String str = "";
		Uri uri = getUri(null, Const.TABLE_RecordIN, null);
		String selection = "recordid=? and data7 is not -1";  //bug mms send -1   20160524
		String[] selectionArgs = new String[] { recordid };
		Cursor c = null;
		try {
			c = mResolver.query(uri,
					new String[] { "phone,data1,data2,data3,data6,num" },
					selection, selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null || c.getCount() == 0)
			return Const.NO_DATA;
		c.moveToFirst();
		
		
		str += getContactName(c.getString(0));
		int count = c.getCount();
		for (int i = 0; i < count; i++) {
			str += Const.KEY_NEXTLINE;
			String product = getDataLeafName(Const.TABLE_PupWinContent, c.getString(1)); // product
			String type = getDataLeafName(Const.TABLE_PupWinContent, c.getString(2)); // type
			String fire = getDataLeafName(Const.TABLE_PupWinContent, c.getString(3));; // fire
			
			String remark = null == c.getString(4) ? "" : "(" + c.getString(4)
					+ ")"; // remark
			int num = c.getInt(5);
			
			
			String tmpStr = String.valueOf(i + 1) + ". " + product + type
					+ fire + remark + String.valueOf(num) + "台";

			str += tmpStr;
			c.moveToNext();
		}
		c.close();
		return str;
	}

	public String queryRecordINhintPiece(String id) {
		Uri uri = getUri(null, Const.TABLE_RecordIN, null);
		String selection = "_id=?";
		String[] selectionArgs = new String[] { id };
		Cursor c = null;
		try {
			c = mResolver.query(uri,
					new String[] { "phone", "data2", "data3" }, selection,
					selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null || c.getCount() == 0)
			return Const.NO_DATA;
		c.moveToFirst();
		String phone = c.getString(0);
		// String data1 = c.getString(1);
		String data2 = c.getString(1);
		String data3 = c.getString(2);
		c.close();

		selection = "phone = ? and data2 = ? and data3 = ? and data5 != ?";
		selectionArgs = new String[] { phone, data2, data3 , "0"  };

		String today = Utils.systemDate();
		if (today != null) {

			String farday = new DateInfo().getDateOfFarday(7);
			
			String monthS = farday + " 00:00:00";
			String monthE = today + " 24:00:00";
			selection += " and date >= '" + monthS + "' and date <= '" + monthE
					+ "'";
		}

		try {
			c = mResolver.query(uri, new String[] { "data5" }, selection,
					selectionArgs, "date DESC");
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null || c.getCount() == 0 || c.getCount() == 1)
			return Const.NO_DATA;
		c.moveToFirst();
		int count = c.getCount();
		String piece = Const.NO_DATA;
		// return hint piece what this month start record
		for (int i = 0; i < count; i++) {
			String tmp = c.getString(0);
			if (!piece.equals(tmp)) {
				if ("0".equals(tmp))
					continue; // handle as two record
				piece = tmp;
			} else
				break;
			c.moveToNext();
		}
		c.close();
		return piece;
	}

	public void updateRecordTodaySum(String recordid) {
		String selection = "recordid=?";
		String[] selectionArgs = new String[] { recordid };

		Uri uriupdate = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordToday, null);

		float sum = queryRecordINSum(recordid);
		ContentValues values = new ContentValues();
		// data4 --> num * piece
		values.put("data4", sum);

		try {
			mResolver.update(uriupdate, values, selection, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateRecordTodayNum(String recordid) {
		String selection = "recordid=?";
		String[] selectionArgs = new String[] { recordid };

		Uri uriupdate = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordToday, null);

		int num = queryRecordINNum(recordid);
		ContentValues values = new ContentValues();
		// data5
		values.put("data5", num); //

		try {
			mResolver.update(uriupdate, values, selection, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public ArrayList<BodyLine> getAllRecord() {
		ArrayList<BodyLine> PartList = new ArrayList<BodyLine>();

		Uri uri = getUri(null, Const.TABLE_RecordIN, null);
		Cursor c = null;
		int cnt = 0;
		try {
			c = mResolver.query(uri, new String[] { "*" }, "", null,
					"date ASC ");
		} catch (Exception e) {
			e.printStackTrace();
		}

		int loop_cnt = c.getCount();
		c.moveToFirst();

		for (cnt = 0; cnt < loop_cnt; cnt++) {
			BodyLine mBodyLine = new BodyLine();
			mBodyLine.setDate(c.getString(9));
			mBodyLine.setData1(c.getString(4));
			mBodyLine.setData2(c.getString(5));
			mBodyLine.setData3(c.getString(6));
			mBodyLine.setData4(c.getString(7));
			PartList.add(mBodyLine);
			c.moveToNext();
		}
		c.close();
		return PartList;
	}

	public void insertRecordin(BodyLine mBodyLine) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_RecordIN, null);
		ContentValues values = new ContentValues();

		int count = gettableCount(Const.TABLE_RecordIN);

		values.put("_id", count + 1);
		values.put(Const.RECORDIN_COLUMN_RECORDID, mBodyLine.getRecordid());
		values.put("phone", mBodyLine.getPhone());
		values.put("num", mBodyLine.getNum());
		values.put("data1", mBodyLine.getData1());
		values.put("data2", mBodyLine.getData2());
		values.put("data3", mBodyLine.getData3());
		//0104?
		values.put("data4", "0");
		// data5 ---> piece
		values.put("data5", "0");
		values.put("data6", mBodyLine.getRemark());
		values.put("date", mBodyLine.getDate());

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		//for trans
		updataTransTableFlag(Const.TABLE_RecordIN, Const.KEY_TRANS_IN);
	}
	
	//for trans

	public float querySharedPrefs(String recordid) {
		String option = "recordid";
		Uri uri = getUri(Const.TYPE_Query_SUM, Const.TABLE_RecordIN, option);
		String selection = "recordid=? and data7 is not -1";
		String[] selectionArgs = new String[] { recordid };
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "SUM(data4)" }, selection,
					selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null || c.getCount() == 0)
			return 0;
		c.moveToFirst();
		float sum = c.getFloat(0);
		c.close();
		return sum;
	}
	
		private void updataTransTableFlag(String table, String flag) {
//			// check but not un
//			if (isUpdateUsed(Const.TABLE_RecordTodayIndex, "telphone", phonenum))
//				return;

			Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_SharedPrefs, null);
			// String where = "recordid" + "=" + recordid;

			ContentValues values = new ContentValues();
//			values.put(Const.SHARED_TRANS_LAST, count);
			values.put("value", flag);

			try {
				mResolver.update(uri, values, "key=?", new String[] { table });
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
	private void updataTransTableCount(String table, int count) {
//		// check but not un
//		if (isUpdateUsed(Const.TABLE_RecordTodayIndex, "telphone", phonenum))
//			return;

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_SharedPrefs, null);
		// String where = "recordid" + "=" + recordid;

		ContentValues values = new ContentValues();
		values.put(Const.SHARED_TRANS_LAST, count);
		values.put("value", Const.KEY_VALUE_TRUE);

		try {
			mResolver.update(uri, values, "key=?", new String[] { table });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void insertWHRecordin(String whrecorid, String deafID,
			String num, String piece, String spec,String pro) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_WHRecordIN, null);
		ContentValues values = new ContentValues();
		int count = gettableCount(Const.TABLE_WHRecordIN);
		Utils.showToast(mContext, String.valueOf(count));
		values.put("_id", count + 1);
		values.put(Const.RECORDIN_COLUMN_RECORDID, whrecorid);
		values.put("num", Integer.parseInt(num));
		values.put("data1", deafID);
//		values.put("data2", mBodyLine.getData2());
		values.put("data3", spec);
		values.put("data4", pro); //SUM
		// data5 ---> piece
		values.put("data5", piece);
//		values.put("data6", mBodyLine.getRemark());
		values.put("date", Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss"));
		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public void updateRecordRemark(int id,String remark) {

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordIN, null);
		// String where = "recordid" + "=" + recordid;

		ContentValues values = new ContentValues();
		values.put(Const.COLUMN_RECORDIN_REMARK, remark);

		try {
			mResolver.update(uri, values, "_id" + " IN (" + id + ")",
					null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void insertMultiRecord(ArrayList<BodyLine> mArrayList) {

		String option = "data1" + Const.KEY_DELIMITER + "data2"
				+ Const.KEY_DELIMITER + "data3" + Const.KEY_DELIMITER + "data4"
				+ Const.KEY_DELIMITER + "date";

		Uri uri = getUri(Const.TYPE_INSERT_MULTI, Const.TABLE_RecordIN, option);
		ContentValues values = new ContentValues();

		Iterator<BodyLine> mIterator = mArrayList.iterator();

		int cnt = 0;
		for (BodyLine mBodyLine : mArrayList) {
			values.put("data1" + Integer.toString(cnt), mBodyLine.getData1());
			values.put("data2" + Integer.toString(cnt), mBodyLine.getData2());
			values.put("data3" + Integer.toString(cnt), mBodyLine.getData3());
			values.put("data4" + Integer.toString(cnt), mBodyLine.getData4());
			values.put("date" + Integer.toString(cnt), mBodyLine.getDate());
			cnt++;
		}

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean deleteRecord(String selection) {
		Uri uri = getUri(Const.TYPE_DELETE, Const.TABLE_RecordIN, null);

		try {
			mResolver.delete(uri, "_id" + " IN (" + selection + ")", null);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public boolean deleteRecordfortest() {
		Uri uri = getUri(Const.TYPE_DELETE, Const.TABLE_Contacts, null);

		try {
			mResolver.delete(uri,null, null);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public boolean deleteRecordLOC(String selection,String recordid) {

		// check
		// if(isUpdateUsed(Const.TABLE_RecordIN, "recordid", recordid)) return;

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordIN, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("data7", -1);

		try {
			mResolver.update(uri, values, "_id" + " IN (" + selection + ")",
					null);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		checkrecordtodayzero(recordid);
		return true;
	}
	//getData7isNot1_Count
	public void checkrecordtodayzero(String recordid) {
		Uri uri = getUri(null, Const.TABLE_RecordIN, null);
		String selection = "recordid =? and data7 is not -1";
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "*" }, selection, new String[] { recordid },
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int loop_cnt = c.getCount();
		if (loop_cnt == 0) {
		uri = getUri(Const.TYPE_UPDATE, Const.TABLE_RecordToday, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("used", -1);

		try {
			mResolver.update(uri, values, "recordid=?",
					new String[] { recordid });
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
//		return loop_cnt;
	}
	
	public boolean deleteWHRecordLOC(String selection) {

		// check
		// if(isUpdateUsed(Const.TABLE_RecordIN, "recordid", recordid)) return;

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_WHRecordIN, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("data7", -1);

		try {
			mResolver.update(uri, values, "_id" + " IN (" + selection + ")",
					null);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public void updateWHRecordTodaySum(String recordid) {
		String selection = "recordid=?";
		String[] selectionArgs = new String[] { recordid };

		Uri uriupdate = getUri(Const.TYPE_UPDATE, Const.TABLE_WHRecordIndex, null);

		String sum = String.valueOf(queryWHRecordINSum(recordid));
		ContentValues values = new ContentValues();
		// data5
		values.put("data4", sum); //

		try {
			mResolver.update(uriupdate, values, selection, selectionArgs);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public float queryWHRecordINSum(String recordid) {
		String option = "recordid";
		Uri uri = getUri(Const.TYPE_Query_SUM, Const.TABLE_WHRecordIN, option);
		String selection = "recordid=? and data7 is not -1";
		String[] selectionArgs = new String[] { recordid };
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "SUM(data4)" }, selection,
					selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (c == null || c.getCount() == 0)
			return 0;
		c.moveToFirst();
		float sum = c.getFloat(0);
		c.close();
		return sum;
	}
	
	public int gettableCount(String table) {
		Uri uri = getUri(null, table, null);
		String selection = "";
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "*" }, selection, null,
					null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int loop_cnt = c.getCount();
		return loop_cnt;
	}

	public int gettableCount(String table, String column, String value) {
		Uri uri = getUri(null, table, null);
		String where = column + "=?";
		String[] selectionArgs = new String[] { value };
		Cursor c = null;
		try {
			c = mResolver.query(uri, new String[] { "*" }, where,
					selectionArgs, null);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int loop_cnt = c.getCount();
		return loop_cnt;
	}

	/*
	 * PupWin
	 */

	public ArrayList<String> getPupWinType() {
		ArrayList<String> mStrList = new ArrayList<String>();

		Uri uri = getUri(null, Const.TABLE_PupWinMage, null);
		String selection = "";
		Cursor c = null;
		int cnt = 0;
		try {
			c = mResolver.query(uri, new String[] { "*" }, selection, null,
					"TYPE ASC ");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// int index = 0;
		int loop_cnt = c.getCount();
		if (loop_cnt == 0) {
			mStrList.add("+");
			return mStrList;
		}
		c.moveToFirst();

		for (cnt = 0; cnt < loop_cnt; cnt++) {
			mStrList.add(c.getInt(2), c.getString(1));
			// if (c.getInt(PupWin_COLUMN_TYPE) > Const.DB_PUPPART_TYPE -1 )
			// break;
			// val[c.getInt(PupWin_COLUMN_TYPE)][c.getInt(PupWin_COLUMN_NUM)] =
			// c.getString(PupWin_COLUMN_NAME);
			c.moveToNext();
		}
		c.close();

		return mStrList;
	}


	public ArrayList<String> getPupContentNames(String typeID, boolean NineLimit) {
		int PupWin_COLUMN_NAME = 1;
		int limtcount = 0;
		if (NineLimit) {
//			String temp = new SharedPrefsData(mContext).getSharedData(Const.BOTTOM_POPWINDOW_HEIGHT);
//			if (Const.NO_DATA.endsWith(temp)) {
//				limtcount = Const.PUPWIN_CONTENT_NUM_12;
//			} else {
//				String temparr[] = temp.split(Const.KEY_DELIMITER);
//				limtcount = Integer.parseInt(temparr[1]);
//			}
			
			String temp = new DBOperator(mContext).getSharedDataValue(Const.KEY_POPHEIGHT);
				String temparr[] = temp.split(Const.KEY_DELIMITER);
				limtcount = Integer.parseInt(temparr[1]);
		}
		ArrayList<String> contName = new ArrayList<String>();

		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);
		String selection = "typeID=?";
		Cursor c = null;
		int cnt = 0;
		try {
			c = mResolver.query(uri, new String[] { "contID,contName" },
					selection, new String[] { typeID }, "_id ASC ");
		} catch (Exception e) {
			e.printStackTrace();
		}

		int loop_cnt = c.getCount();
		c.moveToFirst();

		for (cnt = 0; cnt < loop_cnt; cnt++) {
			if (NineLimit && cnt == limtcount)
				break;
			contName.add(c.getString(PupWin_COLUMN_NAME));
			c.moveToNext();
		}
		c.close();
		return contName;
	}
	public ArrayList<String> getPupContentNames(String typeID) {
		int PupWin_COLUMN_NAME = 1;
		ArrayList<String> contName = new ArrayList<String>();

		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);
		String selection = "typeID=?";
		Cursor c = null;
		int cnt = 0;
		try {
			c = mResolver.query(uri, new String[] { "contID,contName" },
					selection, new String[] { typeID }, "_id ASC ");
		} catch (Exception e) {
			e.printStackTrace();
		}

		int loop_cnt = c.getCount();
		c.moveToFirst();

		for (cnt = 0; cnt < loop_cnt; cnt++) {
			contName.add(c.getString(PupWin_COLUMN_NAME));
			c.moveToNext();
		}
		c.close();
		return contName;
	}
	
	//warhosleaf
	public ArrayList<String> getWarhosleafNames(String typeID) {
		int PupWin_COLUMN_NAME = 1;
		ArrayList<String> contName = new ArrayList<String>();

		Uri uri = getUri(null, Const.TABLE_warhosleaf, null);
		String selection = "typeID=?";
		Cursor c = null;
		int cnt = 0;
		try {
			c = mResolver.query(uri, new String[] { "contID,contName" },
					selection, new String[] { typeID }, "_id ASC ");
		} catch (Exception e) {
			e.printStackTrace();
		}

		int loop_cnt = c.getCount();
		c.moveToFirst();

		for (cnt = 0; cnt < loop_cnt; cnt++) {
			contName.add(c.getString(PupWin_COLUMN_NAME));
			c.moveToNext();
		}
		c.close();
		return contName;
	}
	public void initContacts(String[] str, int length) {

		String[] partStr = null;
		String option = "NAME" + Const.KEY_DELIMITER + "PHONE"
				+ Const.KEY_DELIMITER + "contractID";

		Uri uri = getUri(Const.TYPE_INSERT_MULTI, Const.TABLE_Contacts, option);
		/* ｸ・ｽﾂ嵭ﾄｿ､ﾎﾔOｶｨ */
		ContentValues values = new ContentValues();

		for (int cnt = 0; cnt < length; cnt++) {
			partStr = str[cnt].split(",");
			if (partStr.length < 3)
				continue;
			values.put("contractID" + Integer.toString(cnt), partStr[0]);
			values.put("NAME" + Integer.toString(cnt), partStr[1]);
			values.put("PHONE" + Integer.toString(cnt), partStr[2]);
		}

		try {
			/* Providerｺﾓｳｷ */
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			// ･ｨ･鬩`Иﾀ・ｽ
			e.printStackTrace();
		}
	}

	/*
	 * PupWinIN
	 */
	private String PupWinInCalc(String data1, String data2, String data3) {

		Float totalCalc = 0f;
		// if (data3 != null || data3 !="") {
		int intdata1 = Integer.parseInt(data1);
		int intdata2 = Integer.parseInt(data2);
		Float intdata3 = Float.parseFloat(data3);

		totalCalc = intdata3 * intdata1 * intdata2;
		// }

		return Float.toString(totalCalc);
	}

	// **********************************************************************************
	/*
	 * ********************************PupWinContent*****************************
	 * *******
	 */
	public void updatePupWinContentName(String typeId, String rownum,
			String name) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_PupWinContent, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("name", name);

		try {
			mResolver.update(uri, values, "typeId=? and rownum=?",
					new String[] { typeId, rownum });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getPupWinContentName(String typeId, String rownum) {
		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);

		String selection = "typeId=? and rownum=?";
		String[] selectionArgs = new String[] { typeId, rownum };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "name" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		return typeName;
	}

	public void clearPupWinContent() {
		/* URI､ﾎﾉ嵭ﾉ */
		Uri uri = getUri(Const.TYPE_DELETE, Const.TABLE_PupWinContent, null);

		try {
			/* Providerｺﾓｳｷ */
			mResolver.delete(uri, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	// ********************************************************************************
	/*
	 * PupWinMage
	 */

	public void updatePupWinMageName(String rownum, String name) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_PupWinMage, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("typeName", name);

		try {
			mResolver.update(uri, values, "typeID=?", new String[] { rownum });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void updateWarhosTreeName(String rownum, String name) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_warhostree, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("typeName", name);

		try {
			mResolver.update(uri, values, "typeID=?", new String[] { rownum });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void upWinMageTableName(String rownum, String name) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_PupWin, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("typeName", name);

		try {
			mResolver.update(uri, values, "typeID=?", new String[] { rownum });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updateRemarkName(String remarkID, String name) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_remark, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("typeName", name);

		try {
			mResolver.update(uri, values, "typeID=?", new String[] { remarkID });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	// public void changePopContPosition2(String typeId, int dragPosition, int
	// movPosition) {
	//
	// Uri uri = getUri(null, Const.TABLE_PupWinContent, null);
	// // String where = "typeID=? and contID=?";
	//
	// String where = "typeID="+typeId +" and _id" + "=" + dragPosition+1;
	//
	//
	// //
	// Cursor dragC = null;
	// try{
	// dragC = mResolver.query(uri,new String[] { "_id,contName" },where,new
	// String[]{typeId,dragPosition},null);
	// }catch (Exception e) {
	// e.printStackTrace();
	// }
	// dragC.moveToFirst();
	// int dragId = dragC.getInt(0);
	// String dragName = dragC.getString(1);
	// dragC.close();
	//
	// Cursor movC = null;
	// try{
	// movC = mResolver.query(uri,new String[] { "_id,contName" },where,new
	// String[]{typeId,movPosition},null);
	// }catch (Exception e) {
	// e.printStackTrace();
	// }
	// movC.moveToFirst();
	// int moveId = movC.getInt(0);
	// String movName = movC.getString(1);
	// movC.close();
	//
	//
	// //update
	// uri = getUri(Const.TYPE_UPDATE,Const.TABLE_PupWinContent,null);
	// ContentValues values = new ContentValues();
	// values.put("_id", moveId);
	//
	// try {
	// mResolver.update(uri, values, where, new String[]{typeId,dragPosition});
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	//
	//
	// values.clear();
	// values.put("_id", dragId);
	// try {
	// mResolver.update(uri, values, where, new String[]{typeId,movPosition});
	// } catch (SQLException e) {
	// e.printStackTrace();
	// }
	// String toast =
	// dragName+"("+dragPosition+")"+"交謐｢"+movName+"("+movPosition+")";
	// Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	// }

	public String getPupElementContID(String typeId, int _id) {
		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);
		String whereFrom = "typeID='" + typeId + "' and _id" + "=" + _id;

		//
		Cursor C = null;
		try {
			C = mResolver.query(uri, new String[] { "contID,contName" },
					whereFrom, null, null);
			if (C == null || C.getCount() == 0)
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		C.moveToFirst();
		String contID = C.getString(0);
		// String dragName = dragC.getString(1);
		C.close();
		return contID;
	}

	public String getWarhosElementContID(String typeId, int _id) {
		Uri uri = getUri(null, Const.TABLE_warhosleaf, null);
		String whereFrom = "typeID='" + typeId + "' and _id" + "=" + _id;

		//
		Cursor C = null;
		try {
			C = mResolver.query(uri, new String[] { "contID" },
					whereFrom, null, null);
			if (C == null || C.getCount() == 0)
				return null;
		} catch (Exception e) {
			e.printStackTrace();
		}
		C.moveToFirst();
		String contID = C.getString(0);
		// String dragName = dragC.getString(1);
		C.close();
		return contID;
	}
	//change element's _id  typeId and contID not change
	public void changePopContPosition(String typeId, int dragFromPosition,
			int dragToPosition) {
		// String from = Utils.toContID(dragPosition);
		// int to = desPosition;

		//
		// // int formPosition = dragPosition+1;
		// // int toPosition = desPosition+1;
		// Uri uri = getUri(null, Const.TABLE_PupWinContent, null);
		// String whereFrom = "typeID='"+typeId +"' and _id" + "=" +
		// dragFromPosition;
		//
		// //
		// Cursor dragC = null;
		// try{
		// dragC = mResolver.query(uri,new String[] { "contID,contName"
		// },whereFrom,null,null);
		// if (dragC == null || dragC.getCount() == 0) return;
		// }catch (Exception e) {
		// e.printStackTrace();
		// }
		// dragC.moveToFirst();
		// String dragFromContId = dragC.getString(0);
		// // String dragName = dragC.getString(1);
		// dragC.close();
		//
		// String whereTo = "typeID='"+typeId +"' and _id" + "=" +
		// dragToPosition;
		// Cursor movC = null;
		// try{
		// movC = mResolver.query(uri,new String[] { "contID,contName"
		// },whereTo,null,null);
		// if (movC == null || movC.getCount() == 0) return;
		// }catch (Exception e) {
		// e.printStackTrace();
		// }
		// movC.moveToFirst();
		// String dragToContID = movC.getString(0);
		// // String movName = movC.getString(1);
		// movC.close();

		String dragContID = getPupElementContID(typeId, dragFromPosition);
		String movContID = getPupElementContID(typeId, dragToPosition);
		// update
		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_PupWinContent, null);
		ContentValues values = new ContentValues();
		values.put("_id", dragToPosition);

		String DragTO = "typeID='" + typeId + "' and contID" + "= '"
				+ dragContID + "'";
		try {
			mResolver.update(uri, values, DragTO, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		values.clear();
		values.put("_id", dragFromPosition);
		String DragFrom = "typeID='" + typeId + "' and contID" + "= '"
				+ movContID + "'";
		try {
			mResolver.update(uri, values, DragFrom, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// String toast = dragName+"("+dragId+")"+"交謐｢"+movName+"("+desID+")";
		// Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	}

	public void changeWarhosContPosition(String dragFormName, String dragToName, int dragFromPosition,
			int dragToPosition) {

		String dragID = getDataLeafID(Const.TABLE_warhosleaf, dragFormName);
		String moveID = getDataLeafID(Const.TABLE_warhosleaf, dragToName);
		String dragarr[]= dragID.split(Const.KEY_DELIMITER_AND);
		String movearr[] = moveID.split(Const.KEY_DELIMITER_AND);
		// update
		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_warhosleaf, null);
		ContentValues values = new ContentValues();
		values.put("_id", dragToPosition);

		String DragTO = "typeID='" + dragarr[0] + "' and contID" + "= '"
				+ dragarr[1] + "'";
		try {
			mResolver.update(uri, values, DragTO, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}

		values.clear();
		values.put("_id", dragFromPosition);
		String DragFrom = "typeID='" + movearr[0] + "' and contID" + "= '"
				+ movearr[1] + "'";
		try {
			mResolver.update(uri, values, DragFrom, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		// String toast = dragName+"("+dragId+")"+"交謐｢"+movName+"("+desID+")";
		// Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
	}
	public String getPupWinMageName(String rownum) {
		Uri uri = getUri(null, Const.TABLE_PupWinMage, null);

		String selection = "rownum=?";
		String[] selectionArgs = new String[] { rownum };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "name" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		return typeName;
	}

	public void clearPupWinMage() {
		/* URI､ﾎﾉ嵭ﾉ */
		Uri uri = getUri(Const.TYPE_DELETE, Const.TABLE_PupWinMage, null);

		try {
			/* Providerｺﾓｳｷ */
			mResolver.delete(uri, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// public void insertPupWinMage(String name, String typeid) {
	public void insertPupWinMage() {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_PupWinMage, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		int count = gettableCount(Const.TABLE_PupWinMage);

		values.put("_id", count + 1);
		values.put("name", "+");
		values.put("rownum", count);
		values.put("typeId", "2013" + count + 1);
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/*
	 * public void setSqliteDB() { // Uri uri = getUri("insert", Const.T_TABLE,
	 * null); Delete();
	 * 
	 * String[] classNameStrings = mContext.getResources().getStringArray(
	 * R.array.DATA_TITLE); // String[] partStr; String option = "type" +
	 * Const.KEY_DELIMITER + "num"+ Const.KEY_DELIMITER + "name";
	 * 
	 * Uri uri = getUri(Const.TYPE_INSERT_MULTI, Const.TABLE_PupWin, option);
	 * 
	 * ContentValues values = new ContentValues(); for (int cnt = 0; cnt <
	 * classNameStrings.length; cnt++) { partStr =
	 * classNameStrings[cnt].split(","); values.put("type" +
	 * Integer.toString(cnt), partStr[0]); values.put("num"
	 * +Integer.toString(cnt), partStr[1]); values.put("name" +
	 * Integer.toString(cnt), partStr[2]); } try { mResolver.insert(uri,
	 * values); } catch (SQLException e) { e.printStackTrace(); } }
	 */
	public void Delete() {
		/* URI､ﾎﾉ嵭ﾉ */
		Uri uri = getUri(Const.TYPE_DELETE, Const.TABLE_warhosleaf, null);

		try {
			/* Providerｺﾓｳｷ */
			mResolver.delete(uri, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		uri = getUri(Const.TYPE_DELETE, Const.TABLE_warhostree, null);

		try {
			/* Providerｺﾓｳｷ */
			mResolver.delete(uri, null, null);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
	}

	public int getSqliteDB() {
		Uri uri = getUri(null, Const.TABLE_Contacts, null);

		String selection = "";
		int m = -1;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "*" }, selection, null,
					null);
			c.moveToFirst();
			m = c.getInt(1);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return m;
	}

	/*
	 * Manager
	 */
	public String getPupWinMageName(int typeID) {
		Uri uri = getUri(null, Const.TABLE_PupWinMage, null);

		String selection = "typeID=?";
		String[] selectionArgs = new String[] { Utils.toTypeID(typeID) };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "typeName" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		c.close();
		return typeName;
	}

	public String getWarhosTreeName(int typeID) {
		Uri uri = getUri(null, Const.TABLE_warhostree, null);

		String selection = "typeID=?";
		String[] selectionArgs = new String[] { Utils.toWarTypeID(typeID) };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "typeName" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		c.close();
		return typeName;
	}
	
	public String getWinMageName(int typeID) {
		Uri uri = getUri(null, Const.TABLE_PupWinMage, null);

		String selection = "typeID=?";
		String[] selectionArgs = new String[] { Utils.toWarTypeID(typeID) };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "typeName" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		c.close();
		return typeName;
	}
	
	public String getWarhosTreeName(String typeID) {
		Uri uri = getUri(null, Const.TABLE_warhostree, null);

		String selection = "typeID=?";
		String[] selectionArgs = new String[] { typeID };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "typeName" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		c.close();
		return typeName;
	}
	public String getRemarkName(int typeID) {
		Uri uri = getUri(null, Const.TABLE_remark, null);

		String selection = "typeID=?";
		String[] selectionArgs = new String[] { Utils.toRekTypeID(typeID) };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "typeName" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		c.close();
		return typeName;
	}
	
	public String getPupWinContentName(int mtypeId, int mContId) {
		Uri uri = getUri(null, Const.TABLE_PupWinContent, null);

		String selection = "typeID=? and contID=?";
		String[] selectionArgs = new String[] { Utils.toTypeID(mtypeId),
				Utils.toContID(mContId) };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "contName" }, selection,
					selectionArgs, null);
			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		c.close();
		return typeName;
	}

	public Boolean insertPupWinContent(int typeId, String contentName) {

		if (!chkDataLeafName(Const.TABLE_PupWinContent,contentName)) return false;
		
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_PupWinContent, null);
		ContentValues values = new ContentValues();

		// systime
		String strtime =  Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss");

		int count = gettableCount(Const.TABLE_PupWinContent, "typeId",
				Utils.toTypeID(typeId));

		values.put("_id", count + 1);
		values.put("typeID", Utils.toTypeID(typeId));
		values.put("contID", Utils.toContID(count));
		values.put("contName", contentName);
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//warhos
	public Boolean insertWarhosLeaf(int typeId, String contentName) {

		if (!chkDataLeafName(Const.TABLE_warhosleaf,contentName)) return false;
		
		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_warhosleaf, null);
		ContentValues values = new ContentValues();

		// systime
		String strtime =  Utils.systemFrmtTime("yyyy/MM/dd HH:mm:ss");

		int count = gettableCount(Const.TABLE_warhosleaf, "typeId",
				Utils.toWarTypeID(typeId));

		values.put("_id", count + 1);
		values.put("typeID", Utils.toWarTypeID(typeId));
		values.put("contID", Utils.toWarContID(count));
		values.put("contName", contentName);
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public String getDataLeafName(String table, String dataLeafID) {
		Uri uri = getUri(null, table, null);

		String selection = "typeID=? and contID =?";
		String[] ID = dataLeafID.split(Const.KEY_DELIMITER_AND);
		// test for mom
		if (ID.length<2) {
			ID = dataLeafID.split(Const.KEY_DELIMITER);
		}
		//test end
		String[] selectionArgs = new String[] {ID[0],ID[1]};
		Cursor c = null;
        String DataLeafName = null;
		try {
			c = mResolver.query(uri, new String[] { "contName" }, selection,
					selectionArgs, null);
			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			DataLeafName =c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			DataLeafName = Const.NO_DATA;
		}
		return DataLeafName;
	}
	
	
	public String getDataLeafID(String table,String dataLeafName) {
		Uri uri = getUri(null, table, null);

		String selection = "contName=?";
		String[] selectionArgs = new String[] {dataLeafName};
		Cursor c = null;
        String DataLeafID = null;
		try {
			c = mResolver.query(uri, new String[] { "typeID","contID" }, selection,
					selectionArgs, null);
			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			String typeID = c.getString(0);
			String contID = c.getString(1);
			DataLeafID = typeID +Const.KEY_DELIMITER_AND+ contID;
		} catch (Exception e) {
			// TODO: handle exception
			DataLeafID = Const.NO_DATA;
		}
		return DataLeafID;
	}
	
	public boolean chkDataLeafName(String table ,String dataLeafName) {
		String rtnStr = getDataLeafID(table,dataLeafName);
		if (Const.NO_DATA.equals(rtnStr)) 
            return true;
		return false;
	}
	
	public boolean updatePupWinContentName(int typeId, String oldname, String name) {

		// check
		if (!chkDataLeafName(Const.TABLE_PupWinContent,name)) return false;
		
		String dataLeafID = getDataLeafID(Const.TABLE_PupWinContent ,oldname);
		String temparr[] = dataLeafID.split(Const.KEY_DELIMITER_AND);
		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_PupWinContent, null);
		ContentValues values = new ContentValues();
		values.put("contName", name);

		try {
			mResolver.update(
					uri,
					values,
					"typeID=? and contID=?",
					new String[] { Utils.toTypeID(typeId),
							temparr[1] });
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	//warhos
	public boolean updateWarhosLeafName(int typeId, String oldname, String name) {

		// check
		if (!chkDataLeafName(Const.TABLE_warhosleaf,name)) return false;
		
		String dataLeafID = getDataLeafID(Const.TABLE_warhosleaf,oldname);
		String temparr[] = dataLeafID.split(Const.KEY_DELIMITER_AND);
		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_warhosleaf, null);
		ContentValues values = new ContentValues();
		values.put("contName", name);

		try {
			mResolver.update(
					uri,
					values,
					"typeID=? and contID=?",
					new String[] { Utils.toWarTypeID(typeId),
							temparr[1] });
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	public void insertPupWinMage(String typeName) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_PupWinMage, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		int count = gettableCount(Const.TABLE_PupWinMage);

		values.put("_id", count + 1);
		values.put("typeID", Utils.toTypeID(count));
		values.put("typeName", typeName);
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertWarhosTrees(String typeName) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_warhostree, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		int count = gettableCount(Const.TABLE_warhostree);

		values.put("_id", count + 1);
		values.put("typeID", Utils.toWarTypeID(count));
		values.put("typeName", typeName);
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void inWinMageTable(String typeName,String flag) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_warhostree, null);
		ContentValues values = new ContentValues();

//		// systime
//		long times = System.currentTimeMillis();
//		Date curdate = new Date(times);
//		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//		String strtime = format.format(curdate);

		String falgColumn = "data2";
		int count = gettableCount(Const.TABLE_warhostree,falgColumn,flag);

//		values.put("_id", count + 1);
		values.put("typeID", Utils.toWarTypeID(count));
		values.put("typeName", typeName);
		values.put(falgColumn, flag);
//		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertRemark(String remark) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_remark, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		int count = gettableCount(Const.TABLE_remark);

		values.put("_id", count + 1);
		values.put("typeID", Utils.toRekTypeID(count));
		values.put("typeName", remark);
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void insertSharedData(String key,String value) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_SharedPrefs, null);
		ContentValues values = new ContentValues();

		// systime
		long times = System.currentTimeMillis();
		Date curdate = new Date(times);
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		String strtime = format.format(curdate);

		int count = gettableCount(Const.TABLE_SharedPrefs);

		values.put("_id", count + 1);
		values.put("key", key);
		values.put("value", value);
		values.put("data1", strtime);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void insertSharedDataTrans(String key,String value) {

		Uri uri = getUri(Const.TYPE_INSERT, Const.TABLE_SharedPrefs, null);
		ContentValues values = new ContentValues();

		int count = gettableCount(Const.TABLE_SharedPrefs);

		values.put("_id", count + 1);
		values.put("key", key);
		values.put("value", value);
		values.put(Const.SHARED_TRANS_START, Const.KEY_TRANS_ZERO);
		values.put(Const.SHARED_TRANS_LAST, Const.KEY_TRANS_ZERO);

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void initAppData(String[] key, String[] value) {

		String option = "_id" + Const.KEY_DELIMITER + "key"
				+ Const.KEY_DELIMITER + "value";

		Uri uri = getUri(Const.TYPE_INSERT_MULTI, Const.TABLE_SharedPrefs, option);
		ContentValues values = new ContentValues();

		for (int cnt = 0; cnt < key.length; cnt++) {
			values.put("_id"+ Integer.toString(cnt), cnt+1);
			values.put("key"+ Integer.toString(cnt), key[cnt]);
			values.put("value"+ Integer.toString(cnt), value[cnt]);
		}

		try {
			mResolver.insert(uri, values);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	public void updateSharedData(String key, String value) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_SharedPrefs, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		values.put("value", value);

		try {
			mResolver.update(uri, values, "key =?", new String[] { key });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public void updateSharedDataTrans(String key, String value) {

		// check

		Uri uri = getUri(Const.TYPE_UPDATE, Const.TABLE_SharedPrefs, null);
		// String where = "recordid" + "=" + recordid;
		ContentValues values = new ContentValues();
		int count = gettableCount(key);
		values.put("data1",String.valueOf(count));
		values.put("value", value);

		try {
			mResolver.update(uri, values, "key =?", new String[] { key });
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public String getSharedDataValue(String key) {
		Uri uri = getUri(null, Const.TABLE_SharedPrefs, null);

		String selection = "key=?";
		String[] selectionArgs = new String[] { key };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { "value" }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		c.close();
		return typeName;
	}
	
	public String getSharedData(String key ,String vol) { //data  for selectd
		Uri uri = getUri(null, Const.TABLE_SharedPrefs, null);

		String selection = "key=?";
		String[] selectionArgs = new String[] { key };
		String typeName = null;
		Cursor c = null;

		try {
			c = mResolver.query(uri, new String[] { vol }, selection,
					selectionArgs, null);

			if (c == null || c.getCount() == 0)
				return Const.NO_DATA;
			c.moveToFirst();
			typeName = c.getString(0);
		} catch (Exception e) {
			// TODO: handle exception
			typeName = Const.NO_DATA;
		}
		c.close();
		return typeName;
	}
	
	private Uri getUri(String type, String table, String option) {
		android.net.Uri.Builder builder = baseUri.buildUpon();
		if (type != null)
			builder.appendQueryParameter("type", type);
		builder.appendQueryParameter("table", table);
		if (option != null)
			builder.appendQueryParameter("option", option);
		return builder.build();
	}

	public Uri getTableUri(String table) {
		android.net.Uri.Builder builder = baseUri.buildUpon();
		builder.appendQueryParameter("table", table);
		return builder.build();
	}
}
