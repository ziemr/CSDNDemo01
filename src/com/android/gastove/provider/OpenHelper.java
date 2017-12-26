package com.android.gastove.provider;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.android.gastove.util.Const;


public class OpenHelper extends SQLiteOpenHelper {

//        private final static int DBverson = 4;//  contactsgroup up
        private final static int DBverson = 5;
	public OpenHelper(Context context) {
		super(context, Const.DATABASE_NAME, null, DBverson);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		/**
		25          * 这个方法
		26          * 1、在第一次打开数据库的时候才会走
		27          * 2、在清除数据之后再次运行-->打开数据库，这个方法会走
		28          * 3、没有清除数据，不会走这个方法
		29          * 4、数据库升级的时候这个方法不会走
		30          */
        System.out.println("OpenHelper" + "onCreate()");
//        db.execSQL("DELETE TABLE PUPPart;");
        android.util.Log.d("OpenHelper" ,"onCreate()");
        db.execSQL(
        		"CREATE TABLE User(" +
        		"_id INTEGER,"+
        		"user STRING," +
                "password STRING," +
                "date STRING," +
                "permission STRING," +
                "data INTEGER" +
        		");");
        db.execSQL(//danger warning account!!!!!
        		"insert into User(" +
        		"_id,"+
        		"user," +
                "password," +
                "date," +
                "permission )" +
                "values(1,"+
                "'admin',"+
                "'110',"+
                "'2015/1/8',"+
                "'danger'"+
        		");");
        db.execSQL(
        		"insert into User(" +
        		"_id,"+
        		"user," +
                "password," +
                "date," +
                "permission )" +
                "values(2,"+
                "'admin',"+
                "'666888',"+
                "'2015/1/8',"+
                "'user'"+
        		");");
        
//        db.execSQL(
//        		"CREATE TABLE PupWinMage(" +
//        		"_id INTEGER,"+
//        		"name STRING," +
//                "rownum INTEGER," +
//                "data1 STRING," +
//                "data2 STRING," +
//        		"typeId STRING"+
//        		");");
//        db.execSQL(
//        		"CREATE TABLE PupWinContent(" +
//        		"_id INTEGER,"+
//                "typeId STRING," +
//                "rownum INTEGER," +
//        		"name STRING," +
//                "data1 STRING," +
//                "data2 STRING," +
//        		"contentId STRING"+
//        		");");
 //used Table

        //Type
        db.execSQL(
        		"CREATE TABLE PupWinMage(" +
        		"_id INTEGER,"+
        		"typeID STRING,"+
        		"typeName STRING," +
                "data1 STRING," +
                "data2 STRING" +
        		");");
        //Content
        db.execSQL(
        		"CREATE TABLE PupWinContent(" +
        		"_id INTEGER,"+
                "typeID STRING," +
                "contID STRING," +
        		"contName TEXT," +
                "data1 STRING," +
                "data2 STRING" +
        		");");
        //PupWin no use
        db.execSQL(
        		"CREATE TABLE PupWin(" +
        		"_id INTEGER,"+
                "type INTEGER," +
                "num INTEGER," +
        		"name STRING," +
                "data1 STRING," +
                "data2 STRING," +
        		"pupwinId INTEGER"+
        		");");
        //PupWinIN no use
        db.execSQL(
        		"CREATE TABLE PupWinIN(" +
        		"_id INTEGER,"+
                "typeId STRING," +
                "rownum INTEGER," +
                "data1 STRING," +
                "data2 STRING," +
                "data3 STRING," +
                "data4 STRING," +
                "data5 STRING," +
        		"date STRING"+
        		");");
//        db.execSQL(
//                "INSERT INTO PupWinMage(" +
//                    "_id," +
//                    "name," +
//                    "rownum," +
//                    "typeId"+
//                    ")" +
//                    " values (" +
//                    1  + Const.KEY_DELIMITER +       // 
//                    "+"+ Const.KEY_DELIMITER +       // 
//                    1  + Const.KEY_DELIMITER +       // 
//                    "20131" + 
//                   ");");
        
        
        db.execSQL(
        		"CREATE TABLE ContactsInfo(" +
        	    "_id INTEGER,"+
                "name STRING," +
                "telphone STRING," +
                "used INTEGER," +
                "data1 STRING," +
                "data2 STRING,"+
                "date  STRING,"+
                "contractId INTEGER" +
        		");");

        //RecordToday
        //
		//data1 --> Today date(2013/11/11)  -->today
		//data2 --> flag(1:show other:not show  -->used
        db.execSQL(
        		"CREATE TABLE RecordToday(" +
                "_id INTEGER,"+
                "recordid TEXT,"+
                "telphone STRING," +
                "name STRING," +
               // "num INTEGER,"+
                "today STRING," +
                "used STRING," +
                "pay STRING," +
                "data4 STRING," +
                "data5 STRING,"+
                "date STRING" +
        		");");
        
        db.execSQL(
        		"CREATE TABLE RecordTodayIndex(" +
                "_id INTEGER,"+
                "telphone STRING," +
               // "num INTEGER,"+
                "today STRING," +
                "used STRING," +
                "date STRING" +
        		");");
        
        db.execSQL(
        		"CREATE TABLE RecordIN(" +
                "_id INTEGER,"+
                "recordid TEXT,"+
                "phone STRING," +
                "num INTEGER,"+
                "data1 STRING," +
                "data2 STRING," +
                "data3 STRING," +
                "data4 STRING," +
                "data5 STRING,"+  //piece
                "date STRING," +
                "data6 STRING,"+  //no use
                "data7 STRING"+   //no use
        		");");
        
        db.execSQL(
        		"CREATE TABLE Calls(" +
                "_id INTEGER," +  //0
                "number TEXT," +  //1
                "date STRING," +  //2
                "used INTEGER," +  //3
                
                //data1 --> recordid
                "data1 TEXT," +
                "data2 TEXT," +
                "name TEXT" +
        		");");
        
      //warhos Type
        db.execSQL(
        		"CREATE TABLE whtree(" +
        		"_id INTEGER,"+
        		"typeID STRING,"+
        		"typeName TEXT," +
                "data1 STRING," +
                "data2 STRING" +
        		");");
        //warhos Content
        db.execSQL(
        		"CREATE TABLE whleaf(" +
        		"_id INTEGER,"+
                "typeID STRING," +
                "contID STRING," +
        		"contName TEXT," +
                "data1 STRING," +
                "data2 STRING" +
        		");");
        
        
        db.execSQL(
        		"CREATE TABLE WHRecordIN(" +
                "_id INTEGER,"+  //!
                "recordid TEXT,"+//!
                "phone STRING," +
                "num INTEGER,"+   //sum num                
                "data1 STRING," + //xiang
                "data2 STRING," + //gui ge
                "data3 STRING," + //type cont id
                "data4 STRING," +
                "data5 STRING,"+  //piece
                "date STRING," +  //date
                "data6 STRING,"+  //remark
                "data7 STRING"+   //delete 
        		");");
        
        db.execSQL(
        		"CREATE TABLE whrecordindex(" +
                "_id INTEGER,"+
                "recordid TEXT,"+
                "telphone STRING," +
                "name STRING," +
               // "num INTEGER,"+
                "today STRING," +
                "used STRING," +
                "pay STRING," +
                "data4 STRING," +
                "data5 STRING,"+
                "date STRING" +
        		");");
        //remark
        db.execSQL(
        		"CREATE TABLE Remark(" +
        		"_id INTEGER,"+
        		"typeID STRING,"+
        		"typeName STRING," +
                "data1 STRING," +
                "data2 STRING" +
        		");");
        
        db.execSQL(
        		"CREATE TABLE SharedPrefs(" +
        		"_id INTEGER,"+
        		"key STRING,"+
        		"value STRING," +
                "data1 STRING," +
                "data2 STRING" +
        		");");
        
        db.execSQL(//app init 
        		"insert into SharedPrefs(" +
        		"_id,"+
        		"key," +
                "value )" +
                "values(1,"+
                "'init',"+
                "'0'"+
        		");");
        
        
        //
		db.execSQL(
        		"CREATE TABLE ContactsGroup(" +
        		"_id INTEGER,"+
        		"groupID STRING,"+
        		"groupName STRING," +
        		"groupPhone STRING," +
                "data1 STRING," +
                "data2 STRING" +
                "data3 STRING" +
                "data4 STRING" +
        		");");
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		/**
		37          * 1、第一次创建数据库的时候，这个方法不会走
		38          * 2、清除数据后再次运行(相当于第一次创建)这个方法不会走
		39          * 3、数据库已经存在，而且版本升高的时候，这个方法才会调用
		40          */
		System.out.println("OpenHelper" + "onUpgrade()");
		android.util.Log.d("OpenHelper" ,"onUpgrade()");
		//version up versionCode 3->4  versionName 1.2 -> 1.3  for contactgroup
		db.execSQL(
        		"CREATE TABLE DATACHANGE(" +
        		"_id INTEGER,"+
        		"gastable STRING,"+
        		"change STRING," +
        		"start STRING," +
                "buck STRING," +
                "data1 STRING" +
                "data2 STRING" +
                "data3 STRING" +
                "data4 STRING" +
        		");");
		
//		db.execSQL("DROP TABLE IF EXISTS ContactsInfo");
//		if (newVersion > oldVersion) {
		if (false) {
			db.execSQL(
	        		"CREATE TABLE SharedPrefs(" +
	        		"_id INTEGER,"+
	        		"key STRING,"+
	        		"value STRING," +
	                "data1 STRING," +
	                "data2 STRING" +
	        		");");
	        
	        db.execSQL(//app init 
	        		"insert into SharedPrefs(" +
	        		"_id,"+
	        		"key," +
	                "value )" +
	                "values(1,"+
	                "'init',"+
	                "'0'"+
	        		");");
	      //remark
	        db.execSQL(
	        		"CREATE TABLE Remark(" +
	        		"_id INTEGER,"+
	        		"typeID STRING,"+
	        		"typeName STRING," +
	                "data1 STRING," +
	                "data2 STRING" +
	        		");");
	        
	        
	        db.execSQL(
	        		"CREATE TABLE WHRecordIN(" +
	                "_id INTEGER,"+  //!
	                "recordid TEXT,"+//!
	                "phone STRING," +
	                "num INTEGER,"+   //sum num                
	                "data1 STRING," + //xiang
	                "data2 STRING," + //gui ge
	                "data3 STRING," + //type cont id
	                "data4 STRING," +
	                "data5 STRING,"+  //piece
	                "date STRING," +  //date
	                "data6 STRING,"+  //remark
	                "data7 STRING"+   //delete 
	        		");");
	        
	        db.execSQL(
	        		"CREATE TABLE whrecordindex(" +
	                "_id INTEGER,"+
	                "recordid TEXT,"+
	                "telphone STRING," +
	                "name STRING," +
	               // "num INTEGER,"+
	                "today STRING," +
	                "used STRING," +
	                "pay STRING," +
	                "data4 STRING," +
	                "data5 STRING,"+
	                "date STRING" +
	        		");");
		}
      
	}

}
