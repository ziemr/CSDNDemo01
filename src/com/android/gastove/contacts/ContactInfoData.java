package com.android.gastove.contacts;

public class ContactInfoData {
	String name = null;
	String telphone = null;
	
	public void setData(int index,String data) {
		switch (index) {
		case 0 :
			 name = data;
			break;
		case 1 :
			telphone = data;;
			break;	
		}
	}
	
	public String getData(int index) {
		switch (index) {
		case 0 :
			return name;
		case 1 :
			return telphone;
		}
		return null;
	}
}
