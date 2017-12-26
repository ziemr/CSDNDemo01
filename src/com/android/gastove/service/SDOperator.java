package com.android.gastove.service;

/* Checks if external storage is available for read and write */
//public boolean isExternalStorageWritable() {
//    String state = Environment.getExternalStorageState();
//    if (Environment.MEDIA_MOUNTED.equals(state)) {
//        return true;
//    }
//    return false;
//}
// 
///* Checks if external storage is available to at least read */
//public boolean isExternalStorageReadable() {
//    String state = Environment.getExternalStorageState();
//    if (Environment.MEDIA_MOUNTED.equals(state) ||
//        Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//        return true;
//    }
//    return false;
//}
//public class SDOperator {
////	public void read() {
//	FileOutputStream fileOutputStream;
//	public void ss(){
//		
//		}
//	}
//	ObjectOutputStream objectOutputStream;
//	FileInputStream fileInputStream;
//	ObjectInputStream objectInputStream;
//	try {
//	    //存入数据
//	    File file = new File(Environment.getExternalStorageDirectory().toString() + File.separator +"ContactBackup"+File.separator + "backup.dat");
//	    if (!file.getParentFile().exists()) {
//	        file.getParentFile().mkdirs();
//	    }
//	 
//	    if (!file.exists()) {
//	        file.createNewFile();
//	    }
//	 
//	    fileOutputStream= new FileOutputStream(file.toString());
//	    objectOutputStream= new ObjectOutputStream(fileOutputStream);
//	    Object studentsArrayList;
//		objectOutputStream.writeObject(studentsArrayList);
//	     
//	     //取出数据
//	    fileInputStream = new FileInputStream(file.toString());
//	    objectInputStream = new ObjectInputStream(fileInputStream);
////	    ArrayList<Student> savedArrayList =(ArrayList<Student>) objectInputStream.readObject();
//	 
////	    for (int i = 0; i < savedArrayList.size(); i++) {
////	        System.out.println("取出的数据:" + savedArrayList.get(i).toString());
////	    }
//	 
//	} catch (Exception e) {
//	    // TODO: handle exception
//	}finally{
//	    if (objectOutputStream!=null) {
//	        try {
//	            objectOutputStream.close();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//	    if (fileOutputStream!=null) {
//	        try {
//	            fileOutputStream.close();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//	    if (objectInputStream!=null) {
//	        try {
//	            objectInputStream.close();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//	    if (fileInputStream!=null) {
//	        try {
//	            fileInputStream.close();
//	        } catch (IOException e) {
//	            e.printStackTrace();
//	        }
//	    }
//	}
//	}
	
	/////////////////////
//	private final BroadcastReceiver broadcastRec = new BroadcastReceiver()  
	 
//	{
//	 
//	　　@Override
//	 
//	　　public void onReceive(Context context, Intent intent) {
//	 
//	　　if(intent.getAction().equals("android.intent.action.MEDIA_MOUNTED"))//SD  
//	 
//	卡已经成功挂载
//	 
//	　　{
//	 
//	　　imagepath =  
//	 
//	android.os.Environment.getExternalStorageDirectory();//你的SD卡路径
//	 
//	　　}else 
//	 
//	if(intent.getAction().equals("android.intent.action.MEDIA_REMOVED")//各种未挂载状态
//	 
//	　　||intent.getAction().equals("android.intent.action.ACTION_MEDIA_UNMOUNTED")
//	 
//	　　||intent.getAction().equals("android.intent.action.ACTION_MEDIA_BAD_REMOVAL"))
//	 
//	　　{
//	 
//	　　imagepath = android.os.Environment.getDataDirectory();//你的本地路径
//	 
//	　　}
//	 
//	　　}
//	 
//	　　};
	 
//在IntentFilter中选择你要监听的行为
	 
//	　　IntentFilter intentFilter = new 
//	 
//	IntentFilter(Intent.ACTION_MEDIA_MOUNTED);
//	 
//	　　intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);
//	 
//	　　intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);
//	 
//	　　//intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
//	 
//	　　intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);
//	 
//	　　//intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);
//	 
//	　　//intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);
//	 
//	　　intentFilter.addDataScheme("file");
//	 
//	　　registerReceiver(broadcastRec, intentFilter);//注册监听函数
//	 
//	　　unregisterReceiver(broadcastRec);//使用完注销广播监听函数
