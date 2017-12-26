package com.android.gastove.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class DateInfo {
		private SimpleDateFormat sdf;

		public DateInfo() {
			sdf = new SimpleDateFormat("yyyy/MM/dd");
		}
		
		//获取当前日期
		public String getToday() {
			Date d = new Date();
			String date = sdf.format(d);
			return date;
		}
		
	        //截取本月
		public String getCurrentMonth() {
			Date d = new Date();
			String t = sdf.format(d);
			String m = t.substring(4, 6);
			return m;
		}
		
	        //截取本年  
		public String getCurrentYear() {
			Date d = new Date();
			String t = sdf.format(d);
			String y = t.substring(0, 4);
			return y;
		}
		
		
		//获取昨天的日期
		public String getDateOfYesterday() {
			Calendar c = Calendar.getInstance();
			long t = c.getTimeInMillis();
			long l = t - 24 * 3600 * 1000;
			Date d = new Date(l);
			String s = sdf.format(d);
			return s;
		}
		
		//获取昨天的日期
		public String getDateOfFarday(int farday) {
			Calendar c = Calendar.getInstance();
			long t = c.getTimeInMillis();
			long l = t - farday * 24 * 3600 * 1000;
			Date d = new Date(l);
			String s = sdf.format(d);
			return s;
		}
		
		//获取上个月的第一天
		public String getFirstDayOfLastMonth() {
			String str = "";  
			Calendar lastDate = Calendar.getInstance();
			lastDate.set(Calendar.DATE,1); //set the date to be 1
			lastDate.add(Calendar.MONTH,-1);//reduce a month to be last month
//			lastDate.add(Calendar.DATE,-1);//reduce one day to be the first day of last month
				         
			str=sdf.format(lastDate.getTime()); 
			return str;
		}
		
		// 获取上个月的最后一天
		public String getLastDayOfLastMonth() {
			String str = "";
			Calendar lastDate = Calendar.getInstance();
			lastDate.set(Calendar.DATE, 1);// 
			lastDate.add(Calendar.MONTH, -1);//
			lastDate.roll(Calendar.DATE, -1);// 
			str = sdf.format(lastDate.getTime());
			return str;
		}
		
		//获取本月第一天
		public String getFirstDayOfThisMonth() {
			String str = "";
			Calendar lastDate = Calendar.getInstance();
			lastDate.set(Calendar.DATE,1);//
//			lastDate.add(Calendar.MONTH,-1);//
//			lastDate.add(Calendar.DATE,-1);//
				         
			str=sdf.format(lastDate.getTime()); 
			return str;
		}
		
		//获取本月最后一天
		public String getLastDayOfThisMonth() {
			String str = "";  
			Calendar lastDate = Calendar.getInstance();
			lastDate.set(Calendar.DATE,1);//
			lastDate.add(Calendar.MONTH,1);//
			lastDate.add(Calendar.DATE,-1);//
				         
			str = sdf.format(lastDate.getTime()); 
			return str;
		}
		
	        //判断闰年
		public static boolean isLeapYear(int year) {
			if ((year % 4 == 0 && year % 100 != 0) || year % 400 == 0) {
				return true;
			}
			return false;
		}

}
